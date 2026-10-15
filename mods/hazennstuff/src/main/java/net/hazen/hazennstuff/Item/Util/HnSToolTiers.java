package net.hazen.hazennstuff.Item.Util;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.hazen.hazennstuff.Datagen.HnSTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.SimpleTier;

public class HnSToolTiers {
   public static final Tier MITHRIL = new SimpleTier(
      HnSTags.Blocks.INCORRECT_FOR_MITHRIL_TOOl, 1400, 6.0F, 3.0F, 28, () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_SCRAP.get()})
   );
}
