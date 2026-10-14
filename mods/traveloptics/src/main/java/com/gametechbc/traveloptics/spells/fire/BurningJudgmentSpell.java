package com.gametechbc.traveloptics.spells.fire;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.api.utils.CurioUtils;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

@AutoSpellConfig
public class BurningJudgmentSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "burning_judgment");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(18.0)
      .build();

   public BurningJudgmentSpell() {
      super();
      this.manaCostPerLevel = 40;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 50;
      this.baseManaCost = 40;
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

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.JUDGMENT_START;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.JUDGMENT_FINISH;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.FLAME_BURST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.SWORD_STOMP.get());
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      SphereParticleManager.spawnParticles(level, entity, 1, ParticleTypes.FLAME, ParticleDirection.INWARD, 3.0);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      int flameStrikes = (int)this.getFlameStrikeCount(spellLevel);
      int duration = (int)this.getDuration(spellLevel);
      int damage = (int)this.getDamage(spellLevel, caster);
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(damage, 2)}),
         Component.translatable("ui.traveloptics.flame_strikes", new Object[]{flameStrikes}),
         Component.translatable("ui.traveloptics.flame_strike_length", new Object[]{Utils.timeFromTicks(duration, 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int flameStrikes = (int)this.getFlameStrikeCount(spellLevel);
      int duration = (int)this.getDuration(spellLevel);
      double casterX = caster.getX();
      double casterZ = caster.getZ();
      double casterHeadY = caster.getY() + 1.0;
      int standOnYPos = Mth.floor(caster.getY()) - 2;
      float yawRadians = (float)Math.toRadians(90.0F + caster.getYRot());
      boolean isSoulStrike = caster.getHealth() <= caster.getMaxHealth() * 0.5
         || CurioUtils.getWearingCurio(caster, (Item)TOItems.AZURE_IGNITION_BRACELET.get());

      for (int i = 0; i < flameStrikes; i++) {
         double d2 = 2.25 * (i + 1);
         int j2 = (int)(1.5F * i);
         double casterXYaw = casterX + Mth.cos(yawRadians) * d2;
         double casterZYaw = casterZ + Mth.sin(yawRadians) * d2;
         this.spawnFlameStrike(casterXYaw, casterZYaw, standOnYPos, casterHeadY, yawRadians, duration, j2, j2, level, 1.0F, isSoulStrike, caster, spellLevel);
      }

      SphereParticleManager.spawnParticles(level, caster, 50, ParticleTypes.FLAME, ParticleDirection.OUTWARD, 3.0);
      ScreenShake_Entity.ScreenShake(level, caster.position(), 12.0F, 0.03F, 10, 20);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
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
      float damage = this.getDamage(spellLevel, caster);
      if (CurioUtils.getWearingCurio(caster, (Item)TOItems.AZURE_IGNITION_BRACELET.get())) {
         damage += 2.0F;
      }

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
         level.addFreshEntity(new Flame_Strike_Entity(level, x, pos.getY() + d0, z, rotation, duration, wait, delay, radius, damage, 0.0F, isSoul, caster));
      }
   }

   private float getFlameStrikeCount(int spellLevel) {
      return 1 + spellLevel;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 3.0F;
   }

   private float getDuration(int spellLevel) {
      return spellLevel * 40;
   }
}
