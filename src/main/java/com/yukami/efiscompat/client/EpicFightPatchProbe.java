package com.yukami.efiscompat.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Runtime diagnostic for EF-patched custom mobs: dumps armature, animator
 * livingAnimations map, and current living motions for born_in_chaos entities.
 * Registered on the client tick; each entity type is probed only once.
 */
public final class EpicFightPatchProbe {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> PROBED = new HashSet<>();
    private static int cooldown = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(EpicFightPatchProbe::onClientTick);
    }

    private static void onClientTick(Minecraft mc) {
        if (--cooldown > 0 || mc.level == null) return;
        cooldown = 40;
        try {
            for (Entity e : mc.level.entitiesForRendering()) {
                if (!(e instanceof LivingEntity living)) continue;
                String ns = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                        .getKey(e.getType()).getNamespace();
                if (!ns.equals("born_in_chaos_v1") && !ns.equals("cataclysm")
                        && !(ns.equals("minecraft") && (e.getType() == net.minecraft.world.entity.EntityType.ZOMBIE
                        || e.getType() == net.minecraft.world.entity.EntityType.SKELETON))) continue;
                String key = e.getType().toString();
                if (!PROBED.add(key)) continue;
                probe(living);
            }
        } catch (Throwable t) {
            LOGGER.warn("patch probe failed", t);
        }
    }

    private static void probe(LivingEntity e) {
        String id = e.getType().toString();
        try {
            Class<?> patchClass = Class.forName("yesman.epicfight.world.capabilities.entitypatch.EntityPatch");
            Object opt = Class.forName("yesman.epicfight.world.capabilities.EpicFightCapabilities")
                    .getMethod("getUnparameterizedEntityPatch", Entity.class, Class.class)
                    .invoke(null, e, patchClass);
            Object patch = unwrap(opt);
            if (patch == null) {
                LOGGER.info("[EFProbe] {} -> NO PATCH", id);
                return;
            }
            StringBuilder sb = new StringBuilder("[EFProbe] ").append(id);
            sb.append(" patch=").append(patch.getClass().getSimpleName());

            Object armature = getField(patch, "armature");
            sb.append(" armature=").append(armature == null ? "NULL" : armature.getClass().getSimpleName());
            if (armature != null) {
                Object root = getField(armature, "rootJoint");
                sb.append(" rootJoint=").append(root != null ? getField(root, "name") : "null");
            }

            Object animator = getField(patch, "animator");
            sb.append(" animator=").append(animator == null ? "NULL" : animator.getClass().getSimpleName());
            if (animator != null) {
                Object livingAnims = invoke(animator, "getLivingAnimations");
                if (livingAnims instanceof Map<?, ?> m) {
                    sb.append(" livingAnims=").append(m.size()).append(m.keySet());
                }
                sb.append(" currentMotion=").append(getField(animator, "currentMotion"));
                Object baseLayer = getField(animator, "baseLayer");
                if (baseLayer != null) {
                    Object player = getField(baseLayer, "animationPlayer");
                    if (player != null) {
                        Object realAnim = invoke(player, "getRealAnimation");
                        sb.append(" playing=").append(realAnim);
                        Object animObj = realAnim != null ? invoke(realAnim, "get") : null;
                        if (animObj != null) {
                            int joints = -1;
                            Object jc = invoke(animObj, "getJointCount");
                            if (jc instanceof Integer i) joints = i;
                            Object totalTime = invoke(animObj, "getTotalTime");
                            sb.append(" animClass=").append(animObj.getClass().getSimpleName())
                              .append(" joints=").append(joints).append(" time=").append(totalTime);
                        } else sb.append(" animObj=NULL");
                    }
                }
                // sample the actual pose the renderer would use
                try {
                    Object pose = animator.getClass().getMethod("getPose", float.class)
                            .invoke(animator, 0.5f);
                    Object data = pose.getClass().getMethod("getJointTransformData").invoke(pose);
                    if (data instanceof Map<?, ?> m) {
                        sb.append(" poseSize=").append(m.size());
                        int animated = 0;
                        StringBuilder animJoints = new StringBuilder();
                        for (Map.Entry<?, ?> en : m.entrySet()) {
                            Object jt = en.getValue();
                            Object rot = jt.getClass().getMethod("rotation").invoke(jt);
                            if (rot != null) {
                                double w = quatW(rot);
                                double mag = quatLen(rot);
                                if (Math.abs(1.0 - w) > 0.03 || mag > 1.05 || mag < 0.95) {
                                    animated++;
                                    animJoints.append(en.getKey()).append(',');
                                }
                            }
                        }
                        sb.append(" animatedJoints=").append(animated).append('[').append(animJoints).append(']');
                        // per-joint: pose local translation vs armature bind local translation
                        try {
                            for (String jn : new String[]{"Arm_R","Head"}) {
                                Object bindJ = invoke(armature, "searchJointByName", jn);
                                if (bindJ != null) {
                                    sb.append(' ').append(jn).append("{");
                                    for (Field f : getAllFields(bindJ.getClass())) {
                                        f.setAccessible(true);
                                        Object val = f.get(bindJ);
                                        if (val == null) continue;
                                        String cn = val.getClass().getSimpleName();
                                        if (cn.contains("Matrix")) sb.append(f.getName()).append('=').append(transStr(val)).append(' ');
                                        else if (cn.contains("JointTransform")) {
                                            Object t2 = invoke(val, "translation");
                                            sb.append(f.getName()).append(".t=").append(transStr(t2)).append(' ');
                                        }
                                    }
                                    sb.append('}');
                                }
                                Object poseJt = m.containsKey(jn) ? m.get(jn) : null;
                                if (poseJt != null) {
                                    sb.append(' ').append(jn).append("Pose{");
                                    for (Field f : getAllFields(poseJt.getClass())) {
                                        f.setAccessible(true);
                                        Object val = f.get(poseJt);
                                        if (val == null) continue;
                                        String cn = val.getClass().getSimpleName();
                                        if (cn.contains("Vec") || cn.contains("Vector") || cn.contains("Quaternion"))
                                            sb.append(f.getName()).append('=').append(transStr(val)).append(' ');
                                        else if (cn.contains("TransformEntry") || cn.contains("JointTransform") || cn.contains("Matrix"))
                                            sb.append(f.getName()).append('=').append(cn).append(' ').append(transStr(invoke(val, "toMatrix") instanceof String ? invoke(val,"translation") : invoke(val,"toMatrix"))).append(' ');
                                    }
                                    sb.append('}');
                                }
                            }
                        } catch (Throwable t3) { sb.append(" jErr=").append(t3.getMessage()); }
                    }
                } catch (Throwable t2) {
                    sb.append(" poseErr=").append(t2.getMessage());
                }
            }
            if (armature != null) {
                Object jc = invoke(armature, "getJointNumber");
                Object hasHead = invoke(armature, "hasJoint", "Head");
                sb.append(" joints=").append(jc).append(" hasHead=").append(hasHead);
                try {
                    Object[] mats = (Object[]) invoke(armature, "getPoseMatrices");
                    int posed = 0;
                    if (mats != null) for (Object m : mats) {
                        if (m == null) continue;
                        float m01 = matElem(m, 0, 1), m10 = matElem(m, 1, 0), m20 = matElem(m, 2, 0);
                        if (Math.abs(m01) > 0.01 || Math.abs(m10) > 0.01 || Math.abs(m20) > 0.01) posed++;
                    }
                    sb.append(" posedMats=").append(posed).append('/').append(mats == null ? -1 : mats.length);
                } catch (Throwable t2) { sb.append(" matsErr=").append(t2.getMessage()); }
            }
            sb.append(" currentLivingMotion=").append(getField(patch, "currentLivingMotion"));
            sb.append(" currentCompositeMotion=").append(getField(patch, "currentCompositeMotion"));

            // does the animation accessor resolve?
            Object accessor = Class.forName("yesman.epicfight.api.animation.AnimationManager")
                    .getMethod("byKey", String.class).invoke(null, "epicfight:biped/living/idle");
            Object anim = accessor != null ? accessor.getClass().getMethod("get").invoke(accessor) : null;
            sb.append(" idleAnim=").append(anim != null ? "OK" : "NULL");
            LOGGER.info(sb.toString());
        } catch (Throwable t) {
            LOGGER.warn("[EFProbe] {} probe failed", id, t);
        }
    }

    private static Object unwrap(Object o) throws Exception {
        if (o == null) return null;
        if (o instanceof java.util.Optional<?> opt) return opt.orElse(null);
        return o;
    }

    private static Object getField(Object o, String name) {
        try {
            Class<?> c = o.getClass();
            while (c != null) {
                try {
                    Field f = c.getDeclaredField(name);
                    f.setAccessible(true);
                    return f.get(o);
                } catch (NoSuchFieldException nf) {
                    c = c.getSuperclass();
                }
            }
            return null;
        } catch (Throwable t) {
            return "ERR:" + t.getClass().getSimpleName();
        }
    }

    private static java.util.List<Field> getAllFields(Class<?> c) {
        java.util.List<Field> out = new java.util.ArrayList<>();
        while (c != null) { out.addAll(java.util.Arrays.asList(c.getDeclaredFields())); c = c.getSuperclass(); }
        return out;
    }

    private static String transStr(Object mat) {
        if (mat == null || mat instanceof String) return String.valueOf(mat);
        try {
            // Vector3f-like: x/y/z accessors or public fields
            try {
                float x = (float) mat.getClass().getMethod("x").invoke(mat);
                float y = (float) mat.getClass().getMethod("y").invoke(mat);
                float z = (float) mat.getClass().getMethod("z").invoke(mat);
                return String.format("%.3f,%.3f,%.3f", x, y, z);
            } catch (Throwable ignored) {}
            try {
                float x = mat.getClass().getField("x").getFloat(mat);
                float y = mat.getClass().getField("y").getFloat(mat);
                float z = mat.getClass().getField("z").getFloat(mat);
                return String.format("%.3f,%.3f,%.3f", x, y, z);
            } catch (Throwable ignored) {}
            float r03 = matElem(mat, 0, 3), r13 = matElem(mat, 1, 3), r23 = matElem(mat, 2, 3);
            float r30 = matElem(mat, 3, 0), r31 = matElem(mat, 3, 1), r32 = matElem(mat, 3, 2);
            return String.format("c03=%.3f,%.3f,%.3f|c30=%.3f,%.3f,%.3f", r03, r13, r23, r30, r31, r32);
        } catch (Throwable t) { return "ERR"; }
    }

    private static float matElem(Object m, int row, int col) {
        try {
            // OpenMatrix4f fields: m00..m33
            Field f = m.getClass().getDeclaredField("m" + row + col);
            f.setAccessible(true);
            return f.getFloat(m);
        } catch (Throwable t) { return Float.NaN; }
    }

    private static double quatW(Object q) {
        try {
            for (String f : new String[]{"w", "field_4561"}) {
                try { Field fl = q.getClass().getDeclaredField(f); fl.setAccessible(true); return fl.getFloat(q); } catch (NoSuchFieldException ignored) {}
            }
            // Quaternionf interface: w() method
            return (float) q.getClass().getMethod("w").invoke(q);
        } catch (Throwable t) { return Double.NaN; }
    }

    private static double quatLen(Object q) {
        try {
            double w = quatW(q), x = comp(q, "x"), y = comp(q, "y"), z = comp(q, "z");
            return Math.sqrt(w * w + x * x + y * y + z * z);
        } catch (Throwable t) { return Double.NaN; }
    }

    private static double comp(Object q, String c) throws Exception {
        try { return (float) q.getClass().getMethod(c).invoke(q); }
        catch (NoSuchMethodException e) {
            Field f = q.getClass().getDeclaredField(c); f.setAccessible(true); return f.getFloat(q);
        }
    }

    private static Object invoke(Object o, String name, Object... args) {
        try {
            for (java.lang.reflect.Method m : o.getClass().getMethods()) {
                if (!m.getName().equals(name) || m.getParameterCount() != args.length) continue;
                m.setAccessible(true);
                return m.invoke(o, args);
            }
            return "ERR:NoMethod:" + name;
        } catch (Throwable t) {
            return "ERR:" + t.getClass().getSimpleName();
        }
    }
}
