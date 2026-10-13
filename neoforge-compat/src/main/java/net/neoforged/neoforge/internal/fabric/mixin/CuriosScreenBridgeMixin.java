package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.client.gui.CuriosScreen;

/**
 * Marks the creative inventory screen (where Trinkets renders its slots) as a
 * {@link CuriosScreen} for curios-based screen checks.
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CuriosScreenBridgeMixin implements CuriosScreen {

    @Override
    public int getGuiLeft() {
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen) (Object) this;
        return (self.width - 176) / 2;
    }

    @Override
    public int getGuiTop() {
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen) (Object) this;
        return (self.height - 166) / 2;
    }

    @Override
    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
}
