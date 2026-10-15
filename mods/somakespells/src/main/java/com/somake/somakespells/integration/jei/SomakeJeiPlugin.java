package com.somake.somakespells.integration.jei;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.recipe.pedestal_ritual.PedestalRitualRecipe;
import com.somake.somakespells.recipe.upgrade_forge.UpgradeForgeRecipe;
import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.registries.ModRecipes;
import com.somake.somakespells.ritual.RitualElementalMode;
import io.redspace.ironsspellbooks.jei.ArcaneAnvilJeiRecipe;
import io.redspace.ironsspellbooks.jei.ArcaneAnvilRecipeCategory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;

@JeiPlugin
public class SomakeJeiPlugin implements IModPlugin {
   private static final ResourceLocation PLUGIN_UID = ResourceLocation.fromNamespaceAndPath("somakespells", "jei_plugin");
   private static final Map<String, Integer> FAMILY_ORDER = Map.ofEntries(
      Map.entry("burning_magma", 0),
      Map.entry("tidebound", 1),
      Map.entry("starless_grimoire", 2),
      Map.entry("voidbound_codex", 3),
      Map.entry("invoker_canon", 4),
      Map.entry("dawnlit_gospel", 5),
      Map.entry("frostvein_codex", 6),
      Map.entry("stormcaller_lexicon", 7),
      Map.entry("verdant_testament", 8),
      Map.entry("sanguine_testament", 9),
      Map.entry("soullit_testament", 10),
      Map.entry("axiomatic_codex", 11),
      Map.entry("stoneheart_codex", 12),
      Map.entry("resonance_canticle", 13),
      Map.entry("dormant_grimoire_to_sanctum", 14),
      Map.entry("dormant_grimoire_to_profane", 15)
   );
   private static final Map<String, Integer> PEDESTAL_RESULT_ORDER = Map.ofEntries(
      Map.entry("somakespells:fire", 0),
      Map.entry("somakespells:soul_fire", 1),
      Map.entry("somakespells:infernal_fire", 2),
      Map.entry("somakespells:ignitium_staff", 3),
      Map.entry("somakespells:soul_fire_ignitium_staff", 4),
      Map.entry("somakespells:infernal_fire_ignitium_staff", 5),
      Map.entry("somakespells:soul_fire_rune", 6),
      Map.entry("somakespells:infernal_fire_rune", 7),
      Map.entry("somakespells:soul_fire_upgrade_orb", 8),
      Map.entry("somakespells:infernal_fire_upgrade_orb", 9),
      Map.entry("somakespells:soul_fire_necklace", 10),
      Map.entry("somakespells:infernal_fire_necklace", 12)
   );

