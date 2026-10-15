package com.snackpirate.aeromancy.entity.armor;

import com.snackpirate.aeromancy.item.WindmakerRobeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WindmakerArmorModel extends GeoModel<WindmakerRobeItem> {
   public ResourceLocation getModelResource(WindmakerRobeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("aero_additions", "geo/windmaker_robes.geo.json");
   }

   public ResourceLocation getTextureResource(WindmakerRobeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("aero_additions", "textures/models/armor/windmaker_robes.png");
   }

   public ResourceLocation getAnimationResource(WindmakerRobeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("aero_additions", "animations/wizard_armor_animation.json");
   }
}
