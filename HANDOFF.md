# Handoff — ISS + addon ecosystem, NeoForge → Fabric 1.21.1 port

## Goal

Port Iron's Spells 'n Spellbooks + the user's whole modpack ecosystem to **Fabric 1.21.1**, merging addon functionality into the ISS tree where practical ("มัดรวมไปเลยจบๆ"). End state: server + client launch, connect, and play normally with all spells/mobs/rendering working.

## Environment

- Minecraft 1.21.1, Fabric Loader 0.19.5, Fabric Loom 1.17.19, JDK 21.0.12
- Repo root: `C:\Users\nonza\Documents\GitHub\irons-spells-n-spellbooks`
- Key libs in `libs/`: trinkets-3.10.0, geckolib-fabric-4.9.3, azurelib-3.1.18, epicfight-fabric-21.17.3, player-animation-lib-2.0.4, tacz-refabricated-0.7.0, mowziesmobs-1.8.2, legendary_monsters-2.2.3, jei, cloth-config, biolith, citadel-video-deps
- forgeconfigapiport (FCAP) 21.1.4 supplies `net.neoforged.neoforge.common.ModConfigSpec` etc.
- Original modpack jars live at `C:\Users\nonza\Downloads\kampanarth\mods\` and `C:\Users\nonza\Downloads\modsCCWYK\` (needed when re-decompiling)
- `vineflower.jar` at repo root is the decompiler; `_addons_src/` holds raw decompile output; `_vendor_addon.py` vendors a decompiled dir into `mods/<modid>/`

## Architecture

- **Root project** = ISS (`irons_spellbooks` mod id). Its fabric.mod.json also `provides`: `irons_lib`, `grandsigil`, `crystal_chronicles`, `efiscompat`, `ironsarms`, `familiarslib`.
- **`neoforge-compat/`** = the NeoForge API shim (mod id `neoforge_compat`, also `provides: curios`). Emulates: `@Mod` construction via `neoforge` entrypoint, `IEventBus`/mod bus (`NeoForgeCompat.MOD_BUS`, scoped per-mod via `MOD_BUS.scoped(modid)`), `DeferredRegister` (queues to `PENDING`, drained in lifecycle), `DeferredHolder` (implements `Holder<R>` — lazy `get()` resolves after drain), `@EventBusSubscriber` (ASM-scanned from .class files, filters by `value`/`dist` arrays, `modid`, `bus`), NeoForge events bridged to Fabric events, attachments, capabilities, Curios-over-Trinkets, config via FCAP (`FabricConfigLoader`, `FabricConfigLoader.postScoped`).
- **`mods/<name>/`** = vendored addon subprojects. Each is a loom subproject producing a `-dev.jar` in `build/devlibs/`. Root `build.gradle` wires them via `modImplementation files("mods/<name>/build/devlibs/...")` (or `project(path:, configuration: 'namedElements')` for safe ones) + loom `mods {}` sourceSet blocks.
- Original NeoForge jar sources were decompiled with vineflower → expect **decompiler artifacts** everywhere (see "Known decompiler pitfalls").

### Ported modules (all compile, all load on dedicated server)

`aces_spell_utils, aero_additions, alexscaves, alshanex_familiars, apothic_attributes, borninchaos, cataclysm, cataclysm_spellbooks, chronomancy, citadel, cobweb, dash, discerning_the_eldritch, expandability, hazennstuff, horror_element_mod, iss_magicfromtheeast, lionfishapi, placebo, prometheus, somakespells, soul_fire_d, spellelemental, traveloptics, tunes_n_tomes` (+ familiarslib merged into root ISS tree — the standalone `mods:familiarslib` project was removed from `settings.gradle` and all wiring; root fmj `provides: familiarslib`).

**Excluded** (missing hard deps, intentional): `better_spellcasting` (needs Better Combat), `ec_isasb_plugin` (needs Expanded Combat), `irons_spells_js` (KubeJS bridge).

## How to run

```bash
./gradlew build                 # full build (all modules)
./gradlew :mods:<name>:jar      # rebuild one module's dev jar (run after editing it)
./gradlew :runServer            # dedicated server — MUST have the colon! bare `runServer`/`runClient`
./gradlew :runClient            #   also runs every subproject's run task and fails
```

Run dirs: **server uses `run_server/`** (own world + config, world migrated from `run/world`), **client uses `run/`**. Splitting stopped the two JVMs from racing corrections on shared `run/config/` files. Logs get redirected to `_server.log` / `_client.log` at repo root by convention.

## Current state (verified)

- `BUILD SUCCESSFUL` — all 25 modules compile together.
- Dedicated server reaches `Done (~1.7s)!` with all addons loaded, zero `@Mod` construction failures.
- Client loads 103 mods in ONE clean resolution pass and **joins the server** (`Player158 joined the game`).
- Previously user-verified in game: Leviathan beam VFX, Cataclysm natural mob spawns, BIC Epic Fight patches (door_knight/pumpkinhead/bonescaller/fisherman weapon attachment + knee/elbow joints), `/cast`, mana HUD, spellbook Trinkets equip.
- `EpicFightPatchProbe` removed (was log spam).

## Known decompiler pitfalls (recurring — check these first when a module misbehaves)

1. **`@EventBusSubscriber({Dist.CLIENT})` on common handlers** — vineflower adds wrong `dist`. Kills server-side registration silently. Fixed: `HnSAttributeRegistry`, `HnSCommonSetup` (all entity attributes!), ~20 hazennstuff item `SpellEvents` inner classes (`ModifySpellLevelEvent` weapon bonuses), prometheus `FMLSetupEventsHandler` (`FMLCommonSetupEvent` dispenser registration). Audit: search `Dist.CLIENT` + `@SubscribeEvent` with non-client event types.
2. **Config `BUILDER.build()` before `define()`** — field-init reorder. Symptom: `Cannot get config value before spec is built`. Fixed: `CSConfig` (cataclysm_spellbooks), `MFTEServerConfigs`. Audit: `build()` line must be textually after last `define()` in the file.
3. **Client class refs in common classes** — `Minecraft.getInstance()` / `LocalPlayer` in item/block/registry class method bodies trips the verifier on dedicated server (`Cannot load class net.minecraft.client...`). Fixes used: `DataTickets.ENTITY` in GeckoLib `AnimationState` (note: raw `getData` returns Object — needs cast; `isFallFlying` is on `LivingEntity` not `Entity`); `Util.getMillis()/50` for animated names; renderer suppliers moved to `HazennstuffClientRenderers`/`XxxClientRenderers` holder classes returning `Object` (ISS `supplyRenderer()` pattern).
4. **`neoForge`/NeoForge-only APIs missing in vanilla** — shimmed in compat: `ItemTags.create`→`TagKey.create`, `CreativeModeTab.builder(Row.TOP,0)`, `ItemBlockRenderTypes.setRenderLayer(Fluid)`/`setBlockRenderLayer`→`NeoForgeFluidRenderLayers`/`NeoForgeBlockRenderLayers` (Fabric `BlockRenderLayerMap`), `ILevelExtension.getCapability`, `EventHooks.onChorusFruitTeleport`/`fireBonemealEvent`, `PlayerXpEvent`, `PlaySoundEvent`, `LivingDestroyBlockEvent`, `ChunkEvent`, `ModLoadingContext`/`registerConfig`, `AddPackFindersEvent.addPackFinders`, `Mod.dist()` returning `Dist[]`.
5. **Missing generics on `new Goal<>()`** — decompiler drops `<>`, raw types break lambdas. Add `<>` ONLY to generic goals (`NearestAttackableTargetGoal` yes; `HurtByTargetGoal`/`ResetUniversalAngerTargetGoal`/`OwnerHurt*` NO).
6. **`DeferredItem`/`DeferredBlock` must implement `ItemLike`** in compat.
7. **`META-INF/services` provider files don't survive vendoring** — cobweb/prometheus/soul_fire_d use `ServiceLoader` (`Services.PLATFORM/REGISTRY/CONFIG/NETWORK`); created `META-INF/services/it.crystalnest.*.platform.services.*` → `NeoForge*Helper` in each module's resources. Without them: NPE in `Services` clinit → `@Mod` construction skipped → later nulls (this was the Prometheus `CUSTOM_CAMPFIRE_ENTITY_TYPE == null` crash — NOT a lifecycle ordering bug).
8. **Cobweb `NeoForgeRegistryHelper.bus` set late** — `register(IEventBus)` is only called by cobweb's own `ModLoader` ctor; other mods calling `CobwebRegistry.of()` in their ctors race it. Fixed with `bus()` fallback → `NeoForgeCompat.MOD_BUS`.
9. **fmj `depends` on external libs spams resolution** — loom dev launcher resolves multiple classpath passes; hard `depends` on trinkets/geckolib/forgeconfigapiport/playeranimator fails subset passes → "Incompatible mods found!" spam every launch (game still starts). Fixed by removing those keys from `depends` in all fmj files (they're always on the root classpath). Keep internal deps (`irons_spellbooks`, `neoforge_compat`, etc.) — they resolve.
10. **Mixin config path vs package** — mixin JSON class names must match actual package (e.g. `client.PlaySoundEventMixin`). Audit script exists; inner classes in mixin lists are false positives.
11. **Stale `build/resources`** — deleting source resources does NOT remove them from `build/resources/main` until that module's `jar`/`processResources` reruns. Always `./gradlew :mods:<name>:jar` after resource edits.

## Data cleanup done

- Deleted 39 dead recipe JSONs (`data/*/recipe/`): cataclysm_spellbooks WIP items never registered upstream (technomancy_rune, gauntlets, excelsius upgrades, engineer set, murasama, discs, fragments), `malum:`/`pastel:` compat recipes, DTE `bucket_of_malice`, somakespells missing items. These are upstream data bugs, not port bugs.

## Remaining issues / TODO

- [x] ~~Config `.bak` churn~~ — FIXED by giving the server its own `run_server/` dir (two JVMs no longer race the same `run/config/` files).
- [ ] **Watchdog/save deadlock on Gradle stop** — `EntityStorage.flush` blocked during save when stopping server via Gradle stdin EOF; world already loaded fine. Use `:runServer` and kill via task, or investigate if it recurs.
- [ ] **Full playthrough polish pass** — in-game verify all addons: spells, mobs, armor renders, particles, GUIs; hazennstuff weapon spell-level bonuses now actually fire server-side (newly enabled — test).
- [x] ~~`mods:familiarslib` dir still exists~~ — deleted (commit `078413cc5`); merged into root ISS.
- [x] ~~uncommitted files~~ — checkpoint commits `613ec51ec` (runtime fixes + all modules) and `078413cc5` (familiarslib removal).
- [ ] Re-check `neoforge_compat.mixins.json` entries after any package moves (audit script pattern from history).
- [ ] Optional-dep stubs exist: `ess_requiem` (ESSRCompat guarded by `LoadedOnly`), mowzies/legendary_monsters gated in somakespells via `SomakeMixinPlugin`/`ModCompatEntityTypes` (jars are in `libs/` if full integration is wanted later).

## Useful locations

- Compat lifecycle: `neoforge-compat/.../internal/fabric/NeoForgeCompat.java` (mod scanning/construction, `MOD_BUS`, registry drain) and `NeoForgeCompatLifecycle.java` (client lifecycle, pack finders).
- Event dispatch mixins: `neoforge-compat/.../internal/fabric/mixin*/`, config: `neoforge_compat.mixins.json`.
- Cobweb service helper: `mods/cobweb/.../platform/NeoForgeRegistryHelper.java` (`bus()` fallback added).
- Vendor script: `_vendor_addon.py`; decompiled sources: `_addons_src/`; original jars: `Downloads/kampanarth/mods`, `Downloads/modsCCWYK`.
- RCON test helper: `rcon.py`.

## Git conventions

- Commits use `Co-Authored-By: Rina` trailer (see `rina-commit-identity` skill / recent log).
- Recent: `8321858f7` EF mob patches, `4076ba779` cataclysm bow fix, `ab47b90e8` chronomancy, `6ea25e320` BIC+traveloptics, `099f86c21` cataclysm.
