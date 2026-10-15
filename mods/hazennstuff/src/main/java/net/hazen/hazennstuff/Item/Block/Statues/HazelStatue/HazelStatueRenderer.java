package net.hazen.hazennstuff.Item.Block.Statues.HazelStatue;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HazelStatueRenderer extends GeoBlockRenderer<HazelStatueBlockEntity> {
   public HazelStatueRenderer(Context context) {
      super(new HazelStatueModel());
   }
}
