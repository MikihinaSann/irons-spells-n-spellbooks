package com.yukami.efiscompat;

import com.mojang.logging.LogUtils;
import com.yukami.efiscompat.animation.Animation;
import com.yukami.efiscompat.client.EfiscompatClientEvents;
import com.yukami.efiscompat.config.CommonConfig;
import com.yukami.efiscompat.data.SpellAnimationLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

@Mod(EpicFightIronCompat.MODID)
public class EpicFightIronCompat {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "efiscompat";

    public EpicFightIronCompat(IEventBus bus, ModContainer modContainer) {
        bus.addListener(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent.class, this::commonSetup);
        // @EventBusSubscriber(bus = MOD) equivalent — must be on the bus before
        // registerConfig below fires ModConfigEvent.Loading.
        bus.register(CommonConfig.class);
        // Animation::registerAnimations is bridged via MixinAnimationRegistration —
        // the EpicFight Fabric port hardcodes AnimationRegistryEvent dispatch.

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG, "efiscompat.toml");
        NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.AddReloadListenerEvent.class, this::onResourceReload);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            EfiscompatClientEvents.registerEpicFightClientEvents();
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void onResourceReload(AddReloadListenerEvent event) {
        event.addListener(new SpellAnimationLoader());
    }
}
