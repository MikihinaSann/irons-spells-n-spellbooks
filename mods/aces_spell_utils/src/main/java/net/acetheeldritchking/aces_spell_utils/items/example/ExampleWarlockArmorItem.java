package net.acetheeldritchking.aces_spell_utils.items.example;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtilsClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.entity.armor.ExampleWarlockMaskModel;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ExampleWarlockArmorItem extends ImbuableExtendedGeoArmorItem {
   private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "textures/models/armor/evil_armor_tips.png");

   public ExampleWarlockArmorItem(Type slot, Properties settings) {
      super(
         ExampleArmorMaterialRegistry.EXAMPLE_ARMOR,
         slot,
         settings,
         schoolAttributesWithResistance(AttributeRegistry.ELDRITCH_SPELL_POWER, AttributeRegistry.MANA_REGEN, 150, 0.1F, 0.05F, 0.05F)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return AcesSpellUtilsClientRenderers.exampleWarlockArmorItem(); }
}
