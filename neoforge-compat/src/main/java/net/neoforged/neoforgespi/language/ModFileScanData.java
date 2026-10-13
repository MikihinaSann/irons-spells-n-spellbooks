package net.neoforged.neoforgespi.language;

import org.objectweb.asm.Type;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Stub of NeoForge's class-file scan data. Fabric has no equivalent — this
 * returns empty results so compile-time references resolve.
 */
public class ModFileScanData {
    public Set<AnnotationData> getAnnotations() {
        return Set.of();
    }

    public Set<Type> getClasses() {
        return Set.of();
    }

    public interface AnnotationData {
        default Type annotationType() {
            return null;
        }

        default java.util.Map<String, Object> annotationData() {
            return java.util.Map.of();
        }

        default Type clazz() {
            return null;
        }

        default String memberName() {
            return "";
        }
    }
}
