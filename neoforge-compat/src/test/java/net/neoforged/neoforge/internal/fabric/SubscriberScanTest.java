package net.neoforged.neoforge.internal.fabric;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * scanSubscribers: ModOrigin kind gating (getPaths only for PATH), dir and jar
 * roots, missing entries, and client/server annotation filtering. Each test
 * uses its own fixture classes — registered subscribers are deduped JVM-wide.
 */
public class SubscriberScanTest {

    private static final String PKG = "net.neoforged.neoforge.internal.fabric.scantest";

    private static Object defaultReturn(Class<?> type) {
        if (!type.isPrimitive()) {
            return type == String.class ? "" : List.class.isAssignableFrom(type) ? List.of() : null;
        }
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return 0;
    }

    /** A ModContainer stub whose origin honors the PATH/NESTED contract. */
    private static ModContainer container(Path root, ModOrigin.Kind kind, boolean failGetPaths) {
        ClassLoader cl = SubscriberScanTest.class.getClassLoader();
        var meta = (net.fabricmc.loader.api.metadata.ModMetadata) Proxy.newProxyInstance(cl,
                new Class[] { net.fabricmc.loader.api.metadata.ModMetadata.class },
                (p, m, a) -> "getId".equals(m.getName()) ? "scan_fixture" : defaultReturn(m.getReturnType()));
        ModOrigin origin = (ModOrigin) Proxy.newProxyInstance(cl, new Class[] { ModOrigin.class },
                (p, m, a) -> switch (m.getName()) {
                    case "getKind" -> kind;
                    case "getPaths" -> {
                        if (failGetPaths) {
                            throw new AssertionError("getPaths() must not be called for " + kind);
                        }
                        yield List.of(root);
                    }
                    default -> defaultReturn(m.getReturnType());
                });
        return (ModContainer) Proxy.newProxyInstance(cl, new Class[] { ModContainer.class },
                (p, m, a) -> switch (m.getName()) {
                    case "getMetadata" -> meta;
                    case "getOrigin" -> origin;
                    case "getRootPaths" -> List.of(root);
                    default -> defaultReturn(m.getReturnType());
                });
    }

    private static Path testClassesDir() {
        return Path.of(System.getProperty("user.dir"), "build", "classes", "java", "test");
    }

    private static Path fixtureDir(Path tmp, String... classes) throws Exception {
        Path root = tmp.resolve("classes");
        Path pkg = root.resolve(PKG.replace('.', '/'));
        Files.createDirectories(pkg);
        Path classesDir = testClassesDir();
        for (String c : classes) {
            Files.copy(classesDir.resolve(PKG.replace('.', '/') + "/" + c + ".class"),
                    pkg.resolve(c + ".class"));
        }
        Files.writeString(root.resolve("fabric.mod.json"),
                "{\"schemaVersion\":1,\"id\":\"scan_fixture\",\"entrypoints\":{\"neoforge\":[]}}");
        return root;
    }

    private static String cn(String simple) {
        return PKG + "." + simple;
    }

    @Test
    void pathKindDirectoryServerDist(@TempDir Path tmp) throws Exception {
        Path root = fixtureDir(tmp, "CommonSubscriber", "ClientOnlyA", "ServerOnlyA");
        NeoForgeCompat.scanSubscribers(List.of(container(root, ModOrigin.Kind.PATH, false)), true);
        assertTrue(NeoForgeCompat.isSubscriberRegistered(cn("CommonSubscriber")), "common subscriber registered");
        assertTrue(NeoForgeCompat.isSubscriberRegistered(cn("ServerOnlyA")), "server-only subscriber registered on server");
        assertFalse(NeoForgeCompat.isSubscriberRegistered(cn("ClientOnlyA")), "client-only skipped on dedicated server");
    }

    @Test
    void pathKindDirectoryClientDist(@TempDir Path tmp) throws Exception {
        Path root = fixtureDir(tmp, "ClientOnlyB", "ServerOnlyB");
        NeoForgeCompat.scanSubscribers(List.of(container(root, ModOrigin.Kind.PATH, false)), false);
        assertTrue(NeoForgeCompat.isSubscriberRegistered(cn("ClientOnlyB")), "client-only registered on client");
        assertFalse(NeoForgeCompat.isSubscriberRegistered(cn("ServerOnlyB")), "server-only skipped on client");
    }

    @Test
    void pathKindJarScans(@TempDir Path tmp) throws Exception {
        Path jar = tmp.resolve("fixture.jar");
        Path classesDir = testClassesDir();
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry(PKG.replace('.', '/') + "/JarSubscriber.class"));
            Files.copy(classesDir.resolve(PKG.replace('.', '/') + "/JarSubscriber.class"), out);
            out.closeEntry();
            out.putNextEntry(new JarEntry("fabric.mod.json"));
            out.write("{\"schemaVersion\":1,\"id\":\"scan_fixture\"}".getBytes());
            out.closeEntry();
        }
        NeoForgeCompat.scanSubscribers(List.of(container(jar, ModOrigin.Kind.PATH, false)), true);
        assertTrue(NeoForgeCompat.isSubscriberRegistered(cn("JarSubscriber")),
                "jar-scanned subscriber must register");
    }

    @Test
    void nestedKindUsesRootPathsNotGetPaths(@TempDir Path tmp) throws Exception {
        Path root = fixtureDir(tmp, "NestedSubscriber");
        // failGetPaths=true: any getPaths() call on this NESTED origin fails the test
        NeoForgeCompat.scanSubscribers(List.of(container(root, ModOrigin.Kind.NESTED, true)), true);
        assertTrue(NeoForgeCompat.isSubscriberRegistered(cn("NestedSubscriber")),
                "NESTED origin scanned via rootPaths");
    }

    @Test
    void missingJarEntryReturnsNullInfo(@TempDir Path tmp) throws Exception {
        Path jar = tmp.resolve("empty.jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry("fabric.mod.json"));
            out.write("{}".getBytes());
            out.closeEntry();
        }
        var info = NeoForgeCompat.readSubscriberInfo(jar, "net.neoforged.no.such.Class");
        assertNull(info, "missing jar entry must return null, not throw");
        NeoForgeCompat.scanSubscribers(List.of(container(jar, ModOrigin.Kind.PATH, false)), true);
    }
}
