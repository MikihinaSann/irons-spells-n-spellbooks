package net.alshanex.alshanex_familiars.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.alshanex.alshanex_familiars.item.MonocleItem;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableIntegration;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem.ConsumableData;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem.ConsumableType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class MonocleOverlay implements Layer {
   private static final ResourceLocation SCOPE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/spyglass_scope.png");
   private static final int X_OFFSET = 10;

   public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.level != null && mc.options.getCameraType().isFirstPerson()) {
         ItemStack useItem = mc.player.getUseItem();
         if (useItem.getItem() instanceof MonocleItem && mc.player.isUsingItem()) {
            this.renderScopeEffect(guiGraphics, mc);
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
            AbstractSpellCastingPet targetPet = this.getTargetedPet(mc, partialTick);
            if (targetPet != null) {
               this.renderPetStats(guiGraphics, targetPet, mc);
            }
         }
      }
   }

   private void renderScopeEffect(GuiGraphics guiGraphics, Minecraft mc) {
      RenderSystem.enableBlend();
      int screenWidth = mc.getWindow().getGuiScaledWidth();
      int screenHeight = mc.getWindow().getGuiScaledHeight();
      float minDim = Math.min(screenWidth, screenHeight);
      float scale = Math.min(screenWidth / minDim, screenHeight / minDim) * minDim;
      int scaledWidth = Mth.floor(scale);
      int scaledHeight = Mth.floor(scale);
      int x = (screenWidth - scaledWidth) / 2;
      int y = (screenHeight - scaledHeight) / 2;
      guiGraphics.blit(SCOPE_LOCATION, x, y, -90, 0.0F, 0.0F, scaledWidth, scaledHeight, scaledWidth, scaledHeight);
      RenderSystem.defaultBlendFunc();
      guiGraphics.fill(RenderType.guiOverlay(), 0, 0, screenWidth, y, -16777216);
      guiGraphics.fill(RenderType.guiOverlay(), 0, y + scaledHeight, screenWidth, screenHeight, -16777216);
      guiGraphics.fill(RenderType.guiOverlay(), 0, y, x, y + scaledHeight, -16777216);
      guiGraphics.fill(RenderType.guiOverlay(), x + scaledWidth, y, screenWidth, y + scaledHeight, -16777216);
      RenderSystem.disableBlend();
   }

   private AbstractSpellCastingPet getTargetedPet(Minecraft mc, float partialTick) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity == null) {
         return null;
      } else {
         if (mc.hitResult != null && mc.hitResult.getType() == Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult)mc.hitResult;
            if (entityHit.getEntity() instanceof AbstractSpellCastingPet pet) {
               return pet;
            }
         }

         double distance = 64.0;
         Vec3 eyePos = cameraEntity.getEyePosition(partialTick);
         Vec3 viewVec = cameraEntity.getViewVector(partialTick);
         Vec3 endPos = eyePos.add(viewVec.x * distance, viewVec.y * distance, viewVec.z * distance);
         AABB searchBox = cameraEntity.getBoundingBox().expandTowards(viewVec.scale(distance)).inflate(1.0);
         EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
            cameraEntity, eyePos, endPos, searchBox, entity -> !entity.isSpectator() && entity.isPickable(), distance * distance
         );
         return hitResult != null && hitResult.getEntity() instanceof AbstractSpellCastingPet pet ? pet : null;
      }
   }

   private void renderPetStats(GuiGraphics guiGraphics, AbstractSpellCastingPet familiar, Minecraft mc) {
      ConsumableData data = FamiliarConsumableIntegration.getConsumableData(familiar);
      List<MonocleOverlay.StatEntry> stats = new ArrayList<>();
      int powerVal = data.getValue(ConsumableType.SPELL_POWER);
      int powerMax = ConsumableType.SPELL_POWER.getMaxLimit();
      stats.add(
         new MonocleOverlay.StatEntry(new ItemStack((ItemLike)ItemRegistry.MAGIC_POWER_TIER_1.get()), Component.literal(powerVal + "/" + powerMax + "%"))
      );
      int resistVal = data.getValue(ConsumableType.SPELL_RESIST);
      int resistMax = ConsumableType.SPELL_RESIST.getMaxLimit();
      stats.add(
         new MonocleOverlay.StatEntry(new ItemStack((ItemLike)ItemRegistry.MAGIC_RESIST_TIER_1.get()), Component.literal(resistVal + "/" + resistMax + "%"))
      );
      int screenHeight = mc.getWindow().getGuiScaledHeight();
      int totalContentHeight = stats.size() * 20;
      int startY = screenHeight / 2 - totalContentHeight / 2;

      for (int i = 0; i < stats.size(); i++) {
         MonocleOverlay.StatEntry stat = stats.get(i);
         int yPos = startY + i * 20;
         guiGraphics.renderFakeItem(stat.icon, 10, yPos);
         guiGraphics.drawString(mc.font, stat.text, 28, yPos + 4, 16777215, true);
      }
   }

   private record StatEntry(ItemStack icon, Component text) {
   }
}
