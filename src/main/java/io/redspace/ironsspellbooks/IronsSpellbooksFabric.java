package io.redspace.ironsspellbooks;

import net.neoforged.neoforge.internal.fabric.FabricEventBus;
import net.neoforged.neoforge.internal.fabric.NeoForgeCompat;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

/**
 * ISS-side common setup hook, invoked by the neoforge_compat lifecycle driver
 * (via the {@code neoforge:setup} entrypoint) after the generic NeoForge
 * lifecycle has run. Holds ISS-specific registrations that used to tail
 * {@code onInitialize}: command argument types, the spellbook-equip trigger,
 * Trinkets equip callbacks, ISS biome modifications and Angel Wings flight.
 */
public class IronsSpellbooksFabric implements ModInitializer {
    /** Shared mod bus - alias of the compat driver's single bus. */
    public static final FabricEventBus MOD_BUS = NeoForgeCompat.MOD_BUS;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onInitialize() {
        IronsSpellbooks.LOGGER.info("ISS-FABRIC SETUP envType={}",
                net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType());

        // EnumArgument's type info must be registered or command-tree sync
        // (LAN open, dedicated server join) throws "Unrecognized argument type".
        net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(
                IronsSpellbooks.id("enum"),
                (Class) net.neoforged.neoforge.server.command.EnumArgument.class,
                new net.neoforged.neoforge.server.command.EnumArgument.Info());

        // advancements: spellbook equip trigger over Trinkets
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.TRIGGER_TYPES,
                IronsSpellbooks.id("equip_spellbook"),
                io.redspace.ironsspellbooks.advancements.EquipSpellbookTrigger.INSTANCE);
        dev.emi.trinkets.api.event.TrinketEquipCallback.EVENT.register((stack, ref, entity) -> {
            if (entity instanceof net.minecraft.server.level.ServerPlayer player
                    && ref.inventory().getSlotType().getName().equals(io.redspace.ironsspellbooks.compat.Curios.SPELLBOOK_SLOT)) {
                io.redspace.ironsspellbooks.advancements.EquipSpellbookTrigger.INSTANCE.trigger(player);
            }
        });

        // Angel Wings grants elytra flight without a chest stack — FAPI's
        // EntityElytraEvents.CUSTOM is the hook NeoForge's
        // ItemStack.canElytraFly/elytraFlightTick correspond to.
        net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents.CUSTOM.register(
                (entity, tickElytra) -> entity.hasEffect(io.redspace.ironsspellbooks.registries.MobEffectRegistry.ANGEL_WINGS));

        // ISS biome additions: mithril ore + necromancer spawns in the overworld.
        var overworld = net.fabricmc.fabric.api.biome.v1.BiomeSelectors.tag(net.minecraft.tags.BiomeTags.IS_OVERWORLD);
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addFeature(overworld,
                net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES,
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.PLACED_FEATURE,
                        IronsSpellbooks.id("ore_mithril_placement")));
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addSpawn(overworld,
                net.minecraft.world.entity.MobCategory.MONSTER,
                io.redspace.ironsspellbooks.registries.EntityRegistry.NECROMANCER.get(), 18, 1, 1);

    }
}
