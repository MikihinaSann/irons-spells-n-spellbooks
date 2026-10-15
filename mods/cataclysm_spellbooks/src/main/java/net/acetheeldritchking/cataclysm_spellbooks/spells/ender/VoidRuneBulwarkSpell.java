package net.acetheeldritchking.cataclysm_spellbooks.spells.ender;

import com.github.L_Ender.cataclysm.entity.projectile.Void_Rune_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoidRuneBulwarkSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "void_bulwark");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(50.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.ring_count", new Object[]{this.getRings(spellLevel)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public VoidRuneBulwarkSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
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
      this.spawnVoidRuneCircle(level, entity, spellLevel);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnVoidRune(Level level, double x, double z, double minY, double maxY, double rotation, int delay, LivingEntity caster, int spellLevel) {
      BlockPos blockPos = new BlockPos((int)x, (int)maxY, (int)z);
      boolean flag = false;
      double d0 = 0.0;

      do {
         BlockPos blockPos1 = blockPos.below();
         BlockState blockState = level.getBlockState(blockPos1);
         if (blockState.isFaceSturdy(level, blockPos1, Direction.UP)) {
            if (!level.isEmptyBlock(blockPos)) {
               BlockState blockState1 = level.getBlockState(blockPos);
               VoxelShape voxelShape = blockState1.getCollisionShape(level, blockPos);
               if (!voxelShape.isEmpty()) {
                  d0 = voxelShape.max(Axis.Y);
               }
            }

            flag = true;
            break;
         }

         blockPos = blockPos.below();
      } while (blockPos.getY() >= Mth.floor(minY) - 1);

      if (flag) {
         Void_Rune_Entity voidRune = new Void_Rune_Entity(level, x, blockPos.getY() + d0, z, (float)rotation, delay, this.getDamage(spellLevel, caster), caster);
         level.addFreshEntity(voidRune);
      }
   }

   private void spawnVoidRuneCircle(Level level, LivingEntity caster, int spellLevel) {
      double casterX = caster.getX();
      double casterY = caster.getY();
      double casterZ = caster.getZ();
      double casterHeadY = casterY + 1.0;
      int casterYPosition = Mth.floor(casterY - 3.0);
      int numRings = spellLevel;
      double count = 1.0;
      int entitiesPerRing = Mth.clamp((int)this.getSpellPower(spellLevel, caster), 1, 50);
      int warmUp = 0;

      for (int rings = 0; rings < numRings; rings++) {
         double radius = count + rings * 1.5;
         int entitiesInRing = entitiesPerRing + rings * 4;
         int increaseWarmUp = warmUp + rings * 5;

         for (int i = 0; i < entitiesInRing; i++) {
            double angle = (Math.PI * 2) * i / entitiesInRing;
            double x = casterX + Mth.cos((float)angle) * radius;
            double z = casterZ + Mth.sin((float)angle) * radius;
            this.spawnVoidRune(level, x, z, casterYPosition, casterHeadY, angle, increaseWarmUp, caster, spellLevel);
         }
      }
   }

   private int getRings(int spellLevel) {
      return spellLevel;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) / 2.0F;
   }
}
