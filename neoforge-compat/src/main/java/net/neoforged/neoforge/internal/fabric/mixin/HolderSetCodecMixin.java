package net.neoforged.neoforge.internal.fabric.mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/**
 * Ports NeoForge's {@code HolderSetCodec} patch: holder-set JSON may use
 * custom {@link ICustomHolderSet} types ({@code neoforge:and/or/not/any})
 * dispatched through the {@code neoforge:holder_set_type} registry, alongside
 * vanilla tag/list forms.
 */
@Mixin(HolderSetCodec.class)
public abstract class HolderSetCodecMixin<E> {

    @Shadow
    @Final
    private ResourceKey<? extends Registry<E>> registryKey;

    @Shadow
    @Final
    private Codec<Holder<E>> elementCodec;

    @Shadow
    @Final
    private Codec<Either<TagKey<E>, List<Holder<E>>>> registryAwareCodec;

    @Unique
    private Codec<ICustomHolderSet<E>> neoforge$dispatchCodec;

    @Unique
    private Codec<Either<ICustomHolderSet<E>, Either<TagKey<E>, List<Holder<E>>>>> neoforge$combinedCodec;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neoforge$buildDispatchCodec(ResourceKey<? extends Registry<E>> key,
            Codec<Holder<E>> elementCodec, boolean forceList, CallbackInfo ci) {
        this.neoforge$dispatchCodec = NeoForgeRegistries.HOLDER_SET_TYPES.byNameCodec()
                .dispatch(ICustomHolderSet::type, type -> type.makeCodec(key, elementCodec, forceList));
        this.neoforge$combinedCodec = Codec.either(this.neoforge$dispatchCodec, this.registryAwareCodec);
    }

    @Unique
    private static <E> DataResult<HolderSet<E>> neoforge$lookupTag(HolderGetter<E> getter, TagKey<E> tagKey) {
        return getter.get(tagKey)
                .<DataResult<HolderSet<E>>>map(named -> DataResult.success((HolderSet<E>) named))
                .orElseGet(() -> DataResult.error(() -> "Unknown tag '" + tagKey + "'"));
    }

    @Inject(method = "decode", at = @At("HEAD"), cancellable = true)
    private <T> void neoforge$decode(DynamicOps<T> ops, T input,
            CallbackInfoReturnable<DataResult<Pair<HolderSet<E>, T>>> cir) {
        if (ops instanceof RegistryOps<T> registryOps) {
            Optional<HolderGetter<E>> optional = registryOps.getter(this.registryKey);
            if (optional.isPresent()) {
                HolderGetter<E> getter = optional.get();
                cir.setReturnValue(this.neoforge$combinedCodec.decode(ops, input)
                        .flatMap(pair -> {
                            DataResult<HolderSet<E>> result = pair.getFirst().map(
                                    DataResult::success,
                                    tagOrList -> tagOrList.map(
                                            tagKey -> neoforge$lookupTag(getter, tagKey),
                                            list -> DataResult.success(HolderSet.direct(list))));
                            return result.map(set -> Pair.of(set, pair.getSecond()));
                        }));
            }
        }
    }

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true)
    private <T> void neoforge$encode(HolderSet<E> holderSet, DynamicOps<T> ops, T prefix,
            CallbackInfoReturnable<DataResult<T>> cir) {
        if (ops instanceof RegistryOps<T> registryOps) {
            Optional<HolderOwner<E>> optional = registryOps.owner(this.registryKey);
            if (optional.isPresent()) {
                if (!holderSet.canSerializeIn(optional.get())) {
                    cir.setReturnValue(DataResult.error(() -> "HolderSet " + holderSet + " is not valid in current registry set"));
                    return;
                }
                if (holderSet instanceof ICustomHolderSet<E> custom) {
                    cir.setReturnValue(this.neoforge$dispatchCodec.encode(custom, ops, prefix));
                    return;
                }
                cir.setReturnValue(this.registryAwareCodec.encode(
                        holderSet.unwrap().mapRight(List::copyOf), ops, prefix));
            }
        }
    }
}
