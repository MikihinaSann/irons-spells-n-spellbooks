package net.neoforged.neoforge.internal.fabric;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Processes {@code META-INF/enumextensions.json} shipped by NeoForge-style
 * mods, instantiating the declared enum constants through {@link EnumExtender}
 * before mod construction so {@code Enum#valueOf} lookups succeed.
 */
public final class EnumExtensionLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");
    private static final String PATH = "META-INF/enumextensions.json";

    private EnumExtensionLoader() {
    }

    /** Create all constants declared by the given NeoForge-style mods. */
    public static void process(List<ModContainer> mods) {
        for (ModContainer container : mods) {
            for (Path root : rootsOf(container)) {
                try (InputStream in = open(root)) {
                    if (in == null) {
                        continue;
                    }
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
                    JsonArray entries = json.getAsJsonArray("entries");
                    if (entries == null) {
                        continue;
                    }
                    for (JsonElement el : entries) {
                        try {
                            processEntry(container.getMetadata().getId(), el.getAsJsonObject());
                        } catch (Throwable t) {
                            LOGGER.error("enumextensions entry failed for mod {}", container.getMetadata().getId(), t);
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.warn("enumextensions read failed for mod {}", container.getMetadata().getId(), t);
                }
            }
        }
    }

    private static List<Path> rootsOf(ModContainer container) {
        List<Path> roots = new ArrayList<>();
        if (container.getOrigin().getKind() == net.fabricmc.loader.api.metadata.ModOrigin.Kind.PATH) {
            for (Path p : container.getOrigin().getPaths()) {
                Path abs = p.toAbsolutePath().normalize();
                if (!abs.toString().equals(java.io.File.separator)) {
                    roots.add(abs);
                }
            }
        }
        if (roots.isEmpty()) {
            roots.addAll(container.getRootPaths());
        }
        Set<Path> dedup = new LinkedHashSet<>(roots);
        return List.copyOf(dedup);
    }

    private static InputStream open(Path root) {
        try {
            if (Files.isDirectory(root)) {
                Path file = root.resolve(PATH);
                return Files.exists(file) ? Files.newInputStream(file) : null;
            }
            try (JarFile jar = new JarFile(root.toFile())) {
                JarEntry entry = jar.getJarEntry(PATH);
                if (entry == null) {
                    return null;
                }
                byte[] bytes = jar.getInputStream(entry).readAllBytes();
                return new java.io.ByteArrayInputStream(bytes);
            }
        } catch (Throwable t) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void processEntry(String modid, JsonObject entry) throws Throwable {
        String enumName = entry.get("enum").getAsString().replace('/', '.');
        String constantName = entry.get("name").getAsString();
        String ctorDesc = entry.get("constructor").getAsString();
        JsonObject params = entry.getAsJsonObject("parameters");

        Class<? extends Enum> enumClass;
        try {
            enumClass = (Class<? extends Enum>) Class.forName(enumName);
        } catch (Throwable t) {
            // Client-only enums (e.g. HumanoidModel$ArmPose) can't load on a
            // dedicated server — the constant isn't reachable there anyway.
            LOGGER.warn("enumextensions: skipping {} on {} — enum class unavailable ({})",
                    constantName, enumName, t);
            return;
        }
        int paramCount = descriptorParamCount(ctorDesc);
        Class<?>[] ctorParamTypes = enumClass.getDeclaredConstructors()[0].getParameterTypes();

        String providerClass = params.get("class").getAsString().replace('/', '.');
        Class<?> provider = Class.forName(providerClass);
        if (params.has("field")) {
            // EnumProxy fields resolve their own constant lazily — delegate so
            // a single constant exists under the proxy's derived name.
            Object field = provider.getDeclaredField(params.get("field").getAsString()).get(null);
            if (field instanceof EnumProxy<?> proxy) {
                Enum constant = (Enum) proxy.get();
                LOGGER.info("enumextensions: created {} on {}", constant.name(), enumName);
                return;
            }
            if (!(field instanceof Object[])) {
                throw new IllegalStateException("Unsupported parameters.field type: " + field.getClass());
            }
        }
        Object[] args = null;
        if (params.has("method")) {
            Method m = provider.getDeclaredMethod(params.get("method").getAsString(), int.class, Class.class);
            m.setAccessible(true);
            args = new Object[paramCount];
            for (int i = 0; i < paramCount; i++) {
                // declared ctor params follow the implicit (String name, int ordinal) pair
                args[i] = m.invoke(null, i, ctorParamTypes[i + 2]);
            }
        } else if (params.has("field")) {
            args = (Object[]) provider.getDeclaredField(params.get("field").getAsString()).get(null);
        } else {
            throw new IllegalStateException("enumextensions parameters missing method/field");
        }
        Enum constant = EnumExtender.create((Class) enumClass, constantName, args);
        LOGGER.info("enumextensions: created {} on {}", constantName, enumName);
    }

    private static int descriptorParamCount(String desc) {
        int start = desc.indexOf('(') + 1;
        int end = desc.indexOf(')');
        int count = 0;
        for (int i = start; i < end; i++) {
            char c = desc.charAt(i);
            if (c == 'L') {
                i = desc.indexOf(';', i);
                count++;
            } else if (c == '[') {
                // scan arrays
                while (desc.charAt(i) == '[') i++;
                if (desc.charAt(i) == 'L') {
                    i = desc.indexOf(';', i);
                }
                count++;
            } else {
                count++;
            }
        }
        return count;
    }
}
