package com.yukami.efiscompat.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Gates all efiscompat mixins on EpicFight being present. Several mixins target
 * ISS/vanilla classes ({@code AbstractSpell}, {@code AnimationHelper},
 * {@code Inventory}) and would apply even without EpicFight, then crash when
 * their injected code touches EpicFight classes — so the whole config is
 * conditional, not just the EpicFight-targeted entries.
 */
public class EfisCompatMixinPlugin implements IMixinConfigPlugin {
    private boolean enabled;

    @Override
    public void onLoad(String mixinPackage) {
        enabled = FabricLoader.getInstance().isModLoaded("epicfight");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!enabled) {
            return false;
        }
        // Some mixins target optional third-party mods (e.g. Invincible's
        // ComboBasicAttack). Skip any whose target class isn't on the classpath.
        // NOTE: check the resource, never Class.forName — loading the target
        // here (superclass chain included) would poison other mods' mixins
        // (e.g. playeranimator's FeatureRendererMixin on RenderLayer).
        return targetClassName == null || classExists(targetClassName);
    }

    private static boolean classExists(String className) {
        return EfisCompatMixinPlugin.class.getClassLoader()
                .getResource(className.replace('.', '/') + ".class") != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
