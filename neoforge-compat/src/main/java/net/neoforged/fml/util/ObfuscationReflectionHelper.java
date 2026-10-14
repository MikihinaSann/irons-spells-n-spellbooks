package net.neoforged.fml.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Named-environment reflection helper — looks members up by their Mojmap name
 * (this runtime always runs under Mojmap, so no SRG remapping is needed).
 */
public final class ObfuscationReflectionHelper {
    private ObfuscationReflectionHelper() {
    }

    public static Field findField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new RuntimeException(new NoSuchFieldException(
                clazz.getName() + "." + name));
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod(name, parameterTypes);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) {
            }
        }
        throw new RuntimeException(new NoSuchMethodException(
                clazz.getName() + "." + name));
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName) {
        try {
            return (T) findField(classToAccess, fieldName).get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String fieldName) {
        try {
            findField(classToAccess, fieldName).set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
