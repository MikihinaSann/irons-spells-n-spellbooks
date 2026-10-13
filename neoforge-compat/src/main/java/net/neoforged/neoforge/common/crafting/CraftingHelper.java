package net.neoforged.neoforge.common.crafting;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.common.extensions.IIngredientExtension;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class CraftingHelper {
   /** NeoForge's patched {@code Ingredient.LIST_CODEC} - custom-ingredient aware. */
   public static final Codec<List<Ingredient>> LIST_CODEC =
         Codec.lazyInitialized(() -> makeIngredientCodec(true)).listOf();

   /** NeoForge's patched {@code Ingredient.LIST_CODEC_NONEMPTY}. */
   public static final Codec<List<Ingredient>> LIST_CODEC_NONEMPTY =
         Codec.lazyInitialized(() -> makeIngredientCodec(false)).listOf();
   public static Codec<Ingredient> makeIngredientCodec(boolean allowEmpty) {
      Codec<List<Ingredient>> listCodec = Codec.lazyInitialized(() -> allowEmpty ? LIST_CODEC : LIST_CODEC_NONEMPTY);
      return Codec.either(listCodec, makeIngredientMapCodec().codec())
         .xmap(either -> (Ingredient)either.map(list -> CompoundIngredient.of(list.toArray(Ingredient[]::new)), i -> i), ingredient -> {
            if (ingredient.isCustom()) {
               if (((IIngredientExtension) (Object) ingredient).getCustomIngredient() instanceof CompoundIngredient compound) {
                  return Either.left(compound.children());
               }
            } else if (ingredient.getValues().length != 1) {
               return Either.left(Stream.of(ingredient.getValues()).map(v -> Ingredient.fromValues(Stream.of(v))).toList());
            }

            return Either.right(ingredient);
         });
   }

   private static Either<ICustomIngredient, Value> deconstructIngredient(Ingredient ingredient) {
      if (!ingredient.isCustom()) {
         Value[] values = ingredient.getValues();
         return values.length == 1
            ? Either.<ICustomIngredient, Value>right(values[0])
            : Either.<ICustomIngredient, Value>left(new CompoundIngredient(Stream.of(ingredient.getValues()).map(v -> Ingredient.fromValues(Stream.of(v))).toList()));
      }
      return Either.<ICustomIngredient, Value>left(((IIngredientExtension) (Object) ingredient).getCustomIngredient());
   }

   /** Stand-in for NeoForge's patched {@code Value.MAP_CODEC}. */
   private static MapCodec<Value> valueMapCodec() {
      Codec<Value> codec = Value.CODEC;
      return new MapCodec<Value>() {
         @Override
         public <T> DataResult<Value> decode(DynamicOps<T> ops, MapLike<T> input) {
            return codec.decode(ops, ops.createMap(input.entries())).map(Pair::getFirst);
         }

         @Override
         public <T> RecordBuilder<T> encode(Value input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            DataResult<T> encoded = codec.encodeStart(ops, input);
            return encoded.flatMap(ops::getMap).map(mapLike -> {
               mapLike.entries().forEach(pair -> prefix.add(pair.getFirst(), pair.getSecond()));
               return prefix;
            }).getOrThrow(err -> {
               throw new IllegalStateException(err);
            });
         }

         @Override
         public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("item"), ops.createString("tag"));
         }
      };
   }

   public static MapCodec<Ingredient> makeIngredientMapCodec() {
      return NeoForgeExtraCodecs.dispatchMapOrElse(
            NeoForgeRegistries.INGREDIENT_TYPES.byNameCodec(), ICustomIngredient::getType, IngredientType::codec, valueMapCodec()
         )
         .xmap(
            either -> either.<Ingredient>map(ICustomIngredient::toVanilla, v -> Ingredient.fromValues(Stream.of(v))),
            (Ingredient ingredient) -> (Either) deconstructIngredient(ingredient)
         )
         .validate(
            ingredient -> !((IIngredientExtension) (Object) ingredient).isCustom()
                     && ((IIngredientExtension) (Object) ingredient).getValues().length == 0
               ? DataResult.error(() -> "Cannot serialize empty ingredient using the map codec")
               : DataResult.success(ingredient)
         );
   }
}
