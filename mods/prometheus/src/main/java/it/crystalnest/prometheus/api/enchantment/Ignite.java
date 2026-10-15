package it.crystalnest.prometheus.api.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record Ignite(LevelBasedValue duration, ResourceLocation fireType) implements EnchantmentEntityEffect {
   public static final MapCodec<Ignite> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(Ignite::duration), ResourceLocation.CODEC.fieldOf("fire_type").forGetter(Ignite::fireType)
         )
         .apply(instance, Ignite::new)
   );

   public void apply(@NotNull ServerLevel serverLevel, int enchantmentLevel, @NotNull EnchantedItemInUse item, @NotNull Entity entity, @NotNull Vec3 origin) {
      FireManager.setOnFire(entity, this.duration.calculate(enchantmentLevel), this.fireType);
   }

   @NotNull
   public MapCodec<Ignite> codec() {
      return CODEC;
   }
}
