package top.theillusivec4.curios.api.type;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * Minimal Curios slot-type descriptor (data-driven slots handled by Trinkets).
 */
public interface ISlotType {
    String getIdentifier();

    int getSize();

    ResourceLocation getIcon();

    default Set<String> getValidators() {
        return Set.of();
    }

    default boolean renderCosmetic() {
        return false;
    }
}
