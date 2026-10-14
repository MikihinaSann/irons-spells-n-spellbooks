package net.neoforged.neoforge.internal.fabric.scantest;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "scan_fixture", value = Dist.DEDICATED_SERVER)
public class ServerOnlyB {
    public static void marker() {
    }
}
