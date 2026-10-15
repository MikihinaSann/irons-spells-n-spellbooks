package net.hazen.hazennstuff.Item.Armor.Geckolib.ProjectSekai;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ProjectSekaiArmorModel extends DefaultedEntityGeoModel<ProjectSekaiArmorItem> {
   public ProjectSekaiArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ProjectSekaiArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/project_sekai_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ProjectSekaiArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/project_sekai_armor.png");
   }

   public ResourceLocation getAnimationResource(ProjectSekaiArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
