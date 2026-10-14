package net.neoforged.neoforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;

/**
 * Static event-hook helpers; on Fabric they post the corresponding shim events
 * and fall back to vanilla behavior when nothing handles them.
 */
public class EventHooks {

    public static boolean canEntityGrief(Level level, Entity entity) {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(level, entity);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled() && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @SuppressWarnings("unchecked")
    public static boolean checkSpawnPosition(Entity entity, LevelAccessor level, MobSpawnType spawnType) {
        if (entity instanceof Monster monster && level instanceof ServerLevelAccessor sla) {
            return Monster.checkMonsterSpawnRules((EntityType<? extends Monster>) entity.getType(), sla, spawnType,
                    entity.blockPosition(), level.getRandom());
        }
        return true;
    }

    /**
     * Whether the entity may destroy the block. NeoForge fires an event here;
     * without listeners the default is true.
     */
    public static boolean onEntityDestroyBlock(net.minecraft.world.entity.LivingEntity entity, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return true;
    }

    public static boolean onProjectileImpact(net.minecraft.world.entity.projectile.Projectile projectile,
            net.minecraft.world.phys.HitResult ray) {
        return NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.ProjectileImpactEvent(projectile, ray)).isCanceled();
    }

    public static boolean onAnimalTame(net.minecraft.world.entity.animal.Animal animal,
            net.minecraft.world.entity.player.Player tamer) {
        return NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.AnimalTameEvent(animal, tamer)).isCanceled();
    }

    public static net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity onEnderTeleport(
            net.minecraft.world.entity.LivingEntity entity, double targetX, double targetY, double targetZ) {
        net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event =
                new net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity(entity, targetX, targetY, targetZ);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static net.minecraft.world.entity.SpawnGroupData finalizeMobSpawn(net.minecraft.world.entity.Mob mob,
            net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnType, net.minecraft.world.entity.SpawnGroupData spawnData) {
        var event = new net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent(
                mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, spawnType, spawnData);
        NeoForge.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            return mob.finalizeSpawn(level, event.getDifficulty(), event.getSpawnType(),
                    (net.minecraft.world.entity.SpawnGroupData) event.getSpawnData());
        }
        return null;
    }

    /**
     * NeoForge's arrow-nock hook — fires {@link ArrowNockEvent}; a non-null
     * action overrides vanilla use.
     */
    public static net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> onArrowNock(
            net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
            boolean hasAmmo) {
        net.neoforged.neoforge.event.entity.player.ArrowNockEvent event =
                new net.neoforged.neoforge.event.entity.player.ArrowNockEvent(stack, level, player, hand, hasAmmo);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        return event.getAction();
    }

    /**
     * NeoForge's arrow-loose hook — fires {@link ArrowLooseEvent}; returns the
     * adjusted charge or -1 when cancelled.
     */
    public static int onArrowLoose(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level,
            net.minecraft.world.entity.player.Player player, int charge, boolean hasAmmo) {
        net.neoforged.neoforge.event.entity.player.ArrowLooseEvent event =
                new net.neoforged.neoforge.event.entity.player.ArrowLooseEvent(stack, level, player, charge, hasAmmo);
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled())
            return -1;
        return event.getCharge();
    }

    /**
     * Fires {@link ExplosionEvent.Detonate} after the vanilla affected-entity
     * list is built.
     */
    public static void onExplosionDetonate(net.minecraft.world.level.Level level,
            net.minecraft.world.level.Explosion explosion, java.util.List<net.minecraft.world.entity.Entity> list,
            double diameter) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                new net.neoforged.neoforge.event.level.ExplosionEvent.Detonate(level, explosion, list));
    }

    /**
     * Fires {@link ExplosionKnockbackEvent}; returns the adjusted velocity.
     */
    public static net.minecraft.world.phys.Vec3 getExplosionKnockback(net.minecraft.world.level.Level level,
            net.minecraft.world.level.Explosion explosion, net.minecraft.world.entity.Entity entity,
            net.minecraft.world.phys.Vec3 velocity) {
        net.neoforged.neoforge.event.level.ExplosionKnockbackEvent event =
                new net.neoforged.neoforge.event.level.ExplosionKnockbackEvent(level, explosion, entity, velocity);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        return event.getKnockbackVelocity();
    }
}
