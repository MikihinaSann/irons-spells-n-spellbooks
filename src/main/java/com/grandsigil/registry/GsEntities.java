package com.grandsigil.registry;

import com.grandsigil.entity.ArenaEntity;
import com.grandsigil.entity.ArsenalEntity;
import com.grandsigil.entity.BladeEntity;
import com.grandsigil.entity.BloomEntity;
import com.grandsigil.entity.BoltEntity;
import com.grandsigil.entity.CalderaEntity;
import com.grandsigil.entity.CleaveEntity;
import com.grandsigil.entity.CoffinEntity;
import com.grandsigil.entity.GateEntity;
import com.grandsigil.entity.GazeEntity;
import com.grandsigil.entity.GlacierEntity;
import com.grandsigil.entity.GripEntity;
import com.grandsigil.entity.IceAgeEntity;
import com.grandsigil.entity.PhoenixEntity;
import com.grandsigil.entity.ReckoningEntity;
import com.grandsigil.entity.SigilEntity;
import com.grandsigil.entity.SunEntity;
import com.grandsigil.entity.ThreadsEntity;
import com.grandsigil.entity.ThunderDragonEntity;
import com.grandsigil.entity.TreeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GsEntities {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "grandsigil");
   public static final DeferredHolder<EntityType<?>, EntityType<SigilEntity>> SIGIL = ENTITIES.register(
      "sigil",
      () -> Builder.<SigilEntity>of(SigilEntity::new, MobCategory.MISC).sized(8.0F, 8.0F).fireImmune().clientTrackingRange(12).updateInterval(1).build("sigil")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SunEntity>> SUN = ENTITIES.register(
      "sun",
      () -> Builder.<SunEntity>of(SunEntity::new, MobCategory.MISC).sized(4.0F, 4.0F).fireImmune().clientTrackingRange(24).updateInterval(1).build("sun")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BoltEntity>> BOLT = ENTITIES.register(
      "bolt",
      () -> Builder.<BoltEntity>of(BoltEntity::new, MobCategory.MISC).sized(4.0F, 4.0F).fireImmune().clientTrackingRange(24).updateInterval(1).build("bolt")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TreeEntity>> TREE = ENTITIES.register(
      "tree",
      () -> Builder.<TreeEntity>of(TreeEntity::new, MobCategory.MISC).sized(6.0F, 6.0F).fireImmune().clientTrackingRange(20).updateInterval(1).build("tree")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GateEntity>> GATE = ENTITIES.register(
      "gate",
      () -> Builder.<GateEntity>of(GateEntity::new, MobCategory.MISC).sized(6.0F, 6.0F).fireImmune().clientTrackingRange(24).updateInterval(1).build("gate")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GlacierEntity>> GLACIER = ENTITIES.register(
      "glacier",
      () -> Builder.<GlacierEntity>of(GlacierEntity::new, MobCategory.MISC)
            .sized(8.0F, 8.0F)
            .fireImmune()
            .clientTrackingRange(20)
            .updateInterval(1)
            .build("glacier")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GazeEntity>> GAZE = ENTITIES.register(
      "gaze",
      () -> Builder.<GazeEntity>of(GazeEntity::new, MobCategory.MISC).sized(6.0F, 6.0F).fireImmune().clientTrackingRange(32).updateInterval(1).build("gaze")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ArenaEntity>> ARENA = ENTITIES.register(
      "arena",
      () -> Builder.<ArenaEntity>of(ArenaEntity::new, MobCategory.MISC).sized(8.0F, 8.0F).fireImmune().clientTrackingRange(28).updateInterval(1).build("arena")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GripEntity>> GRIP = ENTITIES.register(
      "grip",
      () -> Builder.<GripEntity>of(GripEntity::new, MobCategory.MISC).sized(6.0F, 6.0F).fireImmune().clientTrackingRange(28).updateInterval(1).build("grip")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CalderaEntity>> CALDERA = ENTITIES.register(
      "caldera",
      () -> Builder.<CalderaEntity>of(CalderaEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(8)
            .updateInterval(2)
            .build("caldera")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ReckoningEntity>> RECKONING = ENTITIES.register(
      "reckoning",
      () -> Builder.<ReckoningEntity>of(ReckoningEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(32)
            .updateInterval(1)
            .build("reckoning")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CoffinEntity>> COFFIN = ENTITIES.register(
      "coffin",
      () -> Builder.<CoffinEntity>of(CoffinEntity::new, MobCategory.MISC)
            .sized(4.0F, 4.0F)
            .fireImmune()
            .clientTrackingRange(24)
            .updateInterval(1)
            .build("coffin")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ArsenalEntity>> ARSENAL = ENTITIES.register(
      "arsenal",
      () -> Builder.<ArsenalEntity>of(ArsenalEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(16)
            .updateInterval(1)
            .build("arsenal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BladeEntity>> BLADE = ENTITIES.register(
      "blade",
      () -> Builder.<BladeEntity>of(BladeEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().clientTrackingRange(12).updateInterval(1).build("blade")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PhoenixEntity>> PHOENIX = ENTITIES.register(
      "phoenix",
      () -> Builder.<PhoenixEntity>of(PhoenixEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(16)
            .updateInterval(2)
            .build("phoenix")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThunderDragonEntity>> THUNDER_DRAGON = ENTITIES.register(
      "thunder_dragon",
      () -> Builder.<ThunderDragonEntity>of(ThunderDragonEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(16)
            .updateInterval(1)
            .build("thunder_dragon")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloomEntity>> FULL_BLOOM = ENTITIES.register(
      "full_bloom",
      () -> Builder.<BloomEntity>of(BloomEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(16)
            .updateInterval(10)
            .build("full_bloom")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceAgeEntity>> ICE_AGE = ENTITIES.register(
      "ice_age",
      () -> Builder.<IceAgeEntity>of(IceAgeEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(20)
            .updateInterval(10)
            .build("ice_age")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThreadsEntity>> CRIMSON_THREADS = ENTITIES.register(
      "crimson_threads",
      () -> Builder.<ThreadsEntity>of(ThreadsEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(16)
            .updateInterval(10)
            .build("crimson_threads")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CleaveEntity>> DIMENSION_CUT = ENTITIES.register(
      "dimension_cut",
      () -> Builder.<CleaveEntity>of(CleaveEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .fireImmune()
            .clientTrackingRange(20)
            .updateInterval(10)
            .build("dimension_cut")
   );

   public static void register(IEventBus modBus) {
      ENTITIES.register(modBus);
   }

   private GsEntities() {
   }
}
