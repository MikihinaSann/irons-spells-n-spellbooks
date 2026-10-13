package net.neoforged.bus.api;

/**
 * Minimal stand-in for NeoForge's Event base class.
 */
public class Event {
    private boolean canceled = false;
    private Result result = Result.DEFAULT;
    private EventPriority phase = null;

    public boolean isCancelable() {
        return this instanceof ICancellableEvent;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean cancel) {
        if (isCancelable()) {
            canceled = cancel;
        }
    }

    public boolean hasResult() {
        return true;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result value) {
        this.result = value;
    }

    public EventPriority getPhase() {
        return phase;
    }

    public void setPhase(EventPriority value) {
        this.phase = value;
    }

    public enum Result {
        DENY, DEFAULT, ALLOW
    }
}
