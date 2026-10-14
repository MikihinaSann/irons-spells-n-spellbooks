package net.neoforged.neoforge.internal.fabric.scantest;

import java.util.concurrent.atomic.AtomicBoolean;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Common-dist subscriber fixture — picked up on both server and client scans. */
@EventBusSubscriber(modid = "scan_fixture")
public class CommonSubscriber {
    public static final AtomicBoolean FIRED = new AtomicBoolean();

    @SubscribeEvent
    public static void onScan(ScanEvent event) {
        FIRED.set(true);
    }
}
