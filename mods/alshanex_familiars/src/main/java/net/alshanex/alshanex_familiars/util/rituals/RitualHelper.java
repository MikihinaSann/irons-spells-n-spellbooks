package net.alshanex.alshanex_familiars.util.rituals;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.alshanex.alshanex_familiars.block.entity.FamiliarStatueEntity;
import net.alshanex.alshanex_familiars.block.entity.RitualCorePedestalEntity;
import net.alshanex.alshanex_familiars.recipe_types.RitualRecipeType;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RitualHelper {
   private static Optional<RitualRecipeType> findMatchingRecipe(Level level, BlockPos centralPos, ItemStack centralStack) {
      if (centralStack != null && !centralStack.isEmpty()) {
         List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centralPos, level);
         List<ItemStack> pedestalItems = getItemStacksFromPedestals(surroundingPedestals);
         RitualRecipeType.Input recipeInput = new RitualRecipeType.Input(centralStack, pedestalItems);
         RecipeManager manager = level.getRecipeManager();
         return manager.getAllRecipesFor((RecipeType<RitualRecipeType>)(RecipeType<?>)RecipeRegistry.RITUAL_RECIPE_TYPE.get())
            .stream()
            .<RitualRecipeType>map(RecipeHolder::value)
            .filter(recipe -> recipe.matches(recipeInput, level))
            .findFirst();
      } else {
         return Optional.empty();
      }
   }

   public static boolean isValidRecipe(Level level, BlockPos centralPos, ItemStack stack) {
      if (stack != null && level != null) {
         List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centralPos, level);
         return surroundingPedestals.size() < 4 ? false : findMatchingRecipe(level, centralPos, stack).isPresent();
      } else {
         return false;
      }
   }

   public static Item getResultForRecipe(Level level, BlockPos centerPos, ItemStack stack) {
      return stack != null && level != null
         ? findMatchingRecipe(level, centerPos, stack).map(RitualRecipeType::result).<Item>map(ItemStack::getItem).orElse(ItemStack.EMPTY.getItem())
         : ItemStack.EMPTY.getItem();
   }

   public static boolean isItemInRecipeRecipe(Level level, ItemStack stack) {
      if (stack != null && level != null) {
         RecipeManager manager = level.getRecipeManager();
         return manager.getAllRecipesFor((RecipeType<RitualRecipeType>)(RecipeType<?>)RecipeRegistry.RITUAL_RECIPE_TYPE.get())
            .stream()
            .<RitualRecipeType>map(RecipeHolder::value)
            .anyMatch(recipe -> recipe.centralItem().test(stack));
      } else {
         return false;
      }
   }

   public static ParticleOptions getParticleForRecipe(Level level, BlockPos centerPos, ItemStack stack) {
      return (ParticleOptions)(stack != null && level != null
         ? findMatchingRecipe(level, centerPos, stack)
            .map(RitualRecipeType::defaultParticle)
            .map(holder -> holder.value() instanceof SimpleParticleType simple ? simple : ParticleTypes.ENCHANT)
            .orElse(ParticleTypes.ENCHANT)
         : ParticleTypes.ENCHANT);
   }

   public static void clearItemsFromPedestals(Level level, BlockPos pos) {
      BlockEntity centralEntity = level.getBlockEntity(pos);
      ItemStack centralStack = ItemStack.EMPTY;
      if (centralEntity instanceof RitualCorePedestalEntity core) {
         centralStack = core.getHeldItem();
      }

      Optional<RitualRecipeType> recipeOptional = findMatchingRecipe(level, pos, centralStack);
      if (recipeOptional.isPresent()) {
         RitualRecipeType recipe = recipeOptional.get();
         List<BlockEntity> pedestals = getSurroundingPedestals(pos, level);
         List<FamiliarStatueEntity> availablePedestals = pedestals.stream()
            .filter(be -> be instanceof FamiliarStatueEntity s && !s.getHeldItem().isEmpty())
            .map(be -> (FamiliarStatueEntity)be)
            .collect(Collectors.toCollection(ArrayList::new));

         for (RitualRecipeType.RitualIngredient ingredient : recipe.inputs()) {
            if (ingredient.consume()) {
               Iterator<FamiliarStatueEntity> iterator = availablePedestals.iterator();

               while (iterator.hasNext()) {
                  FamiliarStatueEntity pedestal = iterator.next();
                  ItemStack heldStack = pedestal.getHeldItem();
                  if (ingredient.ingredient().test(heldStack) && heldStack.getCount() >= ingredient.count()) {
                     BlockState prevState = pedestal.getBlockState();
                     pedestal.setHeldItem(ItemStack.EMPTY);
                     BlockState nextState = pedestal.getBlockState();
                     level.sendBlockUpdated(pedestal.getBlockPos(), prevState, nextState, 2);
                     iterator.remove();
                     break;
                  }
               }
            }
         }
      }
   }

   public static List<BlockEntity> getSurroundingPedestals(BlockPos origin, Level level) {
      BlockPos[] offsets = new BlockPos[]{origin.north(2), origin.south(2), origin.east(2), origin.west(2)};
      List<BlockEntity> pedestals = new ArrayList<>();

      for (BlockPos pos : offsets) {
         BlockEntity entity = level.getBlockEntity(pos);
         if (entity instanceof FamiliarStatueEntity) {
            pedestals.add(entity);
         }
      }

      return pedestals;
   }

   public static List<ItemStack> getItemStacksFromPedestals(List<BlockEntity> surroundingPedestals) {
      List<ItemStack> pedestalItems = new ArrayList<>();

      for (BlockEntity pedestal : surroundingPedestals) {
         if (pedestal instanceof FamiliarStatueEntity statue && !statue.getHeldItem().isEmpty()) {
            pedestalItems.add(statue.getHeldItem().copy());
         }
      }

      return pedestalItems;
   }

   public static void spawnParticlesAtPedestals(ParticleOptions particles, BlockPos origin, Level pLevel) {
      if (!pLevel.isClientSide()) {
         BlockPos[] offsets = new BlockPos[]{origin.north(2), origin.south(2), origin.east(2), origin.west(2)};
         MagicManager.spawnParticles(pLevel, particles, origin.getX(), origin.getY() + 2, origin.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);

         for (BlockPos pos : offsets) {
            MagicManager.spawnParticles(pLevel, particles, pos.getX(), pos.getY() + 2, pos.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         }
      }
   }
}
