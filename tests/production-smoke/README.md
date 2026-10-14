# Production-ABI smoke probe (test-only fixture)

Verifies that the `loom:injected_interfaces` table in
`neoforge-compat/src/main/resources/fabric.mod.json` is actually satisfied on a
**real intermediary production runtime** (obfuscated official MC jar +
loader-side official→intermediary transform), where Loom's dev-time interface
injection does NOT run. Without the runtime bridge mixins
(`neoforge-compat/.../internal/fabric/mixin/api/`), every row fails.

**This tree is a fixture — never packaged into any production jar.**
The probe runs against a disposable world only; it never touches user data.

## Contents (`src/`)

- `issprobe/IssProdProbe.java` — main entrypoint. Asserts every canonical
  common interface row via `isAssignableFrom` on class literals, exercises real
  API (`ItemStack.canElytraFly`, supplier-component `get`, `Holder.getKey`,
  familiars registry alias), then runs the DamageEventDispatcher scenario
  matrix on `SERVER_STARTED`: mobs with/without armor/absorption, Sundering I,
  and `Player.actuallyHurt` via `ProbeFakePlayer`. The `onPost` fixture
  captures only the expected victim+source and asserts exactly one Post with
  the recorded damage.
- `issprobe/IssProdProbeClient.java` — six client-only rows + the two
  covered rows (GuiGraphics, ClientPacketListener), aggregated hard failure.
- `issprobe/ProbeFakePlayer.java` + `issprobe/ProbeConnection.java` —
  fake `ServerPlayer` with a no-op packet listener so `CombatTracker` can
  deliver damage packets (no online user needed).
- `issprobe/mixin/ServerPlayerAcc.java` — `@Accessor` for
  `spawnInvulnerableTime` (otherwise `ServerPlayer.hurt` always returns false
  for an unticked fake player). The accessor names the intermediary field
  `field_13998` directly — tiny-remapper does not remap annotation strings.
- `issprobe/ProbeAssertionsSelfTest.java` — standalone `main`, no server:
  `Bootstrap.bootStrap()` + `Unsafe.allocateInstance` builds a Post event and
  verifies the capture filter rejects wrong entity/source and the amount
  check rejects a wrong value. `System.exit(1)` on any failure.
- `fabric.mod.json` / `iss_probe.mixins.json` — probe mod metadata.

## Recipe (fully offline, cached artifacts)

```bash
# 1. compile (named): classpath = loom named merged jar (the one carrying the
#    injected interfaces, e.g. minecraft-merged-<hash>-…hash.2198-v2.jar with
#    our fmj's injected_interfaces applied), neoforge-compat +
#    mods/apothic_attributes build/classes/java/main, remapped named FAPI jars,
#    fabric-loader, guava, slf4j, com.mojang logging, authlib
mkdir -p out probe-jar
javac -proc:none -classpath "<compile classpath described above>" -d out \
    src/issprobe/*.java src/issprobe/mixin/*.java

# 2. package + remap named -> intermediary (tiny-remapper needs the named MC
#    jar on ITS classpath or member names do not remap!)
cp -R out/. probe-jar/
cp src/fabric.mod.json src/iss_probe.mixins.json probe-jar/
jar cf iss_prod_probe-named.jar -C probe-jar .
java -cp "<cached tiny-remapper;mapping-io;asm jar paths>" net.fabricmc.tinyremapper.Main \
    iss_prod_probe-named.jar iss_prod_probe.jar \
    "<repo>/.gradle/loom-cache/source_mappings/<hash>.tiny" named intermediary \
    "<named merged jar>"        # <- required as remapper classpath

# 3. standalone assertion self-test (no server):
java -cp "out;<named mc jar>;<compat classes and dependencies>" issprobe.ProbeAssertionsSelfTest
#   -> [SELFTEST] ALL PASSED

# 4. production smoke: disposable run dir with mods/ = remapJar outputs +
#    fabric-api fat jar + trinkets/biolith/azurelib/cloth-config/epicfight/
#    geckolib/playeranimator/tacz/forgeconfigapiport/cardinal-components +
#    iss_prod_probe.jar. Do NOT put plain night-config/jcodec/jaad on the
#    classpath — they must come from FCAP's and Citadel's nested jars.
#    server: official server-1.21.1.jar (inside minecraft-server.jar bundler)
#    + intermediary-1.21.1-v2.jar on classpath, launcher = FabricServerLauncher
#    with fabric-server-launcher.properties serverJar=server-1.21.1.jar
java -cp "<libs and loader classpath>" net.fabricmc.loader.impl.launch.server.FabricServerLauncher nogui
#    client: official minecraft-client.jar on classpath + natives extracted
#    from lwjgl-*-natives-windows-3.3.3 jars + -Djava.library.path, KnotClient:
java -cp "<libs, loader and client jar classpath>" net.fabricmc.loader.impl.launch.knot.KnotClient \
    --gameDir client --assetsDir "<loom assets>" --assetIndex 1.21.1-17 \
    --username ProbeClient --accessToken dummy --userType legacy \
    --quickPlayMultiplayer 127.0.0.1:<port>
#    (preseed client/options.txt skipMultiplayerWarning:true so quickPlay joins)
```

## Pass/fail markers in the log

- `[ISS-PROBE] ALL CHECKS PASSED` / `client ABI check PASSED` — success.
- `[ISS-PROBE] FAIL ...` / `N FAILURES` / `client ABI check FAILED` — hard
  failure; every check aggregates, nothing prints unconditional success.
