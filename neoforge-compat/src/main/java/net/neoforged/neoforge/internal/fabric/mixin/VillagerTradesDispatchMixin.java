package net.neoforged.neoforge.internal.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fires {@link VillagerTradesEvent} once per profession the first time a
 * villager of that profession builds trades — mirroring NeoForge's patch that
 * lets the event mutate the global trade table.
 */
@Mixin(Villager.class)
public abstract class VillagerTradesDispatchMixin {
    private static final Set<VillagerProfession> FIRED = ConcurrentHashMap.newKeySet();

    @Inject(method = "updateTrades", at = @At("HEAD"))
    private void neoforgeCompat$villagerTrades(CallbackInfo ci) {
        Villager self = (Villager) (Object) this;
        VillagerProfession profession = self.getVillagerData().getProfession();
        if (!FIRED.add(profession)) {
            return;
        }
        Int2ObjectMap<VillagerTrades.ItemListing[]> professionTrades = VillagerTrades.TRADES.get(profession);
        if (professionTrades == null) {
            return;
        }
        Int2ObjectMap<List<VillagerTrades.ItemListing>> mutable = new Int2ObjectOpenHashMap<>();
        professionTrades.int2ObjectEntrySet().forEach(entry ->
                mutable.put(entry.getIntKey(), new ArrayList<>(List.of(entry.getValue()))));
        NeoForge.EVENT_BUS.post(new VillagerTradesEvent(mutable, profession));
        Int2ObjectMap<VillagerTrades.ItemListing[]> rebuilt = new Int2ObjectOpenHashMap<>();
        mutable.int2ObjectEntrySet().forEach(entry ->
                rebuilt.put(entry.getIntKey(), entry.getValue().toArray(new VillagerTrades.ItemListing[0])));
        VillagerTrades.TRADES.put(profession, rebuilt);
    }
}
