package net.acetheeldritchking.cataclysm_spellbooks.entity.render.mobs.phantom_remnant;

import com.github.L_Ender.cataclysm.client.render.entity.Ancient_Remnant_Rework_Renderer;
import com.github.L_Ender.cataclysm.client.render.layer.Ancient_Remnant_Layer;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Ancient_Remnant.Ancient_Remnant_Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class PhantomAncientRemnantRenderer extends Ancient_Remnant_Rework_Renderer {
   private static final ResourceLocation REMNANT_TEXTURES = ResourceLocation.fromNamespaceAndPath(
      "cataclysm_spellbooks", "textures/entity/ancient_remnant/ancient_remnant_rag.png"
   );
   private final RandomSource rnd = RandomSource.create();

   public PhantomAncientRemnantRenderer(Context renderManagerIn) {
      super(renderManagerIn);
      this.addLayer(new Ancient_Remnant_Layer(this));
      this.addLayer(new AncientRemnantTransLayer(this));
   }

   public ResourceLocation getTextureLocation(Ancient_Remnant_Entity entity) {
      return REMNANT_TEXTURES;
   }

   protected float getFlipDegrees(Ancient_Remnant_Entity entity) {
      return 0.0F;
   }
}
