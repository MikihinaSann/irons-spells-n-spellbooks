package top.theillusivec4.curios.api;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.capability.TrinketsBackedItemHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * CuriosApi implemented over Trinkets.
 */
public final class CuriosApi {

    private CuriosApi() {
    }

    public static Optional<ICuriosItemHandler> getCuriosInventory(@Nullable LivingEntity entity) {
        if (entity == null) {
            return Optional.empty();
        }
        return TrinketsApi.getTrinketComponent(entity)
                .map(component -> new TrinketsBackedItemHandler(component, entity));
    }

    public static Optional<TrinketComponent> getTrinketComponent(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity);
    }

    public static CuriosHelper getCuriosHelper() {
        return CuriosHelper.INSTANCE;
    }

    public static class CuriosHelper {
        static final CuriosHelper INSTANCE = new CuriosHelper();

        /**
         * Slot identifiers (Curios style — trinkets {@code group/name}) the item can
         * go into, based on the item's trinkets slot tags.
         */
        public Set<String> getCurioTags(Item item) {
            Set<String> slots = new java.util.HashSet<>();
            var holder = item.builtInRegistryHolder();
            holder.tags().forEach(tag -> {
                var loc = tag.location();
                if ("trinkets".equals(loc.getNamespace())) {
                    String path = loc.getPath();
                    int slash = path.indexOf('/');
                    if (slash > 0) {
                        slots.add(path.substring(slash + 1));
                    }
                }
            });
            return slots;
        }
    }
}
