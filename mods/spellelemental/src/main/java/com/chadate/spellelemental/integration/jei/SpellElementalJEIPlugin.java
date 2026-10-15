package com.chadate.spellelemental.integration.jei;

import com.chadate.spellelemental.SpellElemental;
import com.chadate.spellelemental.integration.jei.category.ElementReactionCategory;
import com.chadate.spellelemental.integration.jei.category.SwordOilApplicationCategory;
import com.chadate.spellelemental.integration.jei.data.ElementReactionJEIData;
import com.chadate.spellelemental.integration.jei.data.SwordOilConfigLoader;
import com.chadate.spellelemental.integration.jei.recipe.ElementReactionRecipe;
import com.chadate.spellelemental.integration.jei.recipe.SwordOilApplicationRecipe;
import com.chadate.spellelemental.item.SwordOilItem;
import com.chadate.spellelemental.register.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class SpellElementalJEIPlugin implements IModPlugin {
   public static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath("spellelemental", "jei_plugin");
   public static final RecipeType<SwordOilApplicationRecipe> SWORD_OIL_APPLICATION = RecipeType.create(
      "spellelemental", "sword_oil_application", SwordOilApplicationRecipe.class
   );
   public static final RecipeType<ElementReactionRecipe> ELEMENT_REACTION = RecipeType.create("spellelemental", "element_reaction", ElementReactionRecipe.class);

   @NotNull
   public ResourceLocation getPluginUid() {
      return PLUGIN_ID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new SwordOilApplicationCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new ElementReactionCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.SWORD_STAND.get()), new RecipeType[]{SWORD_OIL_APPLICATION});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      List<SwordOilApplicationRecipe> recipes = this.createSwordOilApplicationRecipes();
      registration.addRecipes(SWORD_OIL_APPLICATION, recipes);
      List<ElementReactionRecipe> reactionRecipes = ElementReactionJEIData.generateReactionRecipes();
      registration.addRecipes(ELEMENT_REACTION, reactionRecipes);
   }

   private List<SwordOilApplicationRecipe> createSwordOilApplicationRecipes() {
      List<SwordOilApplicationRecipe> recipes = new ArrayList<>();
      List<ItemStack> swords = this.getSwordItems();
      List<SwordOilConfigLoader.SwordOilConfig> swordOilConfigs = SwordOilConfigLoader.getSwordOilConfigs();
      if (swordOilConfigs.isEmpty()) {
         SpellElemental.LOGGER.info("精油配置为空，创建基础展示配方");
         recipes.addAll(this.createFallbackRecipes(swords));
      } else {
         for (ItemStack sword : swords) {
            for (SwordOilConfigLoader.SwordOilConfig oilConfig : swordOilConfigs) {
               ItemStack swordOilItem = oilConfig.getItemStack();
               if (!swordOilItem.isEmpty()) {
                  recipes.add(
                     new SwordOilApplicationRecipe(
                        sword.copy(),
                        swordOilItem,
                        new ItemStack((ItemLike)ModBlocks.SWORD_STAND.get()),
                        this.createEnchantedSword(sword.getItem(), oilConfig.getElement(), oilConfig.getAmount()),
                        oilConfig.getElement(),
                        oilConfig.getAmount()
                     )
                  );
               }
            }
         }
      }

      return recipes;
   }

   private List<SwordOilApplicationRecipe> createFallbackRecipes(List<ItemStack> swords) {
      List<SwordOilApplicationRecipe> fallbackRecipes = new ArrayList<>();
      List<ItemStack> oilItems = this.getAllSwordOilItems();

      for (ItemStack sword : swords) {
         for (ItemStack oilItem : oilItems) {
            if (oilItem.getItem() instanceof SwordOilItem swordOilItem) {
               String element = swordOilItem.getElementType();
               int amount = swordOilItem.getElementAmount();
               if (element.isEmpty()) {
                  element = "unknown";
               }

               if (amount <= 0) {
                  amount = 1000;
               }

               fallbackRecipes.add(
                  new SwordOilApplicationRecipe(
                     sword.copy(),
                     oilItem.copy(),
                     new ItemStack((ItemLike)ModBlocks.SWORD_STAND.get()),
                     this.createEnchantedSword(sword.getItem(), element, amount),
                     element,
                     amount
                  )
               );
            }
         }
      }

      return fallbackRecipes;
   }

   private List<ItemStack> getAllSwordOilItems() {
      List<ItemStack> oilItems = new ArrayList<>();

      for (Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
         Item item = entry.getValue();
         if (item instanceof SwordOilItem) {
            oilItems.add(new ItemStack(item));
         }
      }

      return oilItems;
   }

   private List<ItemStack> getSwordItems() {
      List<ItemStack> swords = new ArrayList<>();

      for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.SWORDS)) {
         swords.add(new ItemStack((ItemLike)holder.value()));
      }

      return swords;
   }

   private ItemStack createEnchantedSword(Item swordItem, String elementType, int elementAmount) {
      ItemStack sword = new ItemStack(swordItem);
      CompoundTag tag = new CompoundTag();
      CompoundTag elementData = new CompoundTag();
      elementData.putString("element", elementType);
      elementData.putInt("amount", elementAmount);
      elementData.putLong("appliedTime", System.currentTimeMillis());
      tag.put("ElementAttachment", elementData);
      sword.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return sword;
   }
}
