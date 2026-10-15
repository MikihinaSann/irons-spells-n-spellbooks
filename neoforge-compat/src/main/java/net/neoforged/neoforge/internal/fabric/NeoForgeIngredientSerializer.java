package net.neoforged.neoforge.internal.fabric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

/**
 * Adapts a NeoForge {@link IngredientType} to Fabric's {@link CustomIngredientSerializer}
 * registry. Recipe JSONs use {@code "fabric:type": "<neoforge ingredient id>"}.
 */
public final class NeoForgeIngredientSerializer<T extends ICustomIngredient> implements CustomIngredientSerializer<NeoForgeCustomIngredient> {

    private final ResourceLocation id;
    private final IngredientType<T> type;

    public NeoForgeIngredientSerializer(ResourceLocation id, IngredientType<T> type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return this.id;
    }

    @Override
    public MapCodec<NeoForgeCustomIngredient> getCodec(boolean allowEmpty) {
        return this.type.codec().xmap(i -> new NeoForgeCustomIngredient(i, this), i -> (T) i.delegate());
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, NeoForgeCustomIngredient> getPacketCodec() {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec = this.type.streamCodec();
        return StreamCodec.of(
            (buf, ingredient) -> codec.encode(buf, (T) ingredient.delegate()),
            buf -> new NeoForgeCustomIngredient(codec.decode(buf), this));
    }
}
