package it.crystalnest.cobweb.api.registry;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public record CobwebEntry<T>(Holder<T> holder) implements Holder<T>, Supplier<T> {
   @NotNull
   @Override
   public T get() {
      return this.value();
   }

   @NotNull
   public T value() {
      return (T)this.holder.value();
   }

   public boolean isBound() {
      return this.holder.isBound();
   }

   public boolean is(@NotNull ResourceLocation key) {
      return this.holder.is(key);
   }

   public boolean is(@NotNull ResourceKey<T> key) {
      return this.holder.is(key);
   }

   public boolean is(@NotNull Predicate<ResourceKey<T>> predicate) {
      return this.holder.is(predicate);
   }

   public boolean is(@NotNull TagKey<T> key) {
      return this.holder.is(key);
   }

   public boolean is(@NotNull Holder<T> holder) {
      return this.holder.is(holder);
   }

   @NotNull
   public Stream<TagKey<T>> tags() {
      return this.holder.tags();
   }

   @NotNull
   public Either<ResourceKey<T>, T> unwrap() {
      return this.holder.unwrap();
   }

   @NotNull
   public Optional<ResourceKey<T>> unwrapKey() {
      return this.holder.unwrapKey();
   }

   @NotNull
   public Kind kind() {
      return this.holder.kind();
   }

   public boolean canSerializeIn(@NotNull HolderOwner<T> owner) {
      return this.holder.canSerializeIn(owner);
   }

   @NotNull
   public String getRegisteredName() {
      return this.holder.getRegisteredName();
   }
}
