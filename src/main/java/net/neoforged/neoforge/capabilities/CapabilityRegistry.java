package net.neoforged.neoforge.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime store for {@link RegisterCapabilitiesEvent} registrations. Capability
 * lookups ({@code level.getCapability}/{@code blockEntity.getCapability})
 * resolve through this table.
 */
public final class CapabilityRegistry {
    private record Entry<T>(RegisterCapabilitiesEvent.Capability<T> capability, BlockEntityType<?> type,
                            RegisterCapabilitiesEvent.CapabilityProvider<T, ?> provider) {
    }

    private static final List<Entry<?>> ENTRIES = new ArrayList<>();
    private static final Map<BlockEntityType<?>, List<Entry<?>>> BY_TYPE = new IdentityHashMap<>();

    private CapabilityRegistry() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void drain(RegisterCapabilitiesEvent event) {
        for (RegisterCapabilitiesEvent.Registration<?, ?> reg : event.getRegistrations()) {
            Entry<?> entry = new Entry<>((RegisterCapabilitiesEvent.Capability) reg.capability(), reg.type(), reg.provider());
            ENTRIES.add(entry);
            BY_TYPE.computeIfAbsent(reg.type(), k -> new ArrayList<>()).add(entry);
        }
    }

    /** First-capability-wins lookup matching NeoForge semantics. */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getCapability(RegisterCapabilitiesEvent.Capability<T> capability, BlockEntity blockEntity, @Nullable Direction context) {
        List<Entry<?>> entries = BY_TYPE.get(blockEntity.getType());
        if (entries == null) {
            return null;
        }
        for (Entry<?> entry : entries) {
            if (entry.capability() == capability) {
                return ((RegisterCapabilitiesEvent.CapabilityProvider<T, BlockEntity>) entry.provider()).get(blockEntity, context);
            }
        }
        return null;
    }
}
