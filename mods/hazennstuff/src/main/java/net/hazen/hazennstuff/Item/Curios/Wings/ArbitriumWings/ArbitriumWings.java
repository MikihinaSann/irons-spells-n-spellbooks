package net.hazen.hazennstuff.Item.Curios.Wings.ArbitriumWings;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

public class ArbitriumWings extends CurioBaseItem {
   public final HnSDispatcher dispatcher = new HnSDispatcher();
   private static final int GLIDE_ANIMATION = 0;

   public ArbitriumWings() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).fireResistant().rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()));
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(
         Component.translatable("item.hazennstuff.arbitrium_robes.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC})
      );
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(Attributes.ELYTRA_FLIGHT, new AttributeModifier(id, 1.0, Operation.ADD_VALUE));
      return attr;
   }

   private void handleFlightState(Player player, ItemStack stack) {
      this.dispatcher.flight(player, stack);
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      if (slotContext.entity() instanceof Player player && !player.level().isClientSide) {
         if (!player.level().isClientSide && player.isFallFlying()) {
            this.elytraFlightTick(stack, player, 0);
         }

         if (player.isFallFlying()) {
            this.handleFlightState(player, stack);
            player.fallDistance = 0.0F;
         } else {
            this.dispatcher.idle(player, stack);
         }
      }
   }

   public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
      return true;
   }
}
