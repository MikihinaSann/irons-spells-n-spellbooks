package net.acetheeldritchking.cataclysm_spellbooks.items.armor;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.init.ModKeybind;
import com.github.L_Ender.cataclysm.items.KeybindUsingArmor;
import com.github.L_Ender.cataclysm.message.MessageArmorKey;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class MechanicalFlightArmorItem extends ImbuableCSArmorItem implements KeybindUsingArmor {
   public static boolean hover;

   public MechanicalFlightArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributeContainers) {
      super(material, type, properties, attributeContainers);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return this.type == Type.CHESTPLATE;
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return this.type == Type.CHESTPLATE ? ElytraItem.isFlyEnabled(stack) : false;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
      if (this.type == Type.HELMET) {
         tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_helmet.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(
            Component.translatable("item.cataclysm_spellbooks.excelsius_helmet.desc2", new Object[]{ModKeybind.HELMET_KEY_ABILITY.getTranslatedKeyMessage()})
               .withStyle(ChatFormatting.DARK_GREEN)
         );
      }

      if (this.type == Type.CHESTPLATE) {
         tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_chestplate.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_chestplate.desc2").withStyle(ChatFormatting.DARK_GREEN));
      }

      if (this.type == Type.LEGGINGS) {
         tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_leggings.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_leggings.desc2").withStyle(ChatFormatting.DARK_GREEN));
      }

      if (this.type == Type.BOOTS) {
         if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_boots.desc").withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(
               Component.translatable("item.cataclysm_spellbooks.excelsius_boots.desc2", new Object[]{ModKeybind.BOOTS_KEY_ABILITY.getTranslatedKeyMessage()})
                  .withStyle(ChatFormatting.DARK_GREEN)
            );
            tooltip.add(
               Component.translatable("item.cataclysm_spellbooks.excelsius_boots.desc3", new Object[]{ModKeybind.BOOTS_KEY_ABILITY.getTranslatedKeyMessage()})
                  .withStyle(ChatFormatting.DARK_GREEN)
            );
            tooltip.add(
               Component.translatable("item.cataclysm_spellbooks.excelsius_boots.desc4", new Object[]{ModKeybind.BOOTS_KEY_ABILITY.getTranslatedKeyMessage()})
                  .withStyle(ChatFormatting.DARK_GREEN)
            );
         } else {
            tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_boots.desc").withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(Component.translatable("item.cataclysm_spellbooks.excelsius_boots.desc_shift").withStyle(ChatFormatting.DARK_GREEN));
         }
      }
   }

   @Override
   public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
      if (pEntity instanceof Player player) {
         if ((
               this.type == Type.CHESTPLATE
                     && player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.EXCELSIUS_SPEED_CHESTPLATE.get())
                     && player.isFallFlying()
                  || this.type == Type.CHESTPLATE
                     && player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.EXCELSIUS_POWER_CHESTPLATE.get())
                     && player.isFallFlying()
                  || this.type == Type.CHESTPLATE
                     && player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.EXCELSIUS_RESIST_CHESTPLATE.get())
                     && player.isFallFlying()
            )
            && !player.isCrouching()) {
            Vec3 playerMotion = player.getDeltaMovement().add(player.getLookAngle()).normalize();
            Vec3 playerVelocity = player.getDeltaMovement();
            Vec3 playerRotation = player.getForward();
            double speed = 0.25 * (player.getXRot() < -75.0F && player.getXRot() > -105.0F ? 3.0F : 1.5F) / 1.5;
            player.setDeltaMovement(
               playerVelocity.add(
                     playerRotation.x * speed + (playerRotation.x * 1.5 - playerVelocity.x) * speed,
                     playerRotation.y * speed + (playerRotation.y * 1.5 - playerVelocity.y) * speed,
                     playerRotation.z * speed + (playerRotation.z * 1.5 - playerVelocity.z) * speed
                  )
                  .normalize()
            );
            if (player.tickCount % 3 == 0) {
               if (player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.EXCELSIUS_POWER_CHESTPLATE.get())) {
                  Vec3 vec3 = player.getDeltaMovement();
                  double d0 = player.getX() - vec3.x;
                  double d1 = player.getY() - vec3.y;
                  double d2 = player.getZ() - vec3.z;
                  int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 4);

                  for (int j = 0; j < count; j++) {
                     Vec3 random = Utils.getRandomVec3(0.25);
                     float f = (float)j / count;
                     double x = Mth.lerp(f, d0, player.getX());
                     double y = Mth.lerp(f, d1, player.getY());
                     double z = Mth.lerp(f, d2, player.getZ());
                     player.level()
                        .addParticle(ParticleHelper.FIRE, x + 0.6 - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                     player.level()
                        .addParticle(ParticleHelper.FIRE, x - 0.6 - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                  }
               }

               if (player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.EXCELSIUS_SPEED_CHESTPLATE.get())) {
                  Vec3 vec3 = player.getDeltaMovement();
                  double d0 = player.getX() - vec3.x;
                  double d1 = player.getY() - vec3.y;
                  double d2 = player.getZ() - vec3.z;
                  int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 4);

                  for (int j = 0; j < count; j++) {
                     Vec3 random = Utils.getRandomVec3(0.25);
                     float f = (float)j / count;
                     double x = Mth.lerp(f, d0, player.getX());
                     double y = Mth.lerp(f, d1, player.getY());
                     double z = Mth.lerp(f, d2, player.getZ());
                     player.level()
                        .addParticle(
                           ParticleHelper.FIRE, x + 0.5 - random.x, y + 0.2 + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5
                        );
                     player.level()
                        .addParticle(
                           ParticleHelper.FIRE, x - 0.5 - random.x, y + 0.2 + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5
                        );
                     player.level()
                        .addParticle(
                           ParticleHelper.FIRE, x + 0.5 - random.x, y - 0.5 + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5
                        );
                     player.level()
                        .addParticle(
                           ParticleHelper.FIRE, x - 0.5 - random.x, y - 0.5 + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5
                        );
                  }
               }

               if (player.getItemBySlot(EquipmentSlot.CHEST).is((Item)ItemRegistries.EXCELSIUS_RESIST_CHESTPLATE.get())) {
                  Vec3 vec3 = player.getDeltaMovement();
                  double d0 = player.getX() - vec3.x;
                  double d1 = player.getY() - vec3.y;
                  double d2 = player.getZ() - vec3.z;
                  int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 4);

                  for (int j = 0; j < count; j++) {
                     Vec3 random = Utils.getRandomVec3(0.25);
                     float f = (float)j / count;
                     double x = Mth.lerp(f, d0, player.getX());
                     double y = Mth.lerp(f, d1, player.getY());
                     double z = Mth.lerp(f, d2, player.getZ());
                     player.level()
                        .addParticle(ParticleHelper.FIRE, x + 0.7 - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                     player.level()
                        .addParticle(ParticleHelper.FIRE, x - 0.7 - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                     player.level()
                        .addParticle(ParticleHelper.FIRE, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                  }
               }
            }
         }

         if ((
               player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ItemRegistries.EXCELSIUS_SPEED_HELMET.get()
                  || player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ItemRegistries.EXCELSIUS_POWER_HELMET.get()
                  || player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ItemRegistries.EXCELSIUS_RESIST_HELMET.get()
            )
            && pLevel.isClientSide()
            && ModKeybind.HELMET_KEY_ABILITY.isDown()) {
            PacketDistributor.sendToServer(new MessageArmorKey(EquipmentSlot.HEAD.ordinal(), player.getId(), 5), new CustomPacketPayload[0]);
            this.onKeyPacket(player, pStack, 5);
         }

         if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == ItemRegistries.EXCELSIUS_WARLOCK_BOOTS.get()
            && pLevel.isClientSide()
            && ModKeybind.BOOTS_KEY_ABILITY.isDown()) {
            PacketDistributor.sendToServer(new MessageArmorKey(EquipmentSlot.HEAD.ordinal(), player.getId(), 7), new CustomPacketPayload[0]);
            this.onKeyPacket(player, pStack, 7);
         }
      }
   }

   public void onKeyPacket(Player player, ItemStack itemStack, int i) {
      if (i == 5
         && (
            player != null && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.EXCELSIUS_SPEED_HELMET.get())
               || player != null && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.EXCELSIUS_POWER_HELMET.get())
               || player != null && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.EXCELSIUS_RESIST_HELMET.get())
         )) {
         boolean flag = false;

         for (Entity entities : player.level().getEntities(player, player.getBoundingBox().inflate(24.0))) {
            if (entities instanceof LivingEntity livingEntity) {
               flag = true;
               livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.GUIDING_BOLT, 200));
            }
         }

         if (flag) {
            player.getCooldowns().addCooldown((Item)ItemRegistries.EXCELSIUS_SPEED_HELMET.get(), 200);
            player.getCooldowns().addCooldown((Item)ItemRegistries.EXCELSIUS_POWER_HELMET.get(), 200);
            player.getCooldowns().addCooldown((Item)ItemRegistries.EXCELSIUS_RESIST_HELMET.get(), 200);
         }
      }

      if (i == 7 && player != null && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.EXCELSIUS_WARLOCK_BOOTS.get())) {
         if (!player.isFallFlying() && !player.isCrouching()) {
            if (!this.isHovering()) {
               for (Entity entitiesx : player.level().getEntities(player, player.getBoundingBox().inflate(5.0))) {
                  if (entitiesx instanceof LivingEntity livingEntity) {
                     livingEntity.setRemainingFireTicks(100);
                  }
               }

               Vec3 up = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().add(0.0, 1.0, 0.0);
               player.setDeltaMovement(up.x, up.y, up.z);
               Vec3 vec3 = player.getDeltaMovement();
               double d0 = player.getX() - vec3.x;
               double d1 = player.getY() - vec3.y;
               double d2 = player.getZ() - vec3.z;
               int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 4);

               for (int j = 0; j < count; j++) {
                  Vec3 random = Utils.getRandomVec3(0.25);
                  float f = (float)j / count;
                  double x = Mth.lerp(f, d0, player.getX());
                  double y = Mth.lerp(f, d1, player.getY());
                  double z = Mth.lerp(f, d2, player.getZ());
                  player.level()
                     .addParticle(ParticleTypes.LARGE_SMOKE, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                  player.level()
                     .addParticle(ParticleHelper.EMBERS, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
               }

               player.level().playSound(player, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.55F, 1.0F);
            } else if (this.isHovering()) {
               for (Entity entitiesxx : player.level().getEntities(player, player.getBoundingBox().inflate(5.0))) {
                  if (entitiesxx instanceof LivingEntity livingEntity) {
                     livingEntity.setRemainingFireTicks(100);
                  }
               }

               Vec3 up = player.getLookAngle().multiply(0.2, 0.0, 0.2).normalize().add(0.0, 0.1, 0.0);
               player.setDeltaMovement(up.x, up.y, up.z);
               Vec3 vec3 = player.getDeltaMovement();
               double d0 = player.getX() - vec3.x;
               double d1 = player.getY() - vec3.y;
               double d2 = player.getZ() - vec3.z;
               int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 4);

               for (int j = 0; j < count; j++) {
                  Vec3 random = Utils.getRandomVec3(0.25);
                  float f = (float)j / count;
                  double x = Mth.lerp(f, d0, player.getX());
                  double y = Mth.lerp(f, d1, player.getY());
                  double z = Mth.lerp(f, d2, player.getZ());
                  player.level()
                     .addParticle(ParticleTypes.LARGE_SMOKE, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
                  player.level()
                     .addParticle(ParticleHelper.EMBERS, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
               }

               player.level().playSound(player, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.55F, 0.75F);
            }
         } else if (player.isFallFlying() && !player.isCrouching()) {
            for (Entity entitiesxxx : player.level().getEntities(player, player.getBoundingBox().inflate(5.0))) {
               if (entitiesxxx instanceof LivingEntity livingEntity) {
                  livingEntity.setRemainingFireTicks(300);
               }
            }

            double x = player.getX();
            double y = player.getY() + player.getBbHeight() / 2.0F;
            double z = player.getZ();
            float yaw = (float)Math.toRadians(-player.getYRot());
            float yaw2 = (float)Math.toRadians(-player.getYRot() + 180.0F);
            float pitch = (float)Math.toRadians(-player.getXRot());
            player.level().addParticle(new RingParticleOptions(yaw, pitch, 40, 86, 236, 204, 1.0F, 50.0F, false, 2), x, y, z, 0.0, 0.0, 0.0);
            player.level().addParticle(new RingParticleOptions(yaw2, pitch, 40, 86, 236, 204, 1.0F, 50.0F, false, 2), x, y, z, 0.0, 0.0, 0.0);
            player.level().playSound(player, player.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 0.55F, 1.0F);
         } else if (!player.isFallFlying() && player.isCrouching()) {
            if (player.isNoGravity()) {
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("display.cataclysm_spellbooks.hover_disabled").withStyle(s -> s.withColor(TextColor.fromRgb(16241221)))
                        )
                     );
                  serverPlayer.level()
                     .playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.1F);
               }

               Vec3 down = player.getLookAngle().multiply(0.0, 0.0, 0.0).normalize().add(0.0, -0.1, 0.0);
               player.setDeltaMovement(down.x, down.y, down.z);
               player.setNoGravity(false);
               this.setHovering(false);
            } else {
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("display.cataclysm_spellbooks.hover_enabled").withStyle(s -> s.withColor(TextColor.fromRgb(16241221)))
                        )
                     );
                  serverPlayer.level()
                     .playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.1F);
               }

               player.setNoGravity(true);
               this.setHovering(true);
               Vec3 up = player.getLookAngle().multiply(0.2, 0.0, 0.2).normalize().add(0.0, 0.2, 0.0);
               player.setDeltaMovement(up.x, up.y, up.z);
            }

            Vec3 vec3 = player.getDeltaMovement();
            double d0 = player.getX() - vec3.x;
            double d1 = player.getY() - vec3.y;
            double d2 = player.getZ() - vec3.z;
            int count = Mth.clamp((int)(vec3.lengthSqr() * 2.0), 1, 2);

            for (int j = 0; j < count; j++) {
               Vec3 random = Utils.getRandomVec3(0.25);
               float f = (float)j / count;
               double x = Mth.lerp(f, d0, player.getX());
               double y = Mth.lerp(f, d1, player.getY());
               double z = Mth.lerp(f, d2, player.getZ());
               player.level()
                  .addParticle(ParticleTypes.LARGE_SMOKE, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
               player.level()
                  .addParticle(ParticleHelper.EMBERS, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
            }
         }

         player.getCooldowns().addCooldown((Item)ItemRegistries.EXCELSIUS_WARLOCK_BOOTS.get(), 60);
      }
   }

   public boolean isHovering() {
      return hover;
   }

   public void setHovering(boolean value) {
      hover = value;
   }
}
