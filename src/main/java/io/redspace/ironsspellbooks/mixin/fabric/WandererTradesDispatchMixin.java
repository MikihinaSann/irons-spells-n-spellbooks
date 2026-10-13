package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.world.entity.npc.WanderingTrader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

/**
 * Fires {@link WandererTradesEvent} after the wandering trader builds its
 * offers; subscribed {@code ItemListing}s are evaluated and appended.
 */
@Mixin(WanderingTrader.class)
public abstract class WandererTradesDispatchMixin {

    @Inject(method = "updateTrades", at = @At("RETURN"))
    private void irons_spellbooks$wandererTrades(CallbackInfo ci) {
        var event = new WandererTradesEvent(new ArrayList<>(), new ArrayList<>());
        NeoForge.EVENT_BUS.post(event);
        var self = (WanderingTrader) (Object) this;
        var random = self.getRandom();
        for (var listing : event.getGenericTrades()) {
            var offer = listing.getOffer(self, random);
            if (offer != null) {
                self.getOffers().add(offer);
            }
        }
        for (var listing : event.getRareTrades()) {
            var offer = listing.getOffer(self, random);
            if (offer != null) {
                self.getOffers().add(offer);
            }
        }
    }
}
