package io.redspace.ironslib.transmog.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.util.MemoizedSupplier;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class TransmogClientHandler {
   protected static final MemoizedSupplier<HumanoidModel<LivingEntity>> INNER_ARMOR_MODEL = new MemoizedSupplier<>(
      () -> new HumanoidModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR))
   );
   protected static final MemoizedSupplier<HumanoidModel<LivingEntity>> OUTER_ARMOR_MODEL = new MemoizedSupplier<>(
      () -> new HumanoidModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))
   );
   private static final HashMap<ResourceLocation, MemoizedSupplier<TransmogArmorRenderer<?>>> TRANSMOGS = new HashMap<>();
   private static boolean isTransmogRenderActive;

   public static boolean isTransmogRenderActive() {
      return RenderSystem.isOnRenderThread() && isTransmogRenderActive;
   }

   public static void setTransmogRenderActive(boolean transmogRenderActive) {
      isTransmogRenderActive = transmogRenderActive;
   }

   public static boolean hideForTransmog(Player player, ItemStack stack) {
      return isTransmogRenderActive() && canUseTransmog(player, stack);
   }

   public static TransmogArmorRenderer<?> renderer(TransmogHolder transmogHolder) {
      return Objects.requireNonNull(TRANSMOGS.get(transmogHolder.id()), "No Renderer Registered for transmog: " + transmogHolder.id()).get();
   }

   public static boolean shouldDisableOuterLayer(Player player, EquipmentSlot equipmentSlot) {
      ItemStack stack = player.getInventory().getArmor(equipmentSlot.getIndex());
      if (stack.isEmpty()) {
         return false;
      } else {
         HumanoidModel<?> renderer;
         if (hideForTransmog(player, stack)) {
            renderer = renderer(TransmogItemData.get(stack).transmog());
         } else {
            renderer = GeoRenderProvider.of(stack)
               .getGeoArmorRenderer(player, stack, equipmentSlot, equipmentSlot == EquipmentSlot.LEGS ? INNER_ARMOR_MODEL.get() : OUTER_ARMOR_MODEL.get());
         }

         return !(renderer instanceof TransmogArmorRenderer<?> armor)
            ? false
            : armor.hideHat() && equipmentSlot == EquipmentSlot.HEAD || armor.hideJacket() && equipmentSlot != EquipmentSlot.HEAD;
      }
   }

   public static boolean canUseTransmog(Player player, ItemStack stack) {
      if (stack.isEmpty() || !TransmogItemData.has(stack)) {
         return false;
      } else if (((ITransmogPreview)player).irons_lib$isTransmogPreview()) {
         return true;
      } else {
         PatreonPermissions permission = PatreonData.getPatreonPermissions(player);
         TransmogHolder transmogHolder = TransmogItemData.get(stack).transmog();
         return transmogHolder != null && permission.canUse(transmogHolder);
      }
   }

   static {
      TRANSMOGS.put(
         IronsLib.id("red_rogue"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/rogue")).setHideHat().setHideJacket())
      );
      TRANSMOGS.put(
         IronsLib.id("standard_wizard_robes"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/wizard")).setHideJacket().setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("standard_wizard_hat"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(
                     new GenericArmorModel(IronsLib.id("geo/transmog/wizard_armor_hat.geo.json"), IronsLib.id("textures/models/armor/transmog/wizard.png"))
                  )
                  .setHideJacket()
                  .setDyeable()
         )
      );
      TRANSMOGS.put(
         IronsLib.id("silver_sorcerer"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/silver_sorcerer")).setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("adventurer"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/adventurer")).setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("witchhunter"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/witchhunter")).setDyeable().withCapeBone("cape"))
      );
      TRANSMOGS.put(
         IronsLib.id("traveler"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/traveler"))
                  .setDyeable()
                  .withCapeBone(
                     new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.5, 0.5, 0.5)),
                     new TransmogArmorRenderer.CapeBone("leg_cape", new Vec3(1.0, 0.0, 0.0))
                  )
         )
      );
      TRANSMOGS.put(
         IronsLib.id("battlemage"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/battlemage")).setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("ruby_warrior"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/ruby_warrior")).setHideHat())
      );
      TRANSMOGS.put(
         IronsLib.id("onyx_shadow"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/onyx_shadow"))
                  .setHideJacket()
                  .setHideHat()
                  .withCapeBone(new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.85F, 0.85F, 0.85F)))
         )
      );
      TRANSMOGS.put(
         IronsLib.id("sapphire_scholar"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/sapphire_scholar"))
                  .setHideJacket()
                  .setHideHat()
                  .withCapeBone(new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.85F, 0.85F, 0.85F)))
                  .withCapeBone(new TransmogArmorRenderer.CapeBone("rightArmSleeveCape", new Vec3(0.0, 0.0, 1.5)))
                  .withCapeBone(new TransmogArmorRenderer.CapeBone("leftArmSleeveCape", new Vec3(0.0, 0.0, 1.5)))
         )
      );
   }
}
