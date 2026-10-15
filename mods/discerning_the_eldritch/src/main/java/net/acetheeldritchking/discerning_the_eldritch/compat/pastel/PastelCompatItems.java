package net.acetheeldritchking.discerning_the_eldritch.compat.pastel;

import java.util.Collection;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEFluidRegistry;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class PastelCompatItems {
   public static final Items ITEMS = DeferredRegister.createItems("discerning_the_eldritch");
   public static final DeferredHolder<Item, Item> DREAM_REAVER_YMIR = ITEMS.register("dream_reaver_ymir", DreamReaverYmirItem::new);
   public static final DeferredHolder<Item, Item> BUCKET_OF_MALICE = ITEMS.register(
      "bucket_of_malice",
      () -> new BucketItem(
         (Fluid)DTEFluidRegistry.LIQUID_MALICE.get(),
         new Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).rarity((Rarity)ASRarities.ACCURSED_RARITY_PROXY.getValue()).stacksTo(1)
      )
   );

   public static Collection<DeferredHolder<Item, ? extends Item>> getDTEPastelItems() {
      return ITEMS.getEntries();
   }

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
