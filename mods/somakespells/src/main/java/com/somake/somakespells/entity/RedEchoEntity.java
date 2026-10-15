package com.somake.somakespells.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RedEchoEntity extends AbstractSpellCastingMob {
   private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(RedEchoEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Boolean> SLIM_MODEL = SynchedEntityData.defineId(RedEchoEntity.class, EntityDataSerializers.BOOLEAN);
   private static final int MAX_DUEL_TICKS = 2400;
   private static final double MAX_OWNER_DISTANCE_SQR = 2304.0;
   private int duelTicks;

   public RedEchoEntity(EntityType<? extends AbstractSpellCastingMob> type, Level level) {
      super(type, level);
      this.setPersistenceRequired();
      this.xpReward = 0;
   }

   public RedEchoEntity(ServerLevel level, ServerPlayer owner) {
      this(level, owner, owner.position().add(horizontalForward(owner).scale(7.0)));
   }

   public RedEchoEntity(ServerLevel level, ServerPlayer owner, Vec3 spawn) {
      this((EntityType<? extends AbstractSpellCastingMob>)ModEntityTypes.RED_ECHO.get(), level);
      this.entityData.set(OWNER_ID, Optional.of(owner.getUUID()));
      this.entityData.set(SLIM_MODEL, usesSlimModel(owner));
      this.setPos(spawn.x, spawn.y, spawn.z);
      this.setTarget(owner);
      this.getMagicData().setMana((float)this.getAttributeValue(AttributeRegistry.MAX_MANA));
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.MAX_HEALTH, 120.0)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.35)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.MOVEMENT_SPEED, 0.27)
         .add(AttributeRegistry.MAX_MANA, 10000.0)
         .add(AttributeRegistry.MANA_REGEN, 8.0)
         .add(AttributeRegistry.SPELL_POWER, 0.9)
         .add(AttributeRegistry.SPELL_RESIST, 1.15)
         .add(AttributeRegistry.CAST_TIME_REDUCTION, 1.35)
         .add(AttributeRegistry.CASTING_MOVESPEED, 1.0);
   }

   public static boolean startDuel(ServerPlayer player) {
      RedEchoEntity.DuelCheck check = checkDuelStart(player);
      if (!check.allowed()) {
         player.displayClientMessage(Component.translatable(check.messageKey()), true);
         return false;
      } else {
         Vec3 spawn = findDuelSpawn(player).orElse(null);
         if (spawn == null) {
            return false;
         } else {
            RedEchoEntity echo = new RedEchoEntity(player.serverLevel(), player, spawn);
            if (!player.serverLevel().addFreshEntity(echo)) {
               player.displayClientMessage(Component.translatable("message.somakespells.crimson_reflection.invalid_area"), true);
               return false;
            } else {
               echo.spawnDuelStart(player.serverLevel());
               player.displayClientMessage(Component.translatable("message.somakespells.red_echo.duel_started"), true);
               return true;
            }
         }
      }
   }

   public static boolean hasActiveDuelFor(ServerPlayer player) {
      return hasActiveDuel(player);
   }

   public static RedEchoEntity.DuelCheck checkDuelStart(ServerPlayer player) {
      if (player.hasEffect(ModMobEffects.RED_SOUL)) {
         return new RedEchoEntity.DuelCheck(false, "message.somakespells.crimson_reflection.red_soul_active");
      } else if (hasActiveDuel(player)) {
         return new RedEchoEntity.DuelCheck(false, "message.somakespells.crimson_reflection.duel_active");
      } else if (!player.isAlive() || player.isPassenger() || player.isFallFlying() || !player.onGround()) {
         return new RedEchoEntity.DuelCheck(false, "message.somakespells.crimson_reflection.invalid_state");
      } else {
         return findDuelSpawn(player).isEmpty()
            ? new RedEchoEntity.DuelCheck(false, "message.somakespells.crimson_reflection.invalid_area")
            : new RedEchoEntity.DuelCheck(true, "");
      }
   }

   private static boolean hasActiveDuel(ServerPlayer player) {
      for (ServerLevel level : player.getServer().getAllLevels()) {
         for (Entity entity : level.getAllEntities()) {
            if (entity instanceof RedEchoEntity echo && echo.isOwnedBy(player) && echo.isAlive()) {
               return true;
            }
         }
      }

      return false;
   }

   private static Optional<Vec3> findDuelSpawn(ServerPlayer player) {
      ServerLevel level = player.serverLevel();
      Vec3 forward = horizontalForward(player);

      for (double distance : new double[]{7.0, 6.0, 8.0, 5.0, 9.0}) {
         Vec3 candidate = player.position().add(forward.scale(distance));

         for (int yOffset = 2; yOffset >= -3; yOffset--) {
            Vec3 spawn = new Vec3(candidate.x, player.getY() + yOffset, candidate.z);
            AABB bounds = ((EntityType)ModEntityTypes.RED_ECHO.get()).getSpawnAABB(spawn.x, spawn.y, spawn.z);
            if (level.getWorldBorder().isWithinBounds(bounds) && level.noCollision(bounds)) {
               BlockPos below = BlockPos.containing(spawn).below();
               if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                  return Optional.of(spawn);
               }
            }
         }
      }

      return Optional.empty();
   }

   public static boolean cancelDuel(ServerPlayer player, boolean notify) {
      List<RedEchoEntity> echoes = new ArrayList<>();

      for (ServerLevel level : player.getServer().getAllLevels()) {
         for (Entity entity : level.getAllEntities()) {
            if (entity instanceof RedEchoEntity echo && echo.isOwnedBy(player)) {
               echoes.add(echo);
            }
         }
      }

      echoes.forEach(Entity::discard);
      boolean removedRedSoul = player.removeEffect(ModMobEffects.RED_SOUL);
      if (notify && (!echoes.isEmpty() || removedRedSoul)) {
         player.displayClientMessage(Component.literal(removedRedSoul ? "Red Soul desativado." : "Duelo com Red Echo cancelado."), true);
      }

      return !echoes.isEmpty() || removedRedSoul;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector
         .addGoal(
            2,
            new WizardAttackGoal(this, 1.25, 28, 52)
               .setSpells(
                  List.of(
                     (AbstractSpell)ModSpells.PROCESSION_OF_SOULS.get(),
                     (AbstractSpell)ModSpells.PROCESSION_OF_SOULS.get(),
                     (AbstractSpell)ModSpells.SPIRAL_OF_RUIN.get(),
                     (AbstractSpell)ModSpells.SOUL_REPRISAL.get(),
                     (AbstractSpell)ModSpells.SPECTRAL_RONDO.get(),
                     (AbstractSpell)ModSpells.SOULFALL_JUDGMENT.get()
                  ),
                  List.of((AbstractSpell)ModSpells.SOUL_BASTION.get()),
                  List.of((AbstractSpell)ModSpells.SPECTRAL_RONDO.get()),
                  List.of()
               )
               .setSpellQuality(0.45F, 0.75F)
               .setAllowFleeing(true)
         );
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(OWNER_ID, Optional.empty());
      builder.define(SLIM_MODEL, false);
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel) {
         ServerPlayer owner = this.getOwnerPlayer();
         if (owner != null && owner.isAlive() && owner.level() == this.level() && !(this.distanceToSqr(owner) > 2304.0) && ++this.duelTicks <= 2400) {
            if (this.getTarget() != owner) {
               this.setTarget(owner);
            }
         } else {
            this.failDuel(owner);
         }
      }
   }

   public boolean canAttack(LivingEntity target) {
      return this.isOwner(target) && super.canAttack(target);
   }

   public boolean hurt(DamageSource source, float amount) {
      Entity attacker = source.getEntity();
      return !this.isOwner(attacker) ? false : super.hurt(source, amount);
   }

   public void die(DamageSource damageSource) {
      if (!this.level().isClientSide && !this.dead) {
         ServerPlayer owner = this.getOwnerPlayer();
         if (owner != null && this.isOwner(damageSource.getEntity())) {
            owner.addEffect(new MobEffectInstance(ModMobEffects.RED_SOUL, 7200, 0, false, false, false));
            owner.displayClientMessage(Component.literal("Red Echo derrotado. Red Soul despertou."), true);
            if (this.level() instanceof ServerLevel level) {
               this.particle("soul_explosion_red")
                  .ifPresent(particle -> level.sendParticles(particle, this.getX(), this.getY() + 1.0, this.getZ(), 8, 0.75, 1.0, 0.75, 0.06));
               this.playLegendarySound(level, "soul_shield_smash", 1.0F, 0.7F);
            }
         }
      }

      super.die(damageSource);
   }

   public boolean isAlliedTo(Entity entity) {
      return entity instanceof RedEchoEntity other && this.getOwnerId().equals(other.getOwnerId());
   }

   public Optional<UUID> getOwnerId() {
      return (Optional<UUID>)this.entityData.get(OWNER_ID);
   }

   public boolean usesSlimModel() {
      return (Boolean)this.entityData.get(SLIM_MODEL);
   }

   private static boolean usesSlimModel(ServerPlayer owner) {
      for (Property property : owner.getGameProfile().getProperties().get("textures")) {
         try {
            String decoded = new String(Base64.getDecoder().decode(property.value()), StandardCharsets.UTF_8);
            JsonObject textures = JsonParser.parseString(decoded).getAsJsonObject().getAsJsonObject("textures");
            if (textures != null && textures.has("SKIN")) {
               JsonObject skin = textures.getAsJsonObject("SKIN");
               if (skin.has("metadata")) {
                  return "slim".equalsIgnoreCase(skin.getAsJsonObject("metadata").get("model").getAsString());
               }

               return false;
            }
         } catch (RuntimeException var6) {
         }
      }

      return Math.floorMod(owner.getUUID().hashCode(), 18) < 9;
   }

   private boolean isOwner(Entity entity) {
      return entity != null && ((Optional<UUID>)this.entityData.get(OWNER_ID)).filter(entity.getUUID()::equals).isPresent();
   }

   private boolean isOwnedBy(ServerPlayer player) {
      return this.isOwner(player);
   }

   private ServerPlayer getOwnerPlayer() {
      return ((Optional<UUID>)this.entityData.get(OWNER_ID))
         .<Player>map(id -> this.level().getPlayerByUUID(id))
         .filter(ServerPlayer.class::isInstance)
         .map(ServerPlayer.class::cast)
         .orElse(null);
   }

   private void spawnDuelStart(ServerLevel level) {
      this.particle("ghostly_soul_red")
         .ifPresent(particle -> level.sendParticles(particle, this.getX(), this.getY() + 1.0, this.getZ(), 36, 0.9, 1.2, 0.9, 0.06));
      this.playLegendarySound(level, "ominous_windup", 1.0F, 0.7F);
   }

   private void failDuel(ServerPlayer owner) {
      if (owner != null) {
         owner.displayClientMessage(Component.literal("O Red Echo escapou. O duelo falhou."), true);
      }

      if (this.level() instanceof ServerLevel level) {
         this.particle("ghostly_soul_red")
            .ifPresent(particle -> level.sendParticles(particle, this.getX(), this.getY() + 1.0, this.getZ(), 22, 0.7, 0.9, 0.7, 0.05));
      }

      this.discard();
   }

   private Optional<ParticleOptions> particle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   private void playLegendarySound(ServerLevel level, String path, float volume, float pitch) {
      Optional<SoundEvent> sound = BuiltInRegistries.SOUND_EVENT.getOptional(LegendaryMonstersCompat.id(path));
      level.playSound(null, this.blockPosition(), sound.orElse((SoundEvent)SoundEvents.SOUL_ESCAPE.value()), SoundSource.HOSTILE, volume, pitch);
   }

   private static Vec3 horizontalForward(LivingEntity entity) {
      Vec3 look = entity.getLookAngle();
      Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
      return horizontal.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : horizontal.normalize();
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("SlimModel", this.usesSlimModel());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(SLIM_MODEL, compound.getBoolean("SlimModel"));
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   public record DuelCheck(boolean allowed, String messageKey) {
   }
}
