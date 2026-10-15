package en.predator.horrorelementmod.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HorrorElementModModTabs {
   public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "horror_element_mod");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HORROR_ELEMENTTAB = REGISTRY.register(
      "horror_elementtab",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("item_group.horror_element_mod.horror_elementtab"))
         .icon(() -> new ItemStack((ItemLike)HorrorElementModModBlocks.BLOOD_1.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_1.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_2.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_3.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_4.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_5.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_6.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_7.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_8.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_9.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOOD_10.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.ACID_BLOOD_1.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.ACID_BLOOD_2.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.GREEN_BLOOD_1.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.GREEN_BLOOD_2.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.VILLAGER_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.RUSSIAN_OFFICER_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.GERMAN_OFFICER_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HAZZMAT_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.PRIMITIVE_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.DESERT_MARINE_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.COLONIAL_MARINE_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.ALEX_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.STEVE_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HEROBRINE_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SPIDER_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.COLONIAL_MARINE_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SCIENTIST_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HAZZMAT_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.MARINE_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.ROTTEN_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SLICED_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BEHEADED_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SPIKED_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HANGING_VILLAGER.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SPIKED_VILLAGER.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.CRUCIFIED_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.IMPALED_SKELETON.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.DEAD_COW.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.DEAD_PIG.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.DEAD_EXPERIMENT.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HUMAN_REMAINS.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BROKEN_TV.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HOSPITAL_BED.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BLOODY_TOMBSTONE.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.IMPALED_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.PHISHED_HEAD.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SEVERED_HAND.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HANGING_CORPSE_1.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.HANGING_CORPSE_2.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SIGN_1.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SIGN_2.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.TRAPPED_LEG.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SLASHED_SKULL.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXE_AND_LOG.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.CHAINED_ENTITY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SELF_TERMINATED_BODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.SCARECROW_FARMER.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.IMPALED_KNIGHT.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.IMPALED_SOLDIER.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADEOAK.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADE_OAK_BLOODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADESPRUCE.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADE_SPRUCE_BLOODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADE_DARKOAK.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADE_DARK_OAK_BLOODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADE_BIRCH.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BARRICADE_BIRCH_BLOODY.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXEDDOORBAMBOO.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXED_DOOR_DARKOAK.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXED_DOOR_JUNGLE.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXED_DOOR_OAK.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.DESTROYED_DOOR_IRON.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXED_DOOR_SPRUCE.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.AXED_DOOR_BIRCH.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.CORDYCEPSFARMER.get()).asItem());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> REDSTONE = REGISTRY.register(
      "redstone",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("item_group.horror_element_mod.redstone"))
         .icon(() -> new ItemStack((ItemLike)HorrorElementModModBlocks.REDLANTERN.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept(((Block)HorrorElementModModBlocks.SOUND_BLOCK.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.ATMOSPHERE_BLOCK.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.BROKEN_LIGHT.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.CURSED_PAINTING.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.REDSTONE_TRIGERRER.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.REDLANTERN.get()).asItem());
            tabData.accept(((Block)HorrorElementModModBlocks.WOODDEN_SHUTTER.get()).asItem());
         })
         .withTabsBefore(new ResourceLocation[]{HORROR_ELEMENTTAB.getId()})
         .build()
   );
}
