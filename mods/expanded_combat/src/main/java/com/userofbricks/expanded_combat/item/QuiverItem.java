package com.userofbricks.expanded_combat.item;

import com.mojang.datafixers.util.Pair;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.client.renderer.QuiverRenderer;
import com.userofbricks.expanded_combat.init.DataAttachments;
import com.userofbricks.expanded_combat.init.ECKeyRegistry;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import com.userofbricks.expanded_combat.network.server.PacketIntAttachment;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.math.Fraction;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class QuiverItem extends BundleItem implements ICurioItem, IMaterialItem {
   public final QuiverItem.Layer[] QUIVER_TEXTURE_LAYERS;
   public final Material material;

   public QuiverItem(Properties properties, Material material) {
      this(properties, material, new QuiverItem.Layer());
   }

   public QuiverItem(Properties properties, Material material, QuiverItem.Layer... layers) {
      super(properties.component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).component(ItemDataComponents.COOL_DOWN, 0).stacksTo(1));
      this.QUIVER_TEXTURE_LAYERS = layers;
      this.material = material;
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      top.theillusivec4.curios.api.type.capability.ICurioItem.super.curioTick(slotContext, stack);
      LivingEntity livingEntity = slotContext.entity();
      if (livingEntity.level().isClientSide()) {
         int countdownTicks = (Integer)stack.getOrDefault(ItemDataComponents.COOL_DOWN, 0);
         if (countdownTicks > 0) {
            stack.set(ItemDataComponents.COOL_DOWN, countdownTicks - 1);
         } else if (ECKeyRegistry.cycleQuiverLeft.isDown() && countdownTicks == 0) {
            this.sycleArrows(livingEntity, stack, false);
            stack.set(ItemDataComponents.COOL_DOWN, 5);
         } else if (ECKeyRegistry.cycleQuiverRight.isDown() && countdownTicks == 0) {
            this.sycleArrows(livingEntity, stack, true);
            stack.set(ItemDataComponents.COOL_DOWN, 5);
         }
      }
   }

   public void sycleArrows(LivingEntity livingEntity, ItemStack stack, boolean forward) {
      BundleContents contents = (BundleContents)stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      List<ItemStack> items = (List<ItemStack>)contents.items();
      if (!items.isEmpty()) {
         int arrowSlot = (Integer)livingEntity.getData(DataAttachments.ARROW_SLOT);
         arrowSlot += forward ? 1 : -1;
         if (arrowSlot >= items.size()) {
            arrowSlot = 0;
         }

         if (arrowSlot < 0) {
            arrowSlot = items.size() - 1;
         }

         livingEntity.setData(DataAttachments.ARROW_SLOT, arrowSlot);
         if (livingEntity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(
               serverPlayer, new PacketIntAttachment(serverPlayer.getId(), DataAttachments.ARROW_SLOT.get(), arrowSlot), new CustomPacketPayload[0]
            );
         }
      }
   }

   @Override
   public Material getMaterial() {
      return this.material;
   }

   public Supplier<ICurioRenderer> getQuiverRenderer() {
      return QuiverRenderer::new;
   }

   public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
      return true;
   }

   public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
      if (pStack.getCount() == 1 && pAction == ClickAction.SECONDARY) {
         BundleContents bundlecontents = (BundleContents)pStack.get(DataComponents.BUNDLE_CONTENTS);
         if (bundlecontents == null) {
            return false;
         } else {
            ItemStack itemstack = pSlot.getItem();
            QuiverItem.MutableQuiverContents bundlecontents$mutable = new QuiverItem.MutableQuiverContents(
               bundlecontents, this.getMaterial().offense().quiverStacks()
            );
            if (itemstack.isEmpty()) {
               this.playRemoveOneSound(pPlayer);
               ItemStack itemstack1 = bundlecontents$mutable.removeOne();
               if (itemstack1 != null) {
                  ItemStack itemstack2 = pSlot.safeInsert(itemstack1);
                  bundlecontents$mutable.tryInsert(itemstack2);
               }
            } else if (itemstack.getItem().canFitInsideContainerItems() && itemstack.is(ItemTags.ARROWS)) {
               int i = bundlecontents$mutable.tryTransfer(pSlot, pPlayer);
               if (i > 0) {
                  this.playInsertSound(pPlayer);
               }
            }

            pStack.set(DataComponents.BUNDLE_CONTENTS, bundlecontents$mutable.toImmutable());
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
      if (pStack.getCount() != 1) {
         return false;
      } else if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer)) {
         BundleContents bundlecontents = (BundleContents)pStack.get(DataComponents.BUNDLE_CONTENTS);
         if (bundlecontents == null) {
            return false;
         } else {
            QuiverItem.MutableQuiverContents bundlecontents$mutable = new QuiverItem.MutableQuiverContents(
               bundlecontents, this.getMaterial().offense().quiverStacks()
            );
            if (pOther.isEmpty()) {
               ItemStack itemstack = bundlecontents$mutable.removeOne();
               if (itemstack != null) {
                  this.playRemoveOneSound(pPlayer);
                  pAccess.set(itemstack);
               }
            } else {
               if (!pOther.is(ItemTags.ARROWS)) {
                  return false;
               }

               int i = bundlecontents$mutable.tryInsert(pOther);
               if (i > 0) {
                  this.playInsertSound(pPlayer);
               }
            }

            pStack.set(DataComponents.BUNDLE_CONTENTS, bundlecontents$mutable.toImmutable());
            return true;
         }
      } else {
         return false;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
   }

   public int getBarWidth(ItemStack pStack) {
      BundleContents bundlecontents = (BundleContents)pStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return Math.min(1 + this.mulAndTruncateRelToSlots(bundlecontents.weight(), 12), 13);
   }

   public int mulAndTruncateRelToSlots(Fraction pFraction, int pFactor) {
      return pFraction.getNumerator() * pFactor / (pFraction.getDenominator() * this.getMaterial().offense().quiverStacks());
   }

   @Override
   public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
      BundleContents bundlecontents = (BundleContents)pStack.get(DataComponents.BUNDLE_CONTENTS);
      if (bundlecontents != null) {
         int i = this.mulAndTruncateRelToSlots(bundlecontents.weight(), 64 * this.getMaterial().offense().quiverStacks());
         pTooltipComponents.add(
            Component.translatable("item.minecraft.bundle.fullness", new Object[]{i, 64 * this.getMaterial().offense().quiverStacks()})
               .withStyle(ChatFormatting.GRAY)
         );
      }
   }

   public void playInsert(Entity pEntity) {
      this.playInsertSound(pEntity);
   }

   public static final class Layer {
      private final String suffix;
      private final boolean dyeable;
      private final Function<ResourceLocation, ResourceLocation> texture;

      public Layer(String suffix, boolean pDyeable) {
         this.suffix = suffix;
         this.dyeable = pDyeable;
         this.texture = this.resolveTexture();
      }

      public Layer(String suffix) {
         this(suffix, false);
      }

      public Layer() {
         this("", false);
      }

      public Layer(ResourceLocation relativeTexture, boolean pDyeable) {
         this.suffix = "";
         this.dyeable = pDyeable;
         this.texture = assetName -> relativeTexture.withPath(p_324187_ -> "textures/model/quiver/" + relativeTexture.getPath() + ".png");
      }

      private Function<ResourceLocation, ResourceLocation> resolveTexture() {
         return assetName -> assetName.withPath(
            p_324187_ -> "textures/model/quiver/" + assetName.getPath() + (!this.suffix.isEmpty() ? "_" + this.suffix : "") + ".png"
         );
      }

      public ResourceLocation texture(ResourceLocation material) {
         return this.texture.apply(material);
      }

      public boolean dyeable() {
         return this.dyeable;
      }
   }

   public static class MutableQuiverContents {
      private final int stacks;
      private final List<ItemStack> items;
      private Fraction weight;

      public MutableQuiverContents(BundleContents pContents, int stacks) {
         this.items = new ArrayList<>((List)pContents.items());
         this.weight = pContents.weight();
         this.stacks = stacks;
      }

      public QuiverItem.MutableQuiverContents clearItems() {
         this.items.clear();
         this.weight = Fraction.ZERO;
         return this;
      }

      private Pair<Integer, Integer> findStackIndex(ItemStack pStack) {
         if (!pStack.isStackable()) {
            return Pair.of(-1, -1);
         } else {
            Pair<Integer, Integer> ret = Pair.of(-1, -1);

            for (int i = 0; i < this.items.size(); i++) {
               ItemStack itemStack = this.items.get(i);
               if (ItemStack.isSameItemSameComponents(itemStack, pStack)) {
                  if ((Integer)ret.getFirst() == -1) {
                     ret = Pair.of(i, (Integer)ret.getSecond());
                  } else {
                     ret = Pair.of((Integer)ret.getFirst(), i);
                  }
               }
            }

            return ret;
         }
      }

      private int getMaxAmountToAdd(ItemStack pStack) {
         Fraction fraction = Fraction.getFraction(this.stacks, 1).subtract(this.weight);
         return Math.max(fraction.divideBy(BundleContents.getWeight(pStack)).intValue(), 0);
      }

      public int tryInsert(ItemStack pStack) {
         if (!pStack.isEmpty() && pStack.getItem().canFitInsideContainerItems()) {
            int i = Math.min(pStack.getCount(), this.getMaxAmountToAdd(pStack));
            if (i == 0) {
               return 0;
            } else {
               this.weight = this.weight.add(BundleContents.getWeight(pStack).multiplyBy(Fraction.getFraction(i, 1)));
               Pair<Integer, Integer> j = this.findStackIndex(pStack);
               if (!j.equals(Pair.of(-1, -1))) {
                  for (int k = (Integer)j.getFirst(); k <= j.getSecond(); k++) {
                     ItemStack itemstack = this.items.remove(k);
                     int available_space = itemstack.getMaxStackSize() - itemstack.getCount();
                     int add = Math.min(available_space, i);
                     ItemStack itemstack1 = itemstack.copyWithCount(itemstack.getCount() + add);
                     pStack.shrink(add);
                     i -= add;
                     this.items.add(k, itemstack1);
                     if (i <= 0) {
                        break;
                     }
                  }

                  if (i > 0) {
                     this.items.add((Integer)j.getSecond() + 1, pStack.split(i));
                  }
               } else {
                  this.items.addFirst(pStack.split(i));
               }

               return i;
            }
         } else {
            return 0;
         }
      }

      public int tryTransfer(Slot pSlot, Player pPlayer) {
         ItemStack itemstack = pSlot.getItem();
         int i = this.getMaxAmountToAdd(itemstack);
         return this.tryInsert(pSlot.safeTake(itemstack.getCount(), i, pPlayer));
      }

      @Nullable
      public ItemStack removeOne() {
         if (this.items.isEmpty()) {
            return null;
         } else {
            ItemStack itemstack = this.items.removeFirst().copy();
            this.weight = this.weight.subtract(BundleContents.getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));
            return itemstack;
         }
      }

      public Fraction weight() {
         return this.weight;
      }

      public void assureNoEmpties() {
         this.items.removeIf(ItemStack::isEmpty);
      }

      public BundleContents toImmutable() {
         this.assureNoEmpties();
         return new BundleContents(List.copyOf(this.items));
      }
   }
}
