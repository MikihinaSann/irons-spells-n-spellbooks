package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi.SummonedJiangshiEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;

@AutoSpellConfig
public class JiangshiInvokeSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "jiangshi_invoke");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(7)
      .setCooldownSeconds(150.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{this.getJiangshiHealth(spellLevel, caster)}),
         Component.translatableEscape("ui.irons_spellbooks.damage", new Object[]{this.getJiangshiDamage(spellLevel, caster)})
      );
   }

   public JiangshiInvokeSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
      this.baseManaCost = 30;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAISE_DEAD_START.value());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAISE_DEAD_FINISH.value());
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

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      float radius = 2.055F;
      PlayerRecasts recast = playerMagicData.getPlayerRecasts();
      if (!recast.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();

         for (int i = 0; i < 3; i++) {
            SummonedJiangshiEntity jiangshi = new SummonedJiangshiEntity(world, entity, true);
            jiangshi.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getJiangshiDamage(spellLevel, entity));
            jiangshi.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getJiangshiHealth(spellLevel, entity));
            jiangshi.setHealth(jiangshi.getMaxHealth());
            jiangshi.setDropChance(EquipmentSlot.HEAD, 0.0F);
            jiangshi.skipDropExperience();
            jiangshi.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(jiangshi.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
            float yrot = 2.0936668F * i + entity.getYRot() * (float) (Math.PI / 180.0);
            Vec3 spawn = Utils.moveToRelativeGroundLevel(world, entity.getEyePosition().add(new Vec3(radius * Mth.cos(yrot), 0.0, radius * Mth.sin(yrot))), 10);
            jiangshi.setPos(spawn.x, spawn.y, spawn.z);
            jiangshi.setYRot(entity.getYRot());
            jiangshi.setOldPosAndRot();
            SummonedJiangshiEntity creature = (SummonedJiangshiEntity)((SpellSummonEvent)NeoForge.EVENT_BUS
                  .post(new SpellSummonEvent(entity, jiangshi, this.spellId, spellLevel)))
               .getCreature();
            world.addFreshEntity(creature);
            SummonManager.initSummon(entity, creature, summonTime, summonedEntitiesCastData);
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recast.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getJiangshiDamage(int spellLevel, LivingEntity summoner) {
      return 2 + spellLevel;
   }

   private float getJiangshiHealth(int spellLevel, LivingEntity summoner) {
      return 10 + spellLevel * 5;
   }

   public AnimationHolder getCastStartAnimation() {
      return MFTESpellAnimations.ANIMATION_INVOKING;
   }
}
