package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * NeoForge patches {@code ItemStack.getDisplayName} and
 * {@code getTooltipLines} to apply {@code Rarity.getStyleModifier()} instead
 * of {@code Rarity.color()}. Extended rarities hold a null
 * {@link ChatFormatting}, so a null argument reliably marks the rarity
 * styling call — apply the operator form then.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackRarityStyleMixin {

    @WrapOperation(method = {"getDisplayName", "getTooltipLines"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent neoforge$rarityStyle(MutableComponent component,
            ChatFormatting formatting, Operation<MutableComponent> original) {
        if (formatting == null && ((Object) this) instanceof ItemStack stack) {
            Rarity rarity = stack.getRarity();
            if ((Object) rarity instanceof net.neoforged.neoforge.common.extensions.IRarityExtension ext) {
                return component.withStyle(ext.getStyleModifier());
            }
        }
        return original.call(component, formatting);
    }
}
