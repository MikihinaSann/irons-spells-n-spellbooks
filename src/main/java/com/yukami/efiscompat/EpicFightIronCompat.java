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
        bus.addListener(net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent.class, this::loadComplete);
        // @EventBusSubscriber(bus = MOD) equivalent — must be on the bus before
        // registerConfig below fires ModConfigEvent.Loading.
        bus.register(CommonConfig.class);
        // Animation::registerAnimations is bridged via MixinAnimationRegistration —
        // the EpicFight Fabric port hardcodes AnimationRegistryEvent dispatch.

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG, "efiscompat.toml");
        NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.AddReloadListenerEvent.class, this::onResourceReload);

        injectArmatureAccessors();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            EfiscompatClientEvents.registerEpicFightClientEvents();
        }
    }

    /**
     * The Fabric build's animmodels scan never populates Armatures.ACCESSORS, so
     * custom mobpatch "armature" lookups create blank accessors (jsonObject=null).
     * Alias the ids our datapacks use to the statically-created preset accessors —
     * ArmatureAccessor is a record, so ARMATURES.get() resolves to the same loaded
     * armature the vanilla presets use.
     */
    private static void injectArmatureAccessors() {
        try {
            Class<?> arm = Class.forName("yesman.epicfight.gameasset.Armatures");
            java.lang.reflect.Field accF = arm.getDeclaredField("ACCESSORS");
            accF.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<net.minecraft.resources.ResourceLocation, Object> accessors =
                (java.util.Map<net.minecraft.resources.ResourceLocation, Object>) accF.get(null);
            for (String path : new String[]{"biped", "skeleton", "piglin", "enderman", "iron_golem", "wither"}) {
                java.lang.reflect.Field preset = arm.getField(path.toUpperCase(java.util.Locale.ROOT));
                Object accessor = preset.get(null);
                if (accessor != null) {
                    accessors.putIfAbsent(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("epicfight", "entity/" + path),
                        accessor);
                }
            }
            LOGGER.info("efiscompat: injected {} preset armature accessors", 6);
        } catch (Throwable t) {
            LOGGER.warn("efiscompat: armature accessor injection failed", t);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void loadComplete(final net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent event) {
        // Epic Fight attributes are only bound late in EF's own init — must run
        // after all mods' registries drained, not at commonSetup.
        event.enqueueWork(EpicFightMobPatchCompat::registerMobPatchAttributes);
        event.enqueueWork(EpicFightIronCompat::probeEffectBinding);
    }

    private static void probeEffectBinding() {
        try {
            Class<?> regs = Class.forName("yesman.epicfight.registry.EpicFightRegistries");
            java.util.List<?> list = (java.util.List<?>) regs.getField("DEFERRED_REGISTRIES").get(null);
            java.lang.reflect.Field accF = Class.forName("yesman.epicfight.registry.deferred_shim.DeferredRegisterShim")
                .getDeclaredField("accepted");
            accF.setAccessible(true);
            java.lang.reflect.Field rkF = Class.forName("yesman.epicfight.registry.deferred_shim.DeferredRegisterShim")
                .getDeclaredField("registryKey");
            rkF.setAccessible(true);
            java.lang.reflect.Method entriesM = Class.forName("yesman.epicfight.registry.deferred_shim.DeferredRegisterShim")
                .getMethod("getEntries");
            java.lang.reflect.Field hF = Class.forName("yesman.epicfight.registry.deferred_shim.DeferredHolderShim")
                .getDeclaredField("holder");
            hF.setAccessible(true);
            for (Object shim : list) {
                java.util.Set<?> entries = (java.util.Set<?>) entriesM.invoke(shim);
                int bound = 0;
                for (Object e : entries) if (hF.get(e) != null) bound++;
                LOGGER.info("probe shim {} accepted={} entries={} bound={}",
                    rkF.get(shim), accF.get(shim), entries.size(), bound);
            }
            net.minecraft.resources.ResourceLocation rl =
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("epicfight", "stun_immunity");
            LOGGER.info("probe stun_immunity inRegistry={}",
                net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.containsKey(rl));
        } catch (Throwable t) {
            LOGGER.warn("probeEffectBinding failed", t);
        }
    }

    private void onResourceReload(AddReloadListenerEvent event) {
        event.addListener(new SpellAnimationLoader());
    }
}
