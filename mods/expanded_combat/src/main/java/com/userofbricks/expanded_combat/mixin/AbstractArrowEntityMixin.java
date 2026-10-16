package com.userofbricks.expanded_combat.mixin;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.item.QuiverItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@Mixin({AbstractArrow.class})
public abstract class AbstractArrowEntityMixin extends Projectile {
   @Shadow
   protected boolean inGround;
   @Shadow
   public int shakeTime;
   @Shadow
   public Pickup pickup;

   protected AbstractArrowEntityMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Shadow
   protected abstract ItemStack getPickupItem();

   @Shadow
   public abstract boolean isNoPhysics();

   @Inject(
      method = {"playerTouch"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void playerTouch(Player player, CallbackInfo callback) {
      if (!this.level().isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0 && ExpandedCombat.CONFIG.enableQuivers) {
         ItemStack pickupItem = this.getPickupItem();
         if (this.pickup == Pickup.ALLOWED && this.getPickupItem().is(ItemTags.ARROWS)) {
            SlotResult quiverSlot = CuriosApi.getCuriosInventory(player)
               .flatMap(curiosInventory -> curiosInventory.findFirstCurio(item -> item.getItem() instanceof QuiverItem))
               .orElse(null);
            if (quiverSlot == null) {
               return;
            }

            ItemStack quiverStack = quiverSlot.stack();
            QuiverItem quiverItem = (QuiverItem)quiverStack.getItem();
            BundleContents bundlecontents = (BundleContents)quiverStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            QuiverItem.MutableQuiverContents bundlecontents$mutable = new QuiverItem.MutableQuiverContents(
               bundlecontents, quiverItem.getMaterial().offense().quiverStacks()
            );
            int i = bundlecontents$mutable.tryInsert(pickupItem);
            if (i > 0) {
               quiverStack.set(DataComponents.BUNDLE_CONTENTS, bundlecontents$mutable.toImmutable());
               quiverItem.playInsert(player);
               player.awardStat(Stats.ITEM_PICKED_UP.get(pickupItem.getItem()), 1);
               this.discard();
               callback.cancel();
            }
         }
      }
   }
}
