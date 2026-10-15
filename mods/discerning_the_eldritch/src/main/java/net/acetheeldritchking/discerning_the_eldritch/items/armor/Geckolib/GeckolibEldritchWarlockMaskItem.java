package net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritchClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.GeckolibEldritchWarlockMaskModel;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.DTEArmorMaterialRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GeckolibEldritchWarlockMaskItem extends ImbuableGeckolibDTEArmorItem {
   private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/eldritch_warlock_mask_glowmask.png"
   );

   public GeckolibEldritchWarlockMaskItem(Type slot, Properties settings) {
      super(
         DTEArmorMaterialRegistry.ELDRITCH_WARLOCK,
         slot,
         settings,
         schoolAttributesWithResistance(AttributeRegistry.ELDRITCH_SPELL_POWER, AttributeRegistry.SUMMON_DAMAGE, 150, 0.1F, 0.05F, 0.05F)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return DiscerningTheEldritchClientRenderers.geckolibEldritchWarlockMaskItem(); }
}
