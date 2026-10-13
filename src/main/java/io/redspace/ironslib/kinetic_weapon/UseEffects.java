package io.redspace.ironslib.kinetic_weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.redspace.ironslib.registry.IronsLibRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record UseEffects(boolean canSprint, float speedMultiplier) {
   public static final UseEffects DEFAULT = new UseEffects(false, 0.2F);
   public static final Codec<UseEffects> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
               Codec.BOOL.optionalFieldOf("can_sprint", DEFAULT.canSprint()).forGetter(UseEffects::canSprint),
               Codec.FLOAT.optionalFieldOf("speed_multiplier", DEFAULT.speedMultiplier()).forGetter(UseEffects::speedMultiplier)
            )
            .apply(instance, UseEffects::new)
   );
   public static final StreamCodec<ByteBuf, UseEffects> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, UseEffects::canSprint, ByteBufCodecs.FLOAT, UseEffects::speedMultiplier, UseEffects::new
   );

   public static UseEffects of(ItemStack stack) {
      return (UseEffects)stack.getOrDefault((DataComponentType)IronsLibRegistries.ComponentRegistry.USE_EFFECTS.get(), DEFAULT);
   }

   public boolean unrestricting() {
      return this.canSprint && this.speedMultiplier >= 1.0F;
   }

   public static boolean useIsUnrestricted(LivingEntity entity) {
      return of(entity.getUseItem()).unrestricting();
   }
}
