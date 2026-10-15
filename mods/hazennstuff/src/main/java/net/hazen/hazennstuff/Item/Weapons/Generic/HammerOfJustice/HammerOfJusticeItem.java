package net.hazen.hazennstuff.Item.Weapons.Generic.HammerOfJustice;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

public class HammerOfJusticeItem extends MagicSwordItem {
   public final HnSDispatcher dispatcher = new HnSDispatcher();

   public HammerOfJusticeItem() {
      super(
         HnSExtendedWeaponsTiers.HAMMER_OF_JUSTICE,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.HOLY_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.HAMMER_OF_JUSTICE)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.DIVINE_SMITE_SPELL, 10)})
      );
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && entity instanceof Player player) {
         this.dispatcher.idle(player, stack);
      }
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(stack);
      if (!affinityData.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.hazennstuff.spellbook_spell_count");
         lines.addAll(i < 0 ? lines.size() : i + 1, affinityData.getDescriptionComponent());
      }

      lines.add(Component.translatable("item.hazennstuff.cat.description").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.ITALIC}));
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         itemStack.set(
            ComponentRegistry.AFFINITY_COMPONENT, new AffinityData(Map.of(((AbstractSpell)SpellRegistry.DIVINE_SMITE_SPELL.get()).getSpellResource(), 1))
         );
      }
   }

   @EventBusSubscriber
   public class SpellEvents {
      @SubscribeEvent
      public static void onModifySpellLevel(ModifySpellLevelEvent event) {
         LivingEntity caster = event.getEntity();
         if (caster != null) {
            if (event.getSpell() == SpellRegistry.DIVINE_SMITE_SPELL.get()) {
               ItemStack mainHand = caster.getMainHandItem();
               ItemStack offHand = caster.getOffhandItem();
               boolean usingKnives = mainHand.getItem() instanceof HammerOfJusticeItem || offHand.getItem() instanceof HammerOfJusticeItem;
               if (usingKnives) {
                  event.addLevels(1);
               }
            }
         }
      }
   }
}
