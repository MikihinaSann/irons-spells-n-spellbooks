package net.neoforged.neoforge.registries.datamaps;

// DataMapValueRemover is in the same package

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record DataMapEntry<T>(T value, boolean replace) {
   private DataMapEntry(T value) {
      this(value, false);
   }

   public static <T> Codec<DataMapEntry<T>> codec(DataMapType<?, T> type) {
      return Codec.either(
            RecordCodecBuilder.<DataMapEntry<T>>create(
               i -> i.group(
                     type.codec().fieldOf("value").forGetter(DataMapEntry::value),
                     Codec.BOOL.optionalFieldOf("replace", false).forGetter(DataMapEntry::replace)
                  )
                  .apply(i, DataMapEntry::new)
            ),
            type.codec()
         )
         .xmap(e -> e.map(e1 -> e1, value -> new DataMapEntry<>(value, false)), entry -> entry.replace() ? Either.<DataMapEntry<T>, T>left(entry) : Either.<DataMapEntry<T>, T>right(entry.value()));
   }

   public record Removal<T, R>(Either<TagKey<R>, ResourceKey<R>> key, Optional<DataMapValueRemover<R, T>> remover) {
      private Removal(Either<TagKey<R>, ResourceKey<R>> key) {
         this(key, Optional.empty());
      }

      public static <T, R> Codec<DataMapEntry.Removal<T, R>> codec(Codec<Either<TagKey<R>, ResourceKey<R>>> tagOrValue, DataMapType<R, T> attachment) {
         return attachment instanceof AdvancedDataMapType<R, T, ?> advanced
            ? RecordCodecBuilder.<DataMapEntry.Removal<T, R>>create(
               in -> in.group(
                     tagOrValue.fieldOf("key").forGetter((DataMapEntry.Removal<T, R> r) -> r.key()),
                     ((Codec<DataMapValueRemover<R, T>>) (Codec<?>) advanced.remover()).optionalFieldOf("remover").forGetter((DataMapEntry.Removal<T, R> r) -> r.remover())
                  )
                  .apply(in, DataMapEntry.Removal<T, R>::new)
            )
            : RecordCodecBuilder.<DataMapEntry.Removal<T, R>>create(
               inst -> inst.group(tagOrValue.fieldOf("key").forGetter((DataMapEntry.Removal<T, R> r) -> r.key()))
                  .apply(inst, key -> new DataMapEntry.Removal<T, R>(key))
            );
      }
   }
}
