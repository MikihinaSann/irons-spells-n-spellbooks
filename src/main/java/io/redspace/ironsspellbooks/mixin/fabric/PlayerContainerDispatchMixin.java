package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

/**
 * Fires {@link PlayerContainerEvent.Open}/{@link PlayerContainerEvent.Close}
 * from {@code ServerPlayer#openMenu} and {@code doCloseContainer}.
 */
@Mixin(ServerPlayer.class)
public abstract class PlayerContainerDispatchMixin {

    @Inject(method = "openMenu", at = @At("RETURN"))
    private void irons_spellbooks$onMenuOpen(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir) {
        var self = (ServerPlayer) (Object) this;
        if (cir.getReturnValue().isPresent()) {
            NeoForge.EVENT_BUS.post(new PlayerContainerEvent.Open(self, self.containerMenu));
        }
    }

    @Inject(method = "doCloseContainer", at = @At("HEAD"))
    private void irons_spellbooks$onMenuClose(CallbackInfo ci) {
        var self = (ServerPlayer) (Object) this;
        NeoForge.EVENT_BUS.post(new PlayerContainerEvent.Close(self, self.containerMenu));
    }
}
