package net.neoforged.neoforge.internal.fabric.mixin;

import net.neoforged.neoforge.internal.fabric.GlobalLootModifierManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies loaded global loot modifiers to the generated item list, matching
 * NeoForge's behavior of running GLMs around {@code getRandomItems}.
 */
@Mixin(LootTable.class)
public abstract class LootTableMixin {
    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At("RETURN"), cancellable = true)
    private void irons_spellbooks$applyGlobalLootModifiers(LootContext context,
                                                           CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        cir.setReturnValue(GlobalLootModifierManager.apply((LootTable) (Object) this, cir.getReturnValue(), context));
    }
}
