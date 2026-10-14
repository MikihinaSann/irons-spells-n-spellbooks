package net.neoforged.neoforge.event.entity.player;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;

/**
 * Fired when a player earns or progresses an advancement. Ported from NeoForge
 * 21.1.
 */
public abstract class AdvancementEvent extends PlayerEvent {
    private final AdvancementHolder advancement;

    public AdvancementEvent(Player player, AdvancementHolder advancement) {
        super(player);
        this.advancement = advancement;
    }

    public AdvancementHolder getAdvancement() {
        return advancement;
    }

    /**
     * Fired when the player's progress on an advancement criterion is granted.
     */
    public static class AdvancementEarnEvent extends AdvancementEvent {
        public AdvancementEarnEvent(Player player, AdvancementHolder earned) {
            super(player, earned);
        }
    }

    /**
     * Fired when the player's progress on an advancement criterion is granted
     * or revoked.
     */
    public static class AdvancementProgressEvent extends AdvancementEvent {
        private final AdvancementProgress advancementProgress;
        private final String criterionName;
        private final ProgressType progressType;

        public AdvancementProgressEvent(Player player, AdvancementHolder progressed, AdvancementProgress advancementProgress,
                String criterionName, ProgressType progressType) {
            super(player, progressed);
            this.advancementProgress = advancementProgress;
            this.criterionName = criterionName;
            this.progressType = progressType;
        }

        /**
         * {@return the progress of the advancement}
         */
        public AdvancementProgress getAdvancementProgress() {
            return advancementProgress;
        }

        /**
         * {@return name of the criterion that was progressed}
         */
        public String getCriterionName() {
            return criterionName;
        }

        /**
         * {@return the type of progress made}
         */
        public ProgressType getProgressType() {
            return progressType;
        }

        public enum ProgressType {
            GRANT,
            REVOKE
        }
    }
}
