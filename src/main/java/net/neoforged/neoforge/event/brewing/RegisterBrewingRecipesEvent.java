package net.neoforged.neoforge.event.brewing;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.bus.api.Event;

/**
 * Wraps Fabric's brewing recipe registration — collects mixes which the
 * platform bridge replays onto {@code FabricBrewingRecipeRegistryBuilder}.
 */
public class RegisterBrewingRecipesEvent extends Event {
    private final PotionBrewing.Builder builder;

    public RegisterBrewingRecipesEvent(PotionBrewing.Builder builder) {
        this.builder = builder;
    }

    public PotionBrewing.Builder getBuilder() {
        return builder;
    }
}
