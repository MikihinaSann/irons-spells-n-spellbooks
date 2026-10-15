package net.acetheeldritchking.discerning_the_eldritch.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.network.AddShaderEffectPacket;
import net.acetheeldritchking.aces_spell_utils.network.RemoveShaderEffectPacket;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ConjureGaolerSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "conjure_gaoler");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(600.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{Utils.stringTruncation(this.getGaolerHealth(spellLevel, null), 2)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getGaolerDamage(spellLevel, null), 2)})
      );
   }

   public ConjureGaolerSpell() {
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 25;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 500;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean requiresLearning() {
      return false;
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_GAOLER_SUMMON;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.WARDEN_HEARTBEAT);
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return entity instanceof Player player && ASUtils.hasCurio(player, (Item)ItemRegistries.KINGS_EFFIGY.get()) ? 2 : 1;
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)
         && ASUtils.hasCurio(serverPlayer, (Item)ItemRegistries.KINGS_EFFIGY.get())) {
         super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      }
   }

   public ICastDataSerializable getEmptyCastData() {
      return new SummonedEntitiesCastData();
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      double radius = 15.0;

      for (LivingEntity targets : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius))) {
         targets.addEffect(new MobEffectInstance(MobEffects.DARKNESS, this.getCastTime(spellLevel), 1, false, false, false));
      }

      entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, this.getCastTime(spellLevel), 1, false, false, false));
      entity.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.PORTENT_EFFECT, 100, 1, false, false, false));
      if (entity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(
            serverPlayer, new AddShaderEffectPacket("discerning_the_eldritch", "shaders/grayscale_darker.json"), new CustomPacketPayload[0]
         );
      }

      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      int summonTimer = 1200;
      SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
      new BlockPos((int)entity.getX(), (int)entity.getY(), (int)entity.getZ());
      Vec3 lookDir = entity.getLookAngle();
      Vec3 flatDir = new Vec3(lookDir.x, 0.0, lookDir.z).normalize();
      double offset = 6.0;
      Vec3 spawnPos = entity.position().subtract(flatDir.scale(offset));
      if (!(entity instanceof Player player && ASUtils.hasCurio(player, (Item)ItemRegistries.KINGS_EFFIGY.get()))) {
         this.spawnGaoler(spawnPos.x, spawnPos.y, spawnPos.z, entity, level, summonTimer, spellLevel, summonedEntitiesCastData, true);
      } else if (!recasts.hasRecastForSpell(this)) {
         this.spawnGaoler(spawnPos.x, spawnPos.y, spawnPos.z, entity, level, summonTimer, spellLevel, summonedEntitiesCastData, false);
         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTimer, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      if (entity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(serverPlayer, new RemoveShaderEffectPacket(), new CustomPacketPayload[0]);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnGaoler(
      double x, double y, double z, LivingEntity caster, Level level, int timer, int spellLevel, SummonedEntitiesCastData castData, boolean isFeral
   ) {
      GaolerEntity gaoler = new GaolerEntity(level, caster, true);
      gaoler.setPos(x, y, z);
      gaoler.setOldPosAndRot();
      gaoler.lookAt(caster, 1.0F, 1.0F);
      gaoler.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getGaolerDamage(spellLevel, caster));
      gaoler.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getGaolerHealth(spellLevel, caster));
      gaoler.setHealth(gaoler.getMaxHealth());
      GaolerEntity event = (GaolerEntity)((SpellSummonEvent)NeoForge.EVENT_BUS.post(new SpellSummonEvent(caster, gaoler, this.spellId, spellLevel)))
         .getCreature();
      level.addFreshEntity(event);
      SummonManager.initSummon(caster, event, timer, castData);
      if (isFeral) {
         SummonManager.removeSummon(gaoler);
      }
   }

   private float getGaolerDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 4.5F;
   }

   private float getGaolerHealth(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 20.5F;
   }
}
