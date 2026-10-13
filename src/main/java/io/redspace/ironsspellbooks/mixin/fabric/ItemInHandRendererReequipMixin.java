package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mirrors NeoForge's {@code ClientHooks.shouldCauseReequipAnimation} patch in
 * {@code ItemInHandRenderer.tick}. Vanilla drives the re-equip animation purely
 * by stack reference identity; NeoForge lets the item veto it
 * ({@code Item.shouldCauseReequipAnimation}). When the item declines, the
 * cached stack is synced so the identity checks below see old and new as equal;
 * when it asks for the animation, the compare is forced false.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererReequipMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    /** Last selected hotbar slot, mirroring NeoForge's {@code slotMainHand}. */
    @Unique
    private int irons_spellbooks$mainHandSlot;

    @Unique
    private boolean irons_spellbooks$reequipMain;

    @Unique
    private boolean irons_spellbooks$reequipOff;

    /** Same logic as NeoForge's {@code ClientHooks.shouldCauseReequipAnimation}. */
    @Unique
    private static boolean irons_spellbooks$shouldReequip(ItemStack from, ItemStack to, boolean slotChanged) {
        boolean fromEmpty = from.isEmpty();
        boolean toEmpty = to.isEmpty();
        if (fromEmpty && toEmpty) {
            return false;
        }
        if (fromEmpty != toEmpty) {
            return true;
        }
        return from.getItem().shouldCauseReequipAnimation(from, to, slotChanged);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private void irons_spellbooks$reequipCheck(CallbackInfo ci) {
        // reached only in the non-busy-hands branch, where vanilla then compares
        // mainHandItem/offHandItem against the freshly fetched stacks by identity
        LocalPlayer player = this.minecraft.player;
        int selected = player.getInventory().selected;
        boolean slotChanged = selected != this.irons_spellbooks$mainHandSlot;
        this.irons_spellbooks$mainHandSlot = selected;
        ItemStack main = player.getMainHandItem();
        this.irons_spellbooks$reequipMain = irons_spellbooks$shouldReequip(this.mainHandItem, main, slotChanged);
        if (!this.irons_spellbooks$reequipMain) {
            this.mainHandItem = main;
        }
        ItemStack off = player.getOffhandItem();
        this.irons_spellbooks$reequipOff = irons_spellbooks$shouldReequip(this.offHandItem, off, false);
        if (!this.irons_spellbooks$reequipOff) {
            this.offHandItem = off;
        }
    }

    @Redirect(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;mainHandItem:Lnet/minecraft/world/item/ItemStack;",
            opcode = Opcodes.GETFIELD, ordinal = 1))
    private ItemStack irons_spellbooks$mainHandForHeight(ItemInHandRenderer self) {
        // null guarantees the identity compare fails -> re-equip animation plays
        return this.irons_spellbooks$reequipMain ? null : this.mainHandItem;
    }

    @Redirect(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;offHandItem:Lnet/minecraft/world/item/ItemStack;",
            opcode = Opcodes.GETFIELD, ordinal = 1))
    private ItemStack irons_spellbooks$offHandForHeight(ItemInHandRenderer self) {
        return this.irons_spellbooks$reequipOff ? null : this.offHandItem;
    }
}
