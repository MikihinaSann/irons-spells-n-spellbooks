package net.neoforged.neoforge.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

/**
 * Static helpers NeoForge exposes for firing events / misc queries.
 */
public class CommonHooks {

    /**
     * Fires a block-break event; returns the event so callers can inspect the
     * cancellation state.
     */
    public static BlockEvent.BreakEvent fireBlockBreak(Level level, GameType gameModeForPlayer,
                                                       ServerPlayer player, BlockPos pos, BlockState state) {
        var event = new BlockEvent.BreakEvent(level, pos, state, player);
        return NeoForge.EVENT_BUS.post(event);
    }

    /**
     * Looting level for a killer. Vanilla consults attributes in 1.21.
     */
    public static int getLootingLevel(Entity target, Entity killer, net.minecraft.world.damagesource.DamageSource source) {
        return 0;
    }

    public static boolean onLivingDeath(net.minecraft.world.entity.LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource src) {
        return NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingDeathEvent(entity, src)).isCanceled();
    }

    public static void onLivingJump(net.minecraft.world.entity.LivingEntity entity) {
        NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent(entity));
    }

    /**
     * NeoForge: fires {@link LivingShieldBlockEvent} from inside
     * {@code LivingEntity.hurt}, replacing the vanilla shield block check.
     */
    public static net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent onDamageBlock(
            net.minecraft.world.entity.LivingEntity blocker,
            net.neoforged.neoforge.common.damagesource.DamageContainer container,
            boolean blocked) {
        var event = new net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent(blocker, container, blocked);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    /**
     * NeoForge: fires {@link LivingFallEvent} at the head of
     * {@code LivingEntity.causeFallDamage}; {@code null} cancels the fall.
     */
    public static float[] onLivingFall(
            net.minecraft.world.entity.LivingEntity entity, float distance, float damageMultiplier) {
        var event = new net.neoforged.neoforge.event.entity.living.LivingFallEvent(entity, distance, damageMultiplier);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return null;
        }
        return new float[] { event.getDistance(), event.getDamageMultiplier() };
    }

    public static void onEmptyClick(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    /** NeoForge: fires {@link LivingDropsEvent}; returns true when canceled. */
    public static boolean onLivingDrops(net.minecraft.world.entity.LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource source,
            java.util.Collection<net.minecraft.world.entity.item.ItemEntity> drops,
            boolean recentlyHit) {
        return NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingDropsEvent(
                entity, source, drops, 0, recentlyHit)).isCanceled();
    }

    /** NeoForge: fires {@link LivingKnockBackEvent}; returns the (possibly mutated) event. */
    public static net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent onLivingKnockBack(
            net.minecraft.world.entity.LivingEntity entity, float strength, double ratioX, double ratioZ) {
        var event = new net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent(entity, strength, ratioX, ratioZ);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    /**
     * NeoForge: fires {@link net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent};
     * returns the event-adjusted burn time for the stack.
     */
    public static int getBurnTime(net.minecraft.world.item.ItemStack stack,
            @org.jetbrains.annotations.Nullable net.minecraft.world.item.crafting.RecipeType<?> recipeType) {
        if (stack.isEmpty()) {
            return 0;
        }
        int burnTime = net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.getFuel()
                .getOrDefault(stack.getItem(), 0);
        var event = new net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent(stack, burnTime, recipeType);
        NeoForge.EVENT_BUS.post(event);
        return event.getBurnTime();
    }

    /**
     * NeoForge's armor-durability hook: builds the per-slot damage map, posts
     * {@link net.neoforged.neoforge.event.entity.living.ArmorHurtEvent}, then
     * applies the (possibly event-mutated) damage per slot unless canceled.
     * Called by {@code LivingEntity#doHurtEquipment} via mixin.
     */
    public static void onArmorHurt(net.minecraft.world.damagesource.DamageSource source,
            net.minecraft.world.entity.EquipmentSlot[] slots, float damage,
            net.minecraft.world.entity.LivingEntity armoredEntity) {
        java.util.EnumMap<net.minecraft.world.entity.EquipmentSlot,
                net.neoforged.neoforge.event.entity.living.ArmorHurtEvent.ArmorEntry> armorMap =
                new java.util.EnumMap<>(net.minecraft.world.entity.EquipmentSlot.class);
        for (net.minecraft.world.entity.EquipmentSlot slot : slots) {
            net.minecraft.world.item.ItemStack armorPiece = armoredEntity.getItemBySlot(slot);
            if (!armorPiece.isEmpty()) {
                float damageAfterFireResist = armorPiece.getItem() instanceof net.minecraft.world.item.ArmorItem
                        && armorPiece.canBeHurtBy(source) ? damage : 0.0F;
                armorMap.put(slot, new net.neoforged.neoforge.event.entity.living.ArmorHurtEvent.ArmorEntry(
                        armorPiece, damageAfterFireResist));
            }
        }
        var event = new net.neoforged.neoforge.event.entity.living.ArmorHurtEvent(armorMap, armoredEntity);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            event.getArmorMap().forEach((slotx, entry) ->
                    entry.armorItemStack.hurtAndBreak((int) entry.newDamage, armoredEntity, slotx));
        }
    }

    /**
     * NeoForge's air-supply handling extracted from {@code LivingEntity#baseTick}:
     * computes breathe/drown state, posts {@code LivingBreatheEvent}, adjusts air,
     * and applies drowning damage via {@code LivingDrownEvent} when air runs out.
     */
    public static void onLivingBreathe(net.minecraft.world.entity.LivingEntity entity,
            int consumeAirAmount, int refillAirAmount) {
        var entityExt = (net.neoforged.neoforge.entity.IEntityExtension) entity;
        boolean isAir = entityExt.getEyeInFluidType() == net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value()
                || entity.level().getBlockState(net.minecraft.core.BlockPos.containing(
                        entity.getX(), entity.getEyeY(), entity.getZ()))
                        .is(net.minecraft.world.level.block.Blocks.BUBBLE_COLUMN);
        boolean canBreathe = isAir;
        if (!isAir && (net.minecraft.world.effect.MobEffectUtil.hasWaterBreathing(entity)
                || !entityExt.canDrownInFluidType(entityExt.getEyeInFluidType())
                || entity instanceof net.minecraft.world.entity.player.Player player
                        && player.getAbilities().invulnerable)) {
            canBreathe = true;
            refillAirAmount = 0;
        }
        var breatheEvent = new net.neoforged.neoforge.event.entity.living.LivingBreatheEvent(
                entity, canBreathe, consumeAirAmount, refillAirAmount);
        NeoForge.EVENT_BUS.post(breatheEvent);
        if (breatheEvent.canBreathe()) {
            entity.setAirSupply(Math.min(entity.getAirSupply() + breatheEvent.getRefillAirAmount(),
                    entity.getMaxAirSupply()));
        } else {
            entity.setAirSupply(entity.getAirSupply() - breatheEvent.getConsumeAirAmount());
        }
        if (entity.getAirSupply() <= 0) {
            var drownEvent = new net.neoforged.neoforge.event.entity.living.LivingDrownEvent(entity);
            NeoForge.EVENT_BUS.post(drownEvent);
            if (!drownEvent.isCanceled() && drownEvent.isDrowning()) {
                entity.setAirSupply(0);
                net.minecraft.world.phys.Vec3 vec3 = entity.getDeltaMovement();
                for (int i = 0; i < drownEvent.getBubbleCount(); i++) {
                    double d2 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d3 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d4 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    entity.level().addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE,
                            entity.getX() + d2, entity.getY() + d3, entity.getZ() + d4,
                            vec3.x, vec3.y, vec3.z);
                }
                if (drownEvent.getDamageAmount() > 0.0F) {
                    entity.hurt(entity.damageSources().drown(), drownEvent.getDamageAmount());
                }
            }
        }
        if (!isAir && !entity.level().isClientSide && entity.isPassenger()
                && entity.getVehicle() != null
                && !((net.neoforged.neoforge.entity.IEntityExtension) entity.getVehicle())
                        .canBeRiddenUnderFluidType(entityExt.getEyeInFluidType(), entity)) {
            entity.stopRiding();
        }
    }
}