   public ResourceLocation getPluginUid() {
      return PLUGIN_UID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new UpgradeForgeJeiCategory(registration.getJeiHelpers().getGuiHelper()),
            new PedestalRitualJeiCategory(registration.getJeiHelpers().getGuiHelper())
         }
      );
   }

   public void registerRecipes(IRecipeRegistration registration) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         List<UpgradeForgeRecipe> recipes = minecraft.level
            .getRecipeManager()
            .getAllRecipesFor((RecipeType<UpgradeForgeRecipe>)(RecipeType<?>)ModRecipes.UPGRADE_FORGE_TYPE.get())
            .stream()
            .sorted(
               Comparator.<RecipeHolder<UpgradeForgeRecipe>>comparingInt(holder -> familyOrder(holder.id()))
                  .thenComparingInt(holder -> upgradeStepOrder(holder.id()))
                  .thenComparing(holder -> holder.id().toString())
            )
            .<UpgradeForgeRecipe>map(RecipeHolder::value)
            .toList();
         registration.addRecipes(UpgradeForgeJeiCategory.RECIPE_TYPE, recipes);
         List<PedestalRitualRecipe> pedestalRecipes = minecraft.level
            .getRecipeManager()
            .getAllRecipesFor((RecipeType<PedestalRitualRecipe>)(RecipeType<?>)ModRecipes.PEDESTAL_RITUAL_TYPE.get())
            .stream()
            .filter(holder -> BornInChaosCompat.isLoaded() || !isInfernalFireRecipe((PedestalRitualRecipe)holder.value()))
            .sorted(
               Comparator.<RecipeHolder<PedestalRitualRecipe>>comparingInt(holder -> pedestalResultOrder(holder.value()))
                  .thenComparingInt(holder -> pedestalVariantOrder((PedestalRitualRecipe)holder.value()))
                  .thenComparing(holder -> holder.id().toString())
            )
            .<PedestalRitualRecipe>map(RecipeHolder::value)
            .toList();
         registration.addRecipes(PedestalRitualJeiCategory.RECIPE_TYPE, pedestalRecipes);
         registration.addIngredientInfo(
            (ItemLike)ModItems.SOUL_BLAZE_ROD.get(),
            new Component[]{Component.translatable("jei.somakespells.soul_blaze_rod.info.1"), Component.translatable("jei.somakespells.soul_blaze_rod.info.2")}
         );
         if (BornInChaosCompat.isLoaded()) {
            registration.addIngredientInfo(
               (ItemLike)ModCompatItems.INFERNAL_BLAZE_ROD.get(),
               new Component[]{
                  Component.translatable("jei.somakespells.infernal_blaze_rod.info.1"), Component.translatable("jei.somakespells.infernal_blaze_rod.info.2")
               }
            );
         }
      }
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModItems.UPGRADE_FORGE.get()), new mezz.jei.api.recipe.RecipeType[]{UpgradeForgeJeiCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)com.github.L_Ender.cataclysm.init.ModItems.ALTAR_OF_FIRE.get()),
         new mezz.jei.api.recipe.RecipeType[]{PedestalRitualJeiCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModItems.PEDESTAL_OF_FIRE.get()), new mezz.jei.api.recipe.RecipeType[]{PedestalRitualJeiCategory.RECIPE_TYPE}
      );
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
   }

   public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
      List<ItemStack> hiddenStacks = getHiddenJeiStacks();
      if (hiddenStacks.isEmpty()) {
         hideArcaneAnvilSmallOrbRecipes(jeiRuntime.getRecipeManager());
      } else {
         jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenStacks);
         hideArcaneAnvilSmallOrbRecipes(jeiRuntime.getRecipeManager());
      }
   }

   private static void hideArcaneAnvilSmallOrbRecipes(IRecipeManager recipeManager) {
      List<ArcaneAnvilJeiRecipe> recipesToHide = recipeManager.createRecipeLookup(ArcaneAnvilRecipeCategory.ARCANE_ANVIL_RECIPE_RECIPE_TYPE)
         .get()
         .filter(SomakeJeiPlugin::isArcaneAnvilSmallOrbRecipe)
         .toList();
      if (!recipesToHide.isEmpty()) {
         recipeManager.hideRecipes(ArcaneAnvilRecipeCategory.ARCANE_ANVIL_RECIPE_RECIPE_TYPE, recipesToHide);
      }
   }

   private static boolean isArcaneAnvilSmallOrbRecipe(ArcaneAnvilJeiRecipe recipe) {
      List<ItemStack> rightItems = (List<ItemStack>)recipe.getRecipeItems().b();
      return rightItems.stream().anyMatch(SomakeJeiPlugin::isSmallUpgradeOrbStack);
   }

   private static boolean isSmallUpgradeOrbStack(ItemStack stack) {
      return stack.getItem().builtInRegistryHolder().key().location().getPath().endsWith("_upgrade_orb_small");
   }

   private static int familyOrder(ResourceLocation recipeId) {
      String path = recipeName(recipeId);
      String prefix = "upgrade_";
      String tier1Suffix = "_t1_to_t2";
      String tier2Suffix = "_t2_to_t3";
      String familyKey = path;
      if (path.startsWith(prefix)) {
         familyKey = path.substring(prefix.length());
      }

      if (familyKey.endsWith(tier1Suffix)) {
         familyKey = familyKey.substring(0, familyKey.length() - tier1Suffix.length());
      } else if (familyKey.endsWith(tier2Suffix)) {
         familyKey = familyKey.substring(0, familyKey.length() - tier2Suffix.length());
      }

      return FAMILY_ORDER.getOrDefault(familyKey, Integer.MAX_VALUE);
   }

   private static int upgradeStepOrder(ResourceLocation recipeId) {
      String path = recipeName(recipeId);
      if (path.endsWith("_t1_to_t2")) {
         return 0;
      } else {
         return path.endsWith("_t2_to_t3") ? 1 : Integer.MAX_VALUE;
      }
   }

   private static String recipeName(ResourceLocation recipeId) {
      String path = recipeId.getPath();
      int slashIndex = path.lastIndexOf(47);
      return slashIndex >= 0 ? path.substring(slashIndex + 1) : path;
   }

   private static int pedestalResultOrder(PedestalRitualRecipe recipe) {
      return PEDESTAL_RESULT_ORDER.getOrDefault(recipe.result().getItem().builtInRegistryHolder().key().location().toString(), Integer.MAX_VALUE);
   }

   private static int pedestalVariantOrder(PedestalRitualRecipe recipe) {
      String resultId = recipe.result().getItem().builtInRegistryHolder().key().location().toString();
      String centerId = firstCenterItemId(recipe);

      return switch (resultId) {
         case "somakespells:soul_fire_necklace" -> "irons_spellbooks:heavy_chain_necklace".equals(centerId) ? 0 : 1;
         case "somakespells:infernal_fire_necklace" -> "somakespells:soul_fire_necklace".equals(centerId) ? 0 : 1;
         default -> 0;
      };
   }

   private static String firstCenterItemId(PedestalRitualRecipe recipe) {
      ItemStack[] items = recipe.centerItem().getItems();
      return items.length == 0 ? "" : items[0].getItem().builtInRegistryHolder().key().location().toString();
   }

   private static boolean isInfernalFireRecipe(PedestalRitualRecipe recipe) {
      String resultId = recipe.result().getItem().builtInRegistryHolder().key().location().toString();
      return recipe.requiredMode() == RitualElementalMode.INFERNAL_FIRE || recipe.activatesInfernalFire() || resultId.startsWith("somakespells:infernal_fire");
   }

   private static List<ItemStack> getHiddenJeiStacks() {
      List<Item> hiddenItems = new ArrayList<>();
      hiddenItems.add((Item)ModItems.STARLESS_GRIMOIRE_SPELL_BOOK.get());
      hiddenItems.add((Item)ModItems.STARLESS_GRIMOIRE_2_SPELL_BOOK.get());
      hiddenItems.add((Item)ModItems.STARLESS_GRIMOIRE_3_SPELL_BOOK.get());
      hiddenItems.add((Item)ModItems.FROSTVEIN_CODEX_SPELL_BOOK.get());
      hiddenItems.add((Item)ModItems.FROSTVEIN_CODEX_2_SPELL_BOOK.get());
      hiddenItems.add((Item)ModItems.FROSTVEIN_CODEX_3_SPELL_BOOK.get());
      hiddenItems.add((Item)ModItems.FIRE_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.ICE_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.LIGHTNING_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.HOLY_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.ENDER_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.BLOOD_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.EVOCATION_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.NATURE_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.ELDRITCH_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.AQUA_UPGRADE_ORB_SMALL.get());
      hiddenItems.add((Item)ModItems.SOUL_FIRE_UPGRADE_ORB_SMALL.get());
      hiddenItems.add(resolveItem(ModItems.SPIRIT_UPGRADE_ORB_SMALL));
      hiddenItems.add(resolveItem(ModItems.SYMMETRY_UPGRADE_ORB_SMALL));
      hiddenItems.add(resolveItem(ModItems.SOULLIT_TESTAMENT_SPELL_BOOK));
      hiddenItems.add(resolveItem(ModItems.SOULLIT_TESTAMENT_2_SPELL_BOOK));
      hiddenItems.add(resolveItem(ModItems.SOULLIT_TESTAMENT_3_SPELL_BOOK));
      hiddenItems.add(resolveItem(ModItems.AXIOMATIC_CODEX_SPELL_BOOK));
      hiddenItems.add(resolveItem(ModItems.AXIOMATIC_CODEX_2_SPELL_BOOK));
      hiddenItems.add(resolveItem(ModItems.AXIOMATIC_CODEX_3_SPELL_BOOK));
      if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
         hiddenItems.add((Item)ModCompatItems.STONEHEART_CODEX_SPELL_BOOK.get());
         hiddenItems.add((Item)ModCompatItems.STONEHEART_CODEX_2_SPELL_BOOK.get());
         hiddenItems.add((Item)ModCompatItems.STONEHEART_CODEX_3_SPELL_BOOK.get());
         hiddenItems.add((Item)ModCompatItems.GEO_UPGRADE_ORB_SMALL.get());
      }

      if (ModList.get().isLoaded("tunes_n_tomes")) {
         hiddenItems.add((Item)ModCompatItems.SOUND_UPGRADE_ORB_SMALL.get());
      }

      if (BornInChaosCompat.isLoaded()) {
         hiddenItems.add((Item)ModCompatItems.INFERNAL_FIRE_UPGRADE_ORB_SMALL.get());
      }

      return hiddenItems.stream().filter(Objects::nonNull).distinct().<ItemStack>map(ItemStack::new).toList();
   }

   private static Item resolveItem(DeferredHolder<Item, ? extends Item> holder) {
      return holder == null ? null : (Item)holder.get();
   }
}
