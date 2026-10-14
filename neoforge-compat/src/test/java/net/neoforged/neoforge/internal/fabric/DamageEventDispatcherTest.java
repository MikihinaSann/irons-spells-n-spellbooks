package net.neoforged.neoforge.internal.fabric;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Per-thread frame stack around hurt: Pre may cancel/adjust, Post carries the
 * amount CombatTracker.recordDamage actually got, nested calls and exceptions
 * never leak frames. Entities are null — the events only store them.
 */
public class DamageEventDispatcherTest {

    @AfterEach
    void busClean() {
        NeoForge.EVENT_BUS.unregister(LISTENER);
    }

    private static final AtomicReference<LivingDamageEvent.Pre> PRE = new AtomicReference<>();
    private static final AtomicReference<LivingDamageEvent.Post> POST = new AtomicReference<>();
    private static final AtomicBoolean CANCEL_PRE = new AtomicBoolean();
    private static final Object LISTENER = new Object() {
        @SubscribeEvent
        public void pre(LivingDamageEvent.Pre e) {
            PRE.set(e);
            if (CANCEL_PRE.get()) {
                e.setCanceled(true);
            }
        }

        @SubscribeEvent
        public void post(LivingDamageEvent.Post e) {
            POST.set(e);
        }
    };

    private void listen(boolean cancel) {
        PRE.set(null);
        POST.set(null);
        CANCEL_PRE.set(cancel);
        NeoForge.EVENT_BUS.register(LISTENER);
    }

    @Test
    void normalHitPostsRecordedFinalAmount() {
        listen(false);
        AtomicBoolean ran = new AtomicBoolean();
        DamageEventDispatcher.apply(null, null, 10f, (src, amount) -> {
            ran.set(true);
            assertEquals(10f, amount);
            // Simulate vanilla's CombatTracker.recordDamage capture after reductions.
            DamageEventDispatcher.record(null, 4f);
        });
        assertTrue(ran.get());
        assertNotNull(POST.get());
        assertEquals(10f, POST.get().getOriginalDamage());
        assertEquals(4f, POST.get().getNewDamage(), "Post must carry the recorded final amount");
        assertEquals(0, DamageEventDispatcher.frameDepth());
    }

    @Test
    void preAdjustedAmountReachesHandler() {
        listen(false);
        Object tweak = new Object() {
            @SubscribeEvent
            public void tweak(LivingDamageEvent.Pre e) {
                e.setNewDamage(7f);
            }
        };
        NeoForge.EVENT_BUS.register(tweak);
        AtomicReference<Float> seen = new AtomicReference<>();
        try {
            DamageEventDispatcher.apply(null, null, 10f, (src, amount) -> seen.set(amount));
            assertEquals(7f, seen.get(), "handler must receive the Pre-adjusted amount");
        } finally {
            NeoForge.EVENT_BUS.unregister(tweak);
        }
    }

    @Test
    void canceledPreSkipsHandlerAndPost() {
        listen(true);
        AtomicBoolean ran = new AtomicBoolean();
        DamageEventDispatcher.apply(null, null, 10f, (src, amount) -> ran.set(true));
        assertFalse(ran.get());
        assertNull(POST.get(), "canceled hurt must not post Post (life-steal heals 0)");
        assertEquals(0, DamageEventDispatcher.frameDepth());
    }

    @Test
    void nestedHurtDoesNotOverwriteParentFrame() {
        listen(false);
        DamageEventDispatcher.apply(null, null, 10f, (src, amount) -> {
            // inner hurt on the same entity: record() must hit the inner frame
            DamageEventDispatcher.apply(null, null, 3f, (s2, a2) -> DamageEventDispatcher.record(null, 1f));
            DamageEventDispatcher.record(null, 8f);
        });
        // outer Post must carry 8 (its own record), inner 1
        assertEquals(8f, POST.get().getNewDamage());
        assertEquals(0, DamageEventDispatcher.frameDepth());
    }

    @Test
    void thrownHandlerUnwindsStack() {
        listen(false);
        assertThrows(RuntimeException.class,
                () -> DamageEventDispatcher.apply(null, null, 10f,
                        (src, amount) -> { throw new RuntimeException("boom"); }));
        assertEquals(0, DamageEventDispatcher.frameDepth(), "thrown handler must pop the frame");
        // a clean subsequent apply proves the ThreadLocal was cleared
        DamageEventDispatcher.apply(null, null, 2f, (src, amount) -> DamageEventDispatcher.record(null, 2f));
        assertEquals(2f, POST.get().getNewDamage());
    }

    @Test
    void recordWithoutFrameReturnsInputUnchanged() {
        assertEquals(3.5f, DamageEventDispatcher.record(null, 3.5f));
    }
}
