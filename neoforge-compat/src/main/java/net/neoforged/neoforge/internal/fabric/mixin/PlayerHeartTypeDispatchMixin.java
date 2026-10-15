package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link PlayerHeartTypeEvent} from {@code Gui.HeartType#forPlayer},
 * matching where NeoForge patches the heart-type lookup.
 */
@Mixin(Gui.HeartType.class)
public abstract class PlayerHeartTypeDispatchMixin {

    @Inject(method = "forPlayer", at = @At("RETURN"), cancellable = true)
    private static void irons_spellbooks$heartType(Player player,
                                                   CallbackInfoReturnable<Gui.HeartType> cir) {
        PlayerHeartTypeEvent event = new PlayerHeartTypeEvent(player, cir.getReturnValue());
        NeoForge.EVENT_BUS.post(event);
        if (event.getType() != event.getOriginalType()) {
            cir.setReturnValue(event.getType());
        }
    }
}
