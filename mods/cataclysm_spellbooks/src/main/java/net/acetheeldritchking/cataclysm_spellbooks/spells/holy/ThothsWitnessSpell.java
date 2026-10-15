package net.acetheeldritchking.cataclysm_spellbooks.spells.holy;

import com.github.L_Ender.cataclysm.client.particle.Options.StormParticleOptions;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.spells.CastResult.Type;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedPhantomRemnant;
import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

public class ThothsWitnessSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "thoths_witness");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(600.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.cataclysm_spellbooks.remnant_count", new Object[]{spellLevel}));
   }

   public ThothsWitnessSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 100;
      this.baseManaCost = 650;
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

   public CastResult canBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player) {
      if (castSource == CastSource.SCROLL) {
         return new CastResult(
            Type.FAILURE,
            Component.translatable("ui.cataclysm_spellbooks.thoths_witness_scroll_failure", new Object[]{this.getDisplayName(player)})
               .withStyle(ChatFormatting.RED)
         );
      } else {
         return !player.getItemBySlot(EquipmentSlot.HEAD).is((Item)ItemRegistries.PHARAOH_MAGE_HELMET.get())
               && !player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.PHARAOH_MAGE_CHESTPLATE.get())
               && !player.getItemBySlot(EquipmentSlot.LEGS).is((Item)ItemRegistries.PHARAOH_MAGE_LEGGINGS.get())
               && !player.getItemBySlot(EquipmentSlot.FEET).is((Item)ItemRegistries.PHARAOH_MAGE_BOOTS.get())
            ? new CastResult(
               Type.FAILURE,
               Component.translatable("ui.cataclysm_spellbooks.thoths_witness_armor_failure", new Object[]{this.getDisplayName(player)})
                  .withStyle(ChatFormatting.RED)
            )
            : super.canBeCastedBy(spellLevel, castSource, playerMagicData, player);
      }
   }

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_MALEVOLENT_HAND_SIGN;
   }

   public AnimationHolder getCastFinishAnimation() {
      return ASSpellAnimations.ANIMATION_MALEVOLENT_HAND_SIGN_RELEASE;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.BELL_BLOCK);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.REMNANT_ROAR.get());
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      EarthquakeAoe aoe = new EarthquakeAoe(level);
      aoe.moveTo(entity.position());
      aoe.setOwner(entity);
      aoe.setCircular();
      aoe.setRadius(10.0F);
      aoe.setDuration(this.getCastTime(spellLevel));
      aoe.setDamage(0.0F);
      aoe.setSlownessAmplifier(0);
      level.addFreshEntity(aoe);
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      float random = 0.04F;
      float r = 0.89F + entity.getRandom().nextFloat() * random;
      float g = 0.85F + entity.getRandom().nextFloat() * random;
      float b = 0.69F + entity.getRandom().nextFloat() * random * 1.5F;
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 2.75F + entity.getRandom().nextFloat() * 0.6F, 3.75F + entity.getRandom().nextFloat() * 0.6F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 2.5F + entity.getRandom().nextFloat() * 0.45F, 3.0F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 2.25F + entity.getRandom().nextFloat() * 0.45F, 2.25F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 1.25F + entity.getRandom().nextFloat() * 0.45F, 1.25F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      if (entity.tickCount % 10 == 0) {
         ASUtils.spawnParticlesInRing(16, 1.25F, 2.25F, 3.25F, 0.5F, 0.1F, entity, (ParticleOptions)ModParticle.DUST_BLAST.get());
      }

      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
      super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
         super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      }
   }

   public ICastDataSerializable getEmptyCastData() {
      return new SummonedEntitiesCastData();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
         int summonTimer = 1200;

         for (int i = 0; i < spellLevel; i++) {
            Vec3 vec = entity.getEyePosition();
            double randomNearbyX = vec.x + entity.getRandom().nextGaussian() * 3.0;
            double randomNearbyZ = vec.z + entity.getRandom().nextGaussian() * 3.0;
            this.spawnPhantomRemnant(randomNearbyX, vec.y, randomNearbyZ, entity, level, summonTimer, spellLevel, summonedEntitiesCastData);
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTimer, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnPhantomRemnant(
      double x, double y, double z, LivingEntity caster, Level level, int summonTimer, int spellLevel, SummonedEntitiesCastData castData
   ) {
      SummonedPhantomRemnant kobolediator = new SummonedPhantomRemnant(level, caster);
      SummonedPhantomRemnant event = (SummonedPhantomRemnant)((SpellSummonEvent)NeoForge.EVENT_BUS
            .post(new SpellSummonEvent(caster, kobolediator, this.spellId, spellLevel)))
         .getCreature();
      kobolediator.moveTo(x, y, z);
      level.addFreshEntity(event);
      SummonManager.initSummon(caster, event, summonTimer, castData);
   }
}
