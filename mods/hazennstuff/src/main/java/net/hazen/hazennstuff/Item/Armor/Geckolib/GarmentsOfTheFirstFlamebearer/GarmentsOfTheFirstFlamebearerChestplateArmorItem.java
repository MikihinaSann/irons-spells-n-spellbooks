package net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GarmentsOfTheFirstFlamebearerChestplateArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public GarmentsOfTheFirstFlamebearerChestplateArmorItem(Type type, Properties settings) {
      super(HnSArmorMaterials.TYROS_MATERIAL, type, settings, pureTier(AttributeRegistry.FIRE_SPELL_POWER));
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addArcaneResonance(attributes, group);
      ArsNoveauCompat.addMaxMana(attributes, group);
      ArsNoveauCompat.addManaRegen(attributes, group);
      return attributes.build().modifiers();
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.garmentsOfTheFirstFlamebearerChestplateArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.MAGE_SET_BONUS, 200, 0, false, false, false));
      }

      if (!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
         player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, true));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerChestplateArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerChestplateArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerChestplateArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerChestplateArmorItem;
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(stack);
      if (!affinityData.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.hazennstuff.spellbook_spell_count");
         lines.addAll(i < 0 ? lines.size() : i + 1, affinityData.getDescriptionComponent());
      }

      lines.add(Component.translatable("item.hazennstuff.tyros.description").withStyle(Style.EMPTY.withColor(15881518).withItalic(true)));
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         itemStack.set(
            ComponentRegistry.AFFINITY_COMPONENT,
            new AffinityData(
               Map.of(((AbstractSpell)SpellRegistry.RAISE_HELL_SPELL.get()).getSpellResource(), 1, HnSSpellRegistries.FIERY_DAGGER.get().getSpellResource(), 1)
            )
         );
      }
   }

   @EventBusSubscriber
   public class SpellEvents {
      @SubscribeEvent
      public static void onModifySpellLevel(ModifySpellLevelEvent event) {
         LivingEntity caster = event.getEntity();
         if (caster != null) {
            AbstractSpell spell = event.getSpell();
            boolean isCinderousSpell = spell == SpellRegistry.RAISE_HELL_SPELL.get() || spell == HnSSpellRegistries.FIERY_DAGGER.get();
            if (isCinderousSpell) {
               ItemStack chestItem = caster.getItemBySlot(EquipmentSlot.CHEST);
               if (chestItem.getItem() instanceof GarmentsOfTheFirstFlamebearerChestplateArmorItem) {
                  event.addLevels(1);
               }
            }
         }
      }
   }
}
