package com.mumu17.ironsarms;

import com.mojang.logging.LogUtils;
import com.mumu17.ironsarms.event.IronsArmsBulletEvents;
import com.mumu17.ironsarms.register.ModNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * IronsArms — TACZ x ISS compat, merged into irons_spellbooks.
 * Only constructed when TACZ (tacz) is loaded; class refs to com.tacz are
 * contained to this package's runtime path.
 */
@Mod("ironsarms")
public class IronsArms {
    public static final String MODID = "ironsarms";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronsArms(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(IronsArmsBulletEvents.class);
        IronsArmsBulletEvents.registerTaczCallbacks();
        ModNetworking.register(modEventBus);
    }
}
