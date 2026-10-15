package net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritchClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.IArmorCapeProvider;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.AscendedArmorModel;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.DTEArmorMaterialRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AscendedArmorItem extends ImbuableGeckolibDTEArmorItem implements IArmorCapeProvider {
   private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/ascended_armor_glowmask.png"
   );

   public AscendedArmorItem(Type type, Properties properties) {
      super(
         DTEArmorMaterialRegistry.ELDRITCH_WARLOCK,
         type,
         properties,
         schoolAttributesWithResistance(AttributeRegistry.ELDRITCH_SPELL_POWER, AttributeRegistry.MANA_REGEN, 150, 0.1F, 0.05F, 0.05F)
      );
   }

   public ResourceLocation getCapeResourceLocation() {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/geckolib/ascended_armor_cape.png");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return DiscerningTheEldritchClientRenderers.ascendedArmorItem(); }
}
