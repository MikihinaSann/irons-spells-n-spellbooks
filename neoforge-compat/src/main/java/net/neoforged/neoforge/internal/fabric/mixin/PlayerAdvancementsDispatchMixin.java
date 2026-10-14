package net.neoforged.neoforge.internal.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

/**
 * Dispatches {@link AdvancementEvent.AdvancementEarnEvent} from
 * {@link PlayerAdvancements#award}: when the call completed the advancement
 * (returned true and progress is now done).
 */
@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsDispatchMixin {

    @Shadow
    private ServerPlayer player;

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

    @Inject(method = "award", at = @At("RETURN"))
    private void neoforge$advancementEarned(AdvancementHolder advancement, String criterion,
            CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this.getOrStartProgress(advancement).isDone()) {
            NeoForge.EVENT_BUS.post(new AdvancementEvent.AdvancementEarnEvent(this.player, advancement));
        }
    }
}
