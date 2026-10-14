package com.github.L_Ender.cataclysm.message;

import com.github.L_Ender.cataclysm.client.gui.MinistrosityInventoryScreen;
import com.github.L_Ender.cataclysm.client.sound.MeatShredderSound;
import com.github.L_Ender.cataclysm.config.CMClientConfig;
import com.github.L_Ender.cataclysm.entity.Pet.Netherite_Ministrosity_Entity;
import com.github.L_Ender.cataclysm.inventory.MinistrostiyMenu;
import com.github.L_Ender.cataclysm.client.event.CameraZoomManager;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-only packet handler bodies. Kept in a separate class so the message
 * classes never reference {@code net.minecraft.client} types — on a dedicated
 * server, class verification of a method containing such references loads the
 * stripped client class and crashes startup.
 */
public class ClientPacketHandlers {

    public static void openGuardInventory(MessageOpenInventory packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Entity entity = player.level().getEntity(packet.entityId());
            if (entity instanceof Netherite_Ministrosity_Entity guard) {
                LocalPlayer clientplayerentity = Minecraft.getInstance().player;
                int i = guard.getInventoryColumns();
                MinistrostiyMenu container = new MinistrostiyMenu(packet.id(), player.getInventory(), guard.miniInventory, guard);
                clientplayerentity.containerMenu = container;
                Minecraft.getInstance().setScreen(new MinistrosityInventoryScreen(container, player.getInventory(), guard,i));
            }
        }
    }

    public static void cameraZoom(MessageCameraZoom payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityID());
        if (entity instanceof Player player) {
            if (player == Minecraft.getInstance().player && Minecraft.getInstance().getCameraEntity() == Minecraft.getInstance().player) {
                CameraZoomManager.startZoom(payload.duration(),payload.distance(),payload.N(),payload.U(),payload.M());
            }
        }
    }

    public static void firstPersonCameraSwitch(MessageEntityCameraSwitch payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId);
        if (entity instanceof Player player) {
            if (CMClientConfig.firstPerson) {
                if (player == Minecraft.getInstance().player && Minecraft.getInstance().getCameraEntity() == Minecraft.getInstance().player) {
                    Minecraft.getInstance().setCameraEntity(entity);
                    Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
                }
            }
        }
    }

    public static void thirdPersonCameraSwitch(MessageEntityCameraSwitch payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId);
        if (entity instanceof Player player) {
            if (CMClientConfig.thirdPerson) {
                if (player == Minecraft.getInstance().player && Minecraft.getInstance().getCameraEntity() == Minecraft.getInstance().player) {
                    Minecraft.getInstance().setCameraEntity(entity);
                    Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
                }
            }
        }
    }

    public static void meatShredderSound(MessageMeatShredder message, IPayloadContext ctx) {
        Entity entity = ctx.player().level().getEntity(message.entityID());
        if (entity instanceof LivingEntity living) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(new MeatShredderSound(living));
        }
    }
}
