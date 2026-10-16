package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.weapon_type.WeaponType;
import com.userofbricks.expanded_combat.entity.ECFallingBlockEntity;
import com.userofbricks.expanded_combat.init.ECEnchantments;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SlamWeaponItem extends ECWeaponItem {
   private final int extraSlamLvl;

   public SlamWeaponItem(Material material, WeaponType weapon, Properties properties, int extraSlamLvl) {
      super(material, weapon, properties.component(ItemDataComponents.HITS_TILL_SLAM, 0));
      this.extraSlamLvl = extraSlamLvl;
   }

   public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
      ItemEnchantments itemenchantments = (ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      ResourceLocation location = ResourceLocation.tryParse(enchantment.getRegisteredName());
      return itemenchantments.getLevel(enchantment) + (location != null && location == ECEnchantments.GROUND_SLAM.location() ? this.extraSlamLvl : 0);
   }

   @Override
   public boolean hurtEnemy(ItemStack weapon, LivingEntity target, LivingEntity attacker) {
      super.hurtEnemy(weapon, target, attacker);
      int hitsTillSlam = (Integer)weapon.getOrDefault(ItemDataComponents.HITS_TILL_SLAM, 0);
      hitsTillSlam++;
      int slamLevel = weapon.getEnchantmentLevel(
         attacker.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(ECEnchantments.GROUND_SLAM)
      );
      if (hitsTillSlam >= 10 - slamLevel / 2 && slamLevel > 0) {
         weapon.set(ItemDataComponents.HITS_TILL_SLAM, 0);
         int range = 2 + Math.round(slamLevel / 3.0F);

         for (int rDistance = 2; rDistance <= range; rDistance++) {
            groundSlam(1.25F, rDistance, 1.0F, 0.0F, true, 0.1F, attacker, slamLevel);
         }
      }

      return true;
   }

   protected static void groundSlam(
      float spreadarc, int distance, float maxy, float vec, boolean grab, float airborne, @NotNull LivingEntity attacker, int slamLevel
   ) {
      float dmg = (float)(attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.9);
      dmg += dmg * 0.05F * (slamLevel - 1);
      double perpFacing = attacker.yBodyRot * (Math.PI / 180.0);
      double facingAngle = perpFacing + (Math.PI / 2);
      int hitY = Mth.floor(attacker.getBoundingBox().minY - 0.5);
      double spread = Math.PI * spreadarc;
      int arcLen = Mth.ceil(distance * spread);
      double minY = attacker.getY() - 1.0;
      double maxY = attacker.getY() + maxy;

      for (int i = 0; i < arcLen; i++) {
         double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
         double vx = Math.cos(theta);
         double vz = Math.sin(theta);
         double px = attacker.getX() + vx * distance + vec * Math.cos((attacker.yBodyRot + 90.0F) * Math.PI / 180.0);
         double pz = attacker.getZ() + vz * distance + vec * Math.sin((attacker.yBodyRot + 90.0F) * Math.PI / 180.0);
         float factor = 1.0F - distance / 12.0F;
         int hitX = Mth.floor(px);
         int hitZ = Mth.floor(pz);
         BlockPos pos = new BlockPos(hitX, hitY, hitZ);
         BlockPos abovePos = new BlockPos(pos).above();
         BlockState block = attacker.level().getBlockState(pos);
         BlockState blockAbove = attacker.level().getBlockState(abovePos);
         if (block != Blocks.AIR.defaultBlockState() && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
            ECFallingBlockEntity fallingBlockEntity = new ECFallingBlockEntity(attacker.level(), hitX + 0.5, hitY + 1.125, hitZ + 0.5, block, 10);
            fallingBlockEntity.push(0.0, 0.075 + attacker.getRandom().nextGaussian() * 0.07, 0.0);
            attacker.level().addFreshEntity(fallingBlockEntity);
         }

         AABB selection = new AABB(px - 0.5, minY, pz - 0.5, px + 0.5, maxY, pz + 0.5);

         for (LivingEntity entity : attacker.level().getEntitiesOfClass(LivingEntity.class, selection)) {
            if (!attacker.isAlliedTo(entity) && entity != attacker) {
               DamageSources damageSources = attacker.damageSources();
               boolean flag = entity.hurt(attacker instanceof Player player ? damageSources.playerAttack(player) : damageSources.mobAttack(attacker), dmg);
               if (flag) {
                  if (grab) {
                     double magnitude = -4.0;
                     double x = vx * (1.0F - factor) * magnitude;
                     double y = 0.0;
                     if (entity.onGround()) {
                        y += 0.15;
                     }

                     double z = vz * (1.0F - factor) * magnitude;
                     entity.setDeltaMovement(entity.getDeltaMovement().add(x, y, z));
                  } else {
                     entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, airborne * distance + attacker.level().random.nextDouble() * 0.15, 0.0));
                  }
               }
            }
         }
      }
   }
}
