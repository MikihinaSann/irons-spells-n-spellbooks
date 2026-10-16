package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.entity.ECArrow;
import com.userofbricks.expanded_combat.entity.ECFallingBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ECEntities {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "expanded_combat");
   public static final Supplier<EntityType<ECArrow>> EC_ARROW = ENTITIES.register(
      "ec_arrow", () -> Builder.<ECArrow>of((type, level) -> new ECArrow(type, level), MobCategory.MISC).updateInterval(20).clientTrackingRange(4).sized(0.5F, 0.5F).build("ec_arrow")
   );
   public static final Supplier<EntityType<ECFallingBlockEntity>> EC_FALLING_BLOCK = ENTITIES.register(
      "ec_falling_block",
      () -> Builder.<ECFallingBlockEntity>of((type, level) -> new ECFallingBlockEntity(type, level), MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20).build("ec_falling_block")
   );
   public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
      NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, "expanded_combat"
   );
   public static final Supplier<EntityDataSerializer<Material>> MATERIAL = ENTITY_DATA_SERIALIZERS.register(
      "material", () -> EntityDataSerializer.forValueType(Material.STREAM_CODEC)
   );
}
