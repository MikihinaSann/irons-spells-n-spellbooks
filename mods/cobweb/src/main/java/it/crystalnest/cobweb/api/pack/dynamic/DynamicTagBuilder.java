package it.crystalnest.cobweb.api.pack.dynamic;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public final class DynamicTagBuilder<T> extends TagBuilder {
   private final Set<String> keys = new HashSet<>();
   private final List<ResourceLocation> tags;
   private final Registry<T> registry;

   private DynamicTagBuilder(Registry<T> registry, List<ResourceLocation> tags) {
      this.tags = tags;
      this.registry = registry;
   }

   @SafeVarargs
   public static <R> DynamicTagBuilder<R> of(Registry<R> registry, TagKey<? extends R>... tags) {
      return new DynamicTagBuilder<>((Registry<R>)registry, Arrays.stream(tags).<ResourceLocation>map(TagKey::location).toList());
   }

   @SafeVarargs
   public static <R> DynamicTagBuilder<R> of(ResourceKey<? extends Registry<R>> registryKey, TagKey<? extends R>... tags) {
      return of((Registry<R>)BuiltInRegistries.REGISTRY.get(registryKey.location()), tags);
   }

   public List<ResourceLocation> getTags() {
      return this.tags;
   }

   public List<ResourceLocation> getPaths() {
      return this.getTags()
         .stream()
         .map(
            tag -> DynamicResourceType.TAG
               .getPath(ResourceLocation.fromNamespaceAndPath(tag.getNamespace(), this.registry.key().location().getPath() + "/" + tag.getPath()))
         )
         .toList();
   }

   @NotNull
   public DynamicTagBuilder<T> add(@NotNull TagEntry entry) {
      return this.keys.add(entry.toString()) ? (DynamicTagBuilder)super.add(entry) : this;
   }

   public DynamicTagBuilder<T> addTag(TagKey<? extends T> tagKey) {
      return (DynamicTagBuilder<T>)this.addTag(tagKey.location());
   }

   public DynamicTagBuilder<T> addElement(T element) {
      return (DynamicTagBuilder<T>)this.addElement(Objects.requireNonNull(this.registry.getKey(element)));
   }

   public DynamicTagBuilder<T> addElements(Collection<? extends T> elements) {
      for (T element : elements) {
         this.addElement(element);
      }

      return this;
   }

   public DynamicTagBuilder<T> addElements(T... elements) {
      for (T element : elements) {
         this.addElement(element);
      }

      return this;
   }

   public JsonElement json() {
      return (JsonElement)TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(this.build(), false)).getOrThrow();
   }
}
