package net.warphan.iss_magicfromtheeast.entity.mobs.kitsune;

import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SummonedKitsuneRenderer extends MobRenderer<SummonedKitsune, FoxModel<SummonedKitsune>> {
   public SummonedKitsuneRenderer(Context context) {
      super(context, new FoxModel(context.bakeLayer(ModelLayers.FOX)), 0.55F);
   }

   public ResourceLocation getTextureLocation(SummonedKitsune summonedKitsune) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/entity/fox/kitsune.png");
   }
}
