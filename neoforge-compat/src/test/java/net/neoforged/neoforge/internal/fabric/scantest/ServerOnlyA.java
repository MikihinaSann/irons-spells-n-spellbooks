package net.neoforged.neoforge.internal.fabric.scantest;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "scan_fixture", value = Dist.DEDICATED_SERVER)
public class ServerOnlyA {
    public static void marker() {
    }
}
