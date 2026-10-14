package net.neoforged.neoforge.internal.fabric;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * Dispatches {@link LivingDamageEvent.Pre} and {@link LivingDamageEvent.Post}
 * around {@code LivingEntity.actuallyHurt} / {@code Player.actuallyHurt}.
 * <p>
 * Compatibility contract: {@code Pre} is posted before the wrapped
 * {@code actuallyHurt} body runs (cancellable, may adjust the amount);
 * {@code Post} is posted after it returns, reporting the final amount
 * vanilla actually recorded in the CombatTracker — after armor, resistance
 * and absorption (0 when nothing was recorded). The final amount is captured
 * by a {@code ModifyArg} on the {@code CombatTracker.recordDamage} call,
 * which feeds {@link #record}.
 * <p>
 * A per-thread deque of frames scopes each hurt call, so nested damage (e.g.
 * thorns hurting another entity) does not overwrite the parent frame and a
 * thrown handler or canceled hurt can never leak a frame.
 */
public final class DamageEventDispatcher {

    /** The body of {@code actuallyHurt} minus the event dispatch. */
    @FunctionalInterface
    public interface DamageHandler {
        void apply(DamageSource source, float amount);
    }

    private static final class Frame {
        final LivingEntity entity;
        final float original;
        /** Amount actually handed to CombatTracker.recordDamage; 0 until recorded. */
        float finalAmount;

        Frame(LivingEntity entity, float original) {
            this.entity = entity;
            this.original = original;
        }
    }

    private static final ThreadLocal<Deque<Frame>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * Wraps {@code actuallyHurt}: pushes a frame, posts Pre, runs the body with
     * the Pre-adjusted amount, posts Post with the recorded final amount.
     */
    public static void apply(LivingEntity entity, DamageSource source, float amount, DamageHandler handler) {
        Deque<Frame> frames = FRAMES.get();
        Frame frame = new Frame(entity, amount);
        frames.push(frame);
        try {
            LivingDamageEvent.Pre pre = new LivingDamageEvent.Pre(entity, source, amount);
            NeoForge.EVENT_BUS.post(pre);
            if (pre.isCanceled()) {
                return;
            }
            handler.apply(source, pre.getNewDamage());
            NeoForge.EVENT_BUS.post(new LivingDamageEvent.Post(
                    entity, source, pre.getOriginalDamage(), frame.finalAmount, 0.0F, 0.0F));
        } finally {
            frames.pop();
            if (frames.isEmpty()) {
                FRAMES.remove();
            }
        }
    }

    /**
     * Called by the {@code ModifyArg} on {@code CombatTracker.recordDamage}:
     * records the final damage on the innermost frame for this entity and
     * returns the input unchanged.
     */
    public static float record(LivingEntity entity, float amount) {
        Deque<Frame> frames = FRAMES.get();
        if (frames != null) {
            for (Frame frame : frames) {
                if (frame.entity == entity) {
                    frame.finalAmount = amount;
                    break;
                }
            }
        }
        return amount;
    }

    static int frameDepth() {
        Deque<Frame> frames = FRAMES.get();
        return frames == null ? 0 : frames.size();
    }

    private DamageEventDispatcher() {
    }
}
