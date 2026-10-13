package com.grandsigil;

import com.grandsigil.config.GsConfig;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEffects;
import com.grandsigil.registry.GsEntities;
import com.grandsigil.registry.GsSpells;
import com.grandsigil.registry.GsTabs;
import com.grandsigil.spell.GrandSigilSpell;
import com.grandsigil.spell.HeavenGateSpell;
import com.grandsigil.spell.SkybreakSpell;
import com.grandsigil.spell.SunfallSpell;
import com.grandsigil.spell.WorldTreeSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("grandsigil")
public class GrandSigil {
   public static final String MOD_ID = "grandsigil";
   public static final Logger LOGGER = LoggerFactory.getLogger("Grand Sigil");

   public GrandSigil(IEventBus modBus, ModContainer container) {
      GsEntities.register(modBus);
      GsEffects.register(modBus);
      GsSpells.register(modBus);
      GsTabs.register(modBus);
      container.registerConfig(Type.COMMON, GsConfig.COMMON_SPEC);
      container.registerConfig(Type.CLIENT, GsConfig.CLIENT_SPEC);
      NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, GrandSigil::report);
   }

   private static void report(ServerStartedEvent event) {
      Registry<DamageType> damageTypes = event.getServer().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
      StringBuilder line = new StringBuilder();

      for (Entry<ResourceLocation, ResourceKey<DamageType>> entry : List.of(
         Map.entry(GrandSigilSpell.ID, GsDamage.GRAND_SIGIL),
         Map.entry(SunfallSpell.ID, GsDamage.SUNFALL),
         Map.entry(SkybreakSpell.ID, GsDamage.SKYBREAK),
         Map.entry(WorldTreeSpell.ID, GsDamage.WORLD_TREE),
         Map.entry(HeavenGateSpell.ID, GsDamage.HEAVEN_GATE)
      )) {
         AbstractSpell spell = SpellRegistry.REGISTRY.get(entry.getKey());
         line.append(line.length() == 0 ? "" : "  |  ")
            .append(entry.getKey().getPath())
            .append(": spell=")
            .append(spell != null ? "ok" : "MISSING")
            .append(" school=")
            .append(spell != null ? spell.getDefaultConfig().schoolResource : "-")
            .append(" damage=")
            .append(damageTypes.getOptional(entry.getValue()).isPresent() ? "ok" : "MISSING");
      }

      LOGGER.info(
         "Apex Arcana ready -- entities={} | {}",
         GsEntities.SIGIL.get() != null
               && GsEntities.SUN.get() != null
               && GsEntities.BOLT.get() != null
               && GsEntities.TREE.get() != null
               && GsEntities.GATE.get() != null
            ? "ok"
            : "MISSING",
         line
      );
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("grandsigil", path);
   }
}
