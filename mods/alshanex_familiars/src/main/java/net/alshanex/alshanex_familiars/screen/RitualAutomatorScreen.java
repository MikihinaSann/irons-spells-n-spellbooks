package net.alshanex.alshanex_familiars.screen;

import java.util.List;
import net.alshanex.alshanex_familiars.block.entity.RitualAutomatorBlockEntity;
import net.alshanex.alshanex_familiars.network.ServerboundSetAutomatorRecipePacket;
import net.alshanex.alshanex_familiars.recipe_types.RitualRecipeType;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.PacketDistributor;

public class RitualAutomatorScreen extends Screen {
   private final BlockPos bePos;
   private List<RecipeHolder<RitualRecipeType>> allRecipes;
   private int index = 0;
   private int ingredientCycleTimer = 0;
   private static final int CYCLE_INTERVAL = 20;

   public RitualAutomatorScreen(BlockPos pos) {
      super(Component.literal("Ritual Automator"));
      this.bePos = pos;
   }

   protected void init() {
      if (this.minecraft != null && this.minecraft.level != null) {
         this.allRecipes = this.minecraft.level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeRegistry.RITUAL_RECIPE_TYPE.get());
         if (this.minecraft.level.getBlockEntity(this.bePos) instanceof RitualAutomatorBlockEntity automator) {
            ResourceLocation current = automator.getSelectedRecipeId();
            if (current != null) {
               for (int i = 0; i < this.allRecipes.size(); i++) {
                  if (this.allRecipes.get(i).id().equals(current)) {
                     this.index = i;
                     break;
                  }
               }
            }
         }

         int centerX = this.width / 2;
         int centerY = this.height / 2;
         this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (this.index > 0) {
               this.index--;
            }
         }).bounds(centerX - 60, centerY + 60, 20, 20).build());
         this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (this.index < this.allRecipes.size() - 1) {
               this.index++;
            }
         }).bounds(centerX + 40, centerY + 60, 20, 20).build());
         this.addRenderableWidget(Button.builder(Component.literal("Set Recipe"), button -> {
            this.sendUpdatePacket();
            this.onClose();
         }).bounds(centerX - 30, centerY + 85, 60, 20).build());
      }
   }

   private void sendUpdatePacket() {
      if (!this.allRecipes.isEmpty()) {
         ResourceLocation id = this.allRecipes.get(this.index).id();
         PacketDistributor.sendToServer(new ServerboundSetAutomatorRecipePacket(this.bePos, id), new CustomPacketPayload[0]);
      }
   }

   public void tick() {
      super.tick();
      this.ingredientCycleTimer++;
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      if (this.allRecipes != null && !this.allRecipes.isEmpty()) {
         RitualRecipeType recipe = (RitualRecipeType)this.allRecipes.get(this.index).value();
         guiGraphics.drawCenteredString(
            this.font,
            Component.literal("Select Recipe (" + (this.index + 1) + "/" + this.allRecipes.size() + ")"),
            this.width / 2,
            this.height / 2 - 80,
            16777215
         );
         ItemStack result = recipe.result();
         guiGraphics.renderItem(result, this.width / 2 - 8, this.height / 2 - 40);
         guiGraphics.drawCenteredString(this.font, result.getHoverName(), this.width / 2, this.height / 2 - 20, 11184810);
         List<RitualRecipeType.RitualIngredient> inputs = recipe.inputs();
         int startX = this.width / 2 - inputs.size() * 18 / 2;
         int y = this.height / 2 + 10;

         for (int i = 0; i < inputs.size(); i++) {
            RitualRecipeType.RitualIngredient input = inputs.get(i);
            ItemStack[] items = input.ingredient().getItems();
            if (items.length > 0) {
               int itemIndex = this.ingredientCycleTimer / 20 % items.length;
               ItemStack displayStack = items[itemIndex].copy();
               displayStack.setCount(input.count());
               guiGraphics.renderItem(displayStack, startX + i * 18, y);
            }
         }
      } else {
         guiGraphics.drawCenteredString(this.font, "No Ritual Recipes Found", this.width / 2, this.height / 2, 16711680);
      }
   }
}
