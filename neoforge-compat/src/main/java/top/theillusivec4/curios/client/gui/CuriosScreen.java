package top.theillusivec4.curios.client.gui;

import net.minecraft.client.Minecraft;

/**
 * Curios CuriosScreen - marker interface implemented (via mixin) on the screen
 * that shows trinket slots, so curios-based screen checks match on Fabric.
 */
public interface CuriosScreen {

    int getGuiLeft();

    int getGuiTop();

    Minecraft getMinecraft();
}
