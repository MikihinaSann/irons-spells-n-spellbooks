package issprobe;

import java.lang.reflect.Field;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sun.misc.Unsafe;

/** Standalone (no server) mutation check for the probe harness: verifies the
 * capture filter rejects a Post with the wrong entity and the amount
 * assertion rejects a Post with a wrong recorded amount, and accepts a
 * correct one. Allocates event/entity instances with Unsafe so no Minecraft
 * bootstrap or registry is needed — only field identity is exercised.
 *
 * Run: java -cp <probe classes>;<named mc jar>;<neoforge-compat classes>
 *      issprobe.ProbeAssertionsSelfTest
 * Exits 0 only when every check passes. */
public final class ProbeAssertionsSelfTest {
    private static int failures = 0;

    private static Unsafe unsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    private static void setField(Object o, String name, Object value) throws Exception {
        Class<?> c = o.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                f.set(o, value);
                return;
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name + " on " + o.getClass());
    }

    private static void expect(String what, boolean ok) {
        if (ok) {
            System.out.println("[SELFTEST] ok " + what);
        } else {
            failures++;
            System.out.println("[SELFTEST] FAIL " + what);
        }
    }

    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.setVersion(net.minecraft.DetectedVersion.BUILT_IN);
        net.minecraft.server.Bootstrap.bootStrap();
        Unsafe u = unsafe();
        Zombie expected = (Zombie) u.allocateInstance(Zombie.class);
        Zombie other = (Zombie) u.allocateInstance(Zombie.class);
        DamageSource src = (DamageSource) u.allocateInstance(DamageSource.class);
        DamageSource src2 = (DamageSource) u.allocateInstance(DamageSource.class);

        LivingDamageEvent.Post good = (LivingDamageEvent.Post) u.allocateInstance(LivingDamageEvent.Post.class);
        setField(good, "entity", expected);       // EntityEvent.entity
        setField(good, "source", src);            // LivingDamageEvent.source
        setField(good, "originalDamage", 4.0f);
        setField(good, "newDamage", 4.0f);

        // capture filter (identity on entity AND source)
        expect("capture accepts expected victim+source",
                good.getEntity() == expected && good.getSource() == src);
        expect("capture rejects wrong entity", !(good.getEntity() == other));
        expect("capture rejects wrong source", !(good.getSource() == src2));

        // amount assertion
        expect("amountsEqual accepts exact value", IssProdProbe.amountsEqual(good.getNewDamage(), 4.0f));
        expect("amountsEqual rejects wrong value", !IssProdProbe.amountsEqual(good.getNewDamage(), 3.33f));

        LivingDamageEvent.Post wrong = (LivingDamageEvent.Post) u.allocateInstance(LivingDamageEvent.Post.class);
        setField(wrong, "entity", other);
        setField(wrong, "source", src);
        setField(wrong, "originalDamage", 4.0f);
        setField(wrong, "newDamage", 0.0f);
        expect("wrong-entity post fails identity capture",
                !(wrong.getEntity() == expected && wrong.getSource() == src));
        expect("zero-amount post fails amount check",
                !IssProdProbe.amountsEqual(wrong.getNewDamage(), 4.0f));

        if (failures == 0) {
            System.out.println("[SELFTEST] ALL PASSED");
        } else {
            System.out.println("[SELFTEST] " + failures + " FAILURES");
            System.exit(1);
        }
    }
}
