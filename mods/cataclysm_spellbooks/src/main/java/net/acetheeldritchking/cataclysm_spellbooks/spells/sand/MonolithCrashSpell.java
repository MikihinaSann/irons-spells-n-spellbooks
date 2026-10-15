package net.acetheeldritchking.cataclysm_spellbooks.spells.sand;

import com.github.L_Ender.cataclysm.entity.projectile.Ancient_Desert_Stele_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MonolithCrashSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "monolith_crash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(CSSchoolRegistry.SAND_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.windmill_rings", new Object[]{spellLevel}),
         Component.translatable("ui.cataclysm_spellbooks.windmill_amount", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 0)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public MonolithCrashSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
      this.baseManaCost = 50;
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.spawnMonolithWindmill(
         spellLevel, (int)this.getSpellPower(spellLevel, entity), 2.0, 0.75, 0.6, entity.getY(), 1, entity, level, this.getDamage(spellLevel, entity)
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnMonolithWindmill(
      int numofBranches,
      int particlesPerBranch,
      double initialRadius,
      double radiusIncrement,
      double curveFactor,
      double spawnY,
      int delay,
      LivingEntity caster,
      Level level,
      float damage
   ) {
      float angleIncrement = (float)((Math.PI * 2) / numofBranches);

      for (int branch = 0; branch < numofBranches; branch++) {
         float baseAngle = angleIncrement * branch;

         for (int i = 0; i < particlesPerBranch; i++) {
            double currentRadius = initialRadius + i * radiusIncrement;
            float currentAngle = (float)(baseAngle + i * angleIncrement / initialRadius + i * curveFactor);
            double offsetX = currentRadius * Math.cos(currentAngle);
            double offsetZ = currentRadius * Math.sin(currentAngle);
            double spawnX = caster.getX() + offsetX;
            double spawnZ = caster.getZ() + offsetZ;
            int d1 = delay * (i + 1);
            this.spawnMonoliths(spawnX, spawnY, spawnZ, currentAngle, d1, caster, level, damage);
         }
      }
   }

   private void spawnMonoliths(double x, double y, double z, float rotation, int delay, LivingEntity caster, Level level, float damage) {
      BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
      double d0 = 0.0;

      do {
         BlockPos pos1 = pos.above();
         BlockState blockState = level.getBlockState(pos1);
         if (blockState.isFaceSturdy(level, pos1, Direction.DOWN)) {
            if (!level.isEmptyBlock(pos)) {
               BlockState blockState1 = level.getBlockState(pos);
               VoxelShape shape = blockState1.getCollisionShape(level, pos);
               if (!shape.isEmpty()) {
                  d0 = shape.max(Axis.Y);
               }
            }
            break;
         }

         pos = pos.above();
      } while (pos.getY() < Math.min(level.getMaxBuildHeight(), caster.getBlockY() + 10));

      Ancient_Desert_Stele_Entity monolith = new Ancient_Desert_Stele_Entity(level, x, pos.getY() + d0 - 3.0, z, rotation, delay, damage, caster);
      level.addFreshEntity(monolith);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 2.0F;
   }
}
