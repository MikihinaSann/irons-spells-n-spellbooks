package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.client.sprites.AlphaMaskFolderPermutations;
import com.userofbricks.expanded_combat.client.sprites.PalettedFolderPermutations;
import com.userofbricks.expanded_combat.client.sprites.ReversedPalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;

@EventBusSubscriber(
   modid = "expanded_combat",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class SpriteSourceTypes {
   public static SpriteSourceType PALETTED_FOLDER_PERMUTATIONS = new SpriteSourceType(PalettedFolderPermutations.CODEC);
   public static SpriteSourceType ALPHA_MASK_FOLDER_PERMUTATIONS = new SpriteSourceType(AlphaMaskFolderPermutations.CODEC);
   public static SpriteSourceType REVERSED_PALETTED_FOLDER_PERMUTATIONS = new SpriteSourceType(ReversedPalettedPermutations.CODEC);

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void registerSpriteSourceTypes(RegisterSpriteSourceTypesEvent event) {
      event.register(ExpandedCombat.modLoc("alpha_mask_folder_permutations"), ALPHA_MASK_FOLDER_PERMUTATIONS);
      event.register(ExpandedCombat.modLoc("paletted_folder_permutations"), PALETTED_FOLDER_PERMUTATIONS);
      event.register(ExpandedCombat.modLoc("reversed_paletted_permutations"), REVERSED_PALETTED_FOLDER_PERMUTATIONS);
   }
}
