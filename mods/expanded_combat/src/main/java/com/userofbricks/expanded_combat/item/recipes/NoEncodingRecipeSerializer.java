package com.userofbricks.expanded_combat.item.recipes;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class NoEncodingRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
   private final MapCodec<T> codec;
   private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

   public NoEncodingRecipeSerializer(NoEncodingRecipeSerializer.Factory<T> pConstructor) {
      this.codec = MapCodec.of(Encoder.empty(), Decoder.unit(pConstructor.create()));
      this.streamCodec = StreamCodec.of((o, i) -> {}, pBuffer -> pConstructor.create());
   }

   @NotNull
   public MapCodec<T> codec() {
      return this.codec;
   }

   @NotNull
   public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
      return this.streamCodec;
   }

   @FunctionalInterface
   public interface Factory<T extends Recipe<?>> {
      T create();
   }
}
