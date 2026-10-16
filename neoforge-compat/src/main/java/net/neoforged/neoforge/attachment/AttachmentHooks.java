package net.neoforged.neoforge.attachment;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NeoForge lets mods mixin into {@code Entity.setData} for sync side-effects.
 * On this compat layer {@code setData} is an interface default method, which
 * cannot be targeted by mixins — ported mods register a listener here instead.
 */
public final class AttachmentHooks {

    public interface SetListener {
        void onSet(IAttachmentHolder holder, AttachmentType<?> type, Object data);
    }

    private static final List<SetListener> LISTENERS = new CopyOnWriteArrayList<>();

    private AttachmentHooks() {
    }

    public static void register(SetListener listener) {
        LISTENERS.add(listener);
    }

    public static void fireSet(IAttachmentHolder holder, AttachmentType<?> type, Object data) {
        for (SetListener listener : LISTENERS) {
            listener.onSet(holder, type, data);
        }
    }
}
