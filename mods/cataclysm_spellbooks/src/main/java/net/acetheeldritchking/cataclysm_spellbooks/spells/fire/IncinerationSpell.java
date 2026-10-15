package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IncinerationSpell extends AbstractIgnisSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "incineration");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.range", new Object[]{Utils.stringTruncation(spellLevel, 2)}),
         Component.translatable(
            "ui.cataclysm_spellbooks.incineration_duration", new Object[]{Utils.stringTruncation(this.spellPower(spellLevel, caster) / 10.0F, 2)}
         )
      );
   }

   public IncinerationSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 30;
      this.baseManaCost = 120;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.IGNIS_AMBIENT.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.FLAME_BURST.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_SPIT_ANIMATION;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float MAX_HEALTH = entity.getMaxHealth();
      float baseHealth = entity.getHealth();
      double percent = baseHealth / MAX_HEALTH * 100.0F;
      double casterX = entity.getX();
      double casterZ = entity.getZ();
      double casterHeadY = entity.getY() + 1.0;
      int standOnYPos = Mth.floor(entity.getY()) - 2;
      float yawRadians = (float)Math.toRadians(90.0F + entity.getYRot());

      for (int i = 0; i < this.spellPower(spellLevel, entity) / 10.0F; i++) {
         double d2 = 2.25 * (i + 1);
         int j2 = (int)(1.5F * i);
         double casterXYaw = casterX + Mth.cos(yawRadians) * d2;
         double casterZYaw = casterZ + Mth.sin(yawRadians) * d2;
         if (percent <= 50.0) {
            this.spawnFlameStrike(casterXYaw, casterZYaw, standOnYPos, casterHeadY, yawRadians, spellLevel * 20, j2, j2, level, 1.0F, true, entity, spellLevel);
         } else {
            this.spawnFlameStrike(casterXYaw, casterZYaw, standOnYPos, casterHeadY, yawRadians, spellLevel * 20, j2, j2, level, 1.0F, false, entity, spellLevel);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnFlameStrike(
      double x,
      double z,
      double minY,
      double maxY,
      float rotation,
      int duration,
      int wait,
      int delay,
      Level level,
      float radius,
      boolean isSoul,
      LivingEntity caster,
      int spellLevel
   ) {
      BlockPos pos = new BlockPos((int)x, (int)maxY, (int)z);
      boolean flag = false;
      double d0 = 0.0;

      do {
         BlockPos pos1 = pos.below();
         BlockState blockState = level.getBlockState(pos1);
         if (blockState.isFaceSturdy(level, pos1, Direction.UP)) {
            if (!level.isEmptyBlock(pos)) {
               BlockState blockState1 = level.getBlockState(pos);
               VoxelShape voxelShape = blockState1.getCollisionShape(level, pos);
               if (!voxelShape.isEmpty()) {
                  d0 = voxelShape.max(Axis.Y);
               }
            }

            flag = true;
            break;
         }

         pos = pos.below();
      } while (pos.getY() >= Mth.floor(minY) - 1);

      if (flag) {
         level.addFreshEntity(
            new Flame_Strike_Entity(
               level,
               x,
               pos.getY() + d0,
               z,
               rotation,
               duration,
               wait,
               delay,
               radius,
               this.getDamage(spellLevel, caster),
               this.getHPDamage(spellLevel),
               isSoul,
               caster
            )
         );
         if (isSoul) {
         }

         level.addFreshEntity(
            new Flame_Strike_Entity(
               level,
               x,
               pos.getY() + d0,
               z,
               rotation,
               duration,
               wait,
               delay,
               radius,
               (float)(this.getDamage(spellLevel, caster) * 1.5),
               this.getHPDamage(spellLevel),
               isSoul,
               caster
            )
         );
      }
   }

   private float spellPower(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return (float)(this.getSpellPower(spellLevel, caster) / 1.5);
   }

   private float getHPDamage(int spellLevel) {
      return spellLevel * 10 / 100.0F;
   }
}
