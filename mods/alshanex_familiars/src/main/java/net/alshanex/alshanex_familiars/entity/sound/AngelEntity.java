package net.alshanex.alshanex_familiars.entity.sound;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Objects;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AngelEntity extends Entity implements GeoEntity {
   private int age;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("idle");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AngelEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         if (this.age > 300) {
            MagicManager.spawnParticles(
               this.level, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY() + 1.0, this.getZ(), 50, 0.2, 1.25, 0.2, 0.08, false
            );
            this.discard();
         }

         if (this.age <= 300 && this.age % 5 == 0) {
            MagicManager.spawnParticles(this.level, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY(), this.getZ(), 5, 0.2, 1.25, 0.2, 0.08, false);
         }
      }

      this.age++;
   }

   public void onAddedToLevel() {
      if (!this.level().isClientSide) {
         this.stopMinecraftAmbientMusic();
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)AFSoundRegistry.ANGELIC_HARP.get(), SoundSource.MUSIC, 1.0F, 1.0F);
      }

      super.onAddedToLevel();
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.age = compound.getInt("Age");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.age);
   }

   private void stopMinecraftAmbientMusic() {
      if (!this.level().isClientSide && this.level().getServer() != null) {
         for (ServerPlayer player : Objects.requireNonNull(this.level().getServer()).getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.game"), SoundSource.MUSIC));
            player.connection.send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.creative"), SoundSource.MUSIC));
            player.connection.send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.menu"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.day"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.night"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.hills"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.water"), SoundSource.MUSIC));
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "idle", 0, this::idlePredicate));
   }

   private PlayState idlePredicate(AnimationState event) {
      event.getController().setAnimation(this.idle);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
