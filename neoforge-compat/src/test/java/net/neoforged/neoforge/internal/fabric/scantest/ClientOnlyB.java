package net.neoforged.neoforge.internal.fabric.scantest;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "scan_fixture", value = Dist.CLIENT)
public class ClientOnlyB {
    public static void marker() {
    }
}
