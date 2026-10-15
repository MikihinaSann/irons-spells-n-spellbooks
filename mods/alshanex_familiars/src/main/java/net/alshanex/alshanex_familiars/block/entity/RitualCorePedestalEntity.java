package net.alshanex.alshanex_familiars.block.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import javax.annotation.Nonnull;
import net.alshanex.alshanex_familiars.AlshanexFamiliarsMod;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.alshanex_familiars.util.rituals.RitualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RitualCorePedestalEntity extends BlockEntity {
   private static final String NBT_HELD_ITEM = "heldItem";
   private static final String NBT_ITEM_IN_RECIPE = "itemInRecipe";
   private static final String NBT_IS_VALID_RITUAL = "isValidRitual";
   private static final String NBT_COUNTER = "counter";
   private ItemStack heldItem = ItemStack.EMPTY;
   private boolean itemInRecipe = false;
   private boolean isValidRitual = false;
   private int counter = 0;

   public RitualCorePedestalEntity(BlockPos pPos, BlockState pBlockState) {
      super(BlockEntityRegistry.RITUAL_CORE.get(), pPos, pBlockState);
   }

   public ItemStack getHeldItem() {
      return this.heldItem;
   }

   public void setHeldItem(ItemStack newItem) {
      this.heldItem = newItem;
      this.counter = 0;
      this.isValidRitual = false;
      this.itemInRecipe = false;
      if (!newItem.isEmpty() && RitualHelper.isItemInRecipeRecipe(this.level, newItem)) {
         this.itemInRecipe = true;
      }

      this.setChanged();
   }

   public void drops() {
      SimpleContainer simpleContainer = new SimpleContainer(new ItemStack[]{this.heldItem});
      Containers.dropContents(this.level, this.worldPosition, simpleContainer);
   }

   protected void loadAdditional(CompoundTag pTag, Provider pRegistries) {
      super.loadAdditional(pTag, pRegistries);
      this.readNBT(pTag, pRegistries);
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, Provider registryAccess) {
      this.writeNBT(tag, registryAccess);
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider pRegistries) {
      return this.saveWithoutMetadata(pRegistries);
   }

   private CompoundTag writeNBT(CompoundTag nbt, Provider pRegistries) {
      if (!this.heldItem.isEmpty()) {
         nbt.put("heldItem", this.heldItem.save(pRegistries));
      } else {
         nbt.put("heldItem", new CompoundTag());
      }

      nbt.putBoolean("itemInRecipe", this.itemInRecipe);
      nbt.putBoolean("isValidRitual", this.isValidRitual);
      nbt.putInt("counter", this.counter);
      return nbt;
   }

   private CompoundTag readNBT(CompoundTag nbt, Provider pRegistries) {
      if (nbt.contains("heldItem")) {
         this.heldItem = ItemStack.parseOptional(pRegistries, nbt.getCompound("heldItem"));
      }

      this.itemInRecipe = nbt.getBoolean("itemInRecipe");
      this.isValidRitual = nbt.getBoolean("isValidRitual");
      this.counter = nbt.getInt("counter");
      return nbt;
   }

   public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
      if (!pLevel.isClientSide()) {
         if (this.itemInRecipe && !this.heldItem.isEmpty()) {
            boolean wasValidRitual = this.isValidRitual;
            this.isValidRitual = RitualHelper.isValidRecipe(pLevel, pPos, this.getHeldItem());
            if (!wasValidRitual && this.isValidRitual) {
               AlshanexFamiliarsMod.LOGGER.debug("Ritual started for item: " + this.heldItem.getItem());
            }
         } else {
            this.isValidRitual = false;
            this.counter = 0;
         }

         if (this.isValidRitual) {
            if (this.counter < 100) {
               this.counter++;
               if (this.counter % 10 == 0) {
                  Item result = RitualHelper.getResultForRecipe(pLevel, pPos, this.getHeldItem());
                  if (result != null) {
                     ParticleOptions particles = RitualHelper.getParticleForRecipe(pLevel, pPos, this.getHeldItem());
                     if (particles != null) {
                        RitualHelper.spawnParticlesAtPedestals(particles, pPos, pLevel);
                     }
                  }
               }
            }

            if (this.counter >= 100) {
               this.completeRitual(pLevel, pPos, pState);
            }
         }
      }
   }

   private void completeRitual(Level pLevel, BlockPos pPos, BlockState pState) {
      Item result = RitualHelper.getResultForRecipe(pLevel, pPos, this.getHeldItem());
      if (result != null) {
         MagicManager.spawnParticles(
            pLevel,
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.EVOCATION.get()).getTargetingColor(), 6.0F),
            pPos.getX(),
            pPos.getY() + 0.165F,
            pPos.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         RitualHelper.clearItemsFromPedestals(pLevel, pPos);
         BlockState prevState = this.getBlockState();
         this.setHeldItem(new ItemStack(result));
         BlockState nextState = this.getBlockState();
         pLevel.sendBlockUpdated(pPos, prevState, nextState, 2);
         AlshanexFamiliarsMod.LOGGER.debug("Ritual completed! Result: " + result);
      }

      this.counter = 0;
      this.itemInRecipe = false;
      this.isValidRitual = false;
   }
}
