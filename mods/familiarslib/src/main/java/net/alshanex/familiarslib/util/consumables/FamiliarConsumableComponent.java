package net.alshanex.familiarslib.util.consumables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FamiliarConsumableComponent(FamiliarConsumableSystem.ConsumableType type, int tier) {
   public static final Codec<FamiliarConsumableComponent> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            FamiliarConsumableSystem.ConsumableType.CODEC.fieldOf("type").forGetter(FamiliarConsumableComponent::type),
            Codec.intRange(1, 3).fieldOf("tier").forGetter(FamiliarConsumableComponent::tier)
         )
         .apply(instance, FamiliarConsumableComponent::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FamiliarConsumableComponent> STREAM_CODEC = StreamCodec.composite(
      FamiliarConsumableSystem.ConsumableType.STREAM_CODEC,
      FamiliarConsumableComponent::type,
      ByteBufCodecs.VAR_INT,
      FamiliarConsumableComponent::tier,
      FamiliarConsumableComponent::new
   );

   public int getBonus() {
      return this.type.getTierBonus(this.tier);
   }

   public int getLimit() {
      return this.type.getTierLimit(this.tier);
   }

   public boolean isValidTier() {
      return this.tier >= 1 && this.tier <= this.type.getMaxTier();
   }
}
