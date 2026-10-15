package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.entity.player.Player;

/**
 * NeoForge's {@code PlayerXpEvent} — base for XP-related player events.
 */
public abstract class PlayerXpEvent extends PlayerEvent {
    public PlayerXpEvent(Player player) {
        super(player);
    }

    /**
     * Fired from {@code Player.giveExperiencePoints} before XP is added.
     * The amount is mutable; NeoForge semantics apply.
     */
    public static class XpChange extends PlayerXpEvent {
        private int amount;

        public XpChange(Player player, int amount) {
            super(player);
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    /**
     * Fired when a player levels up ({@code Player.giveExperienceLevels}).
     */
    public static class LevelChange extends PlayerXpEvent {
        private int levels;

        public LevelChange(Player player, int levels) {
            super(player);
            this.levels = levels;
        }

        public int getLevels() {
            return levels;
        }

        public void setLevels(int levels) {
            this.levels = levels;
        }
    }

    /**
     * Fired when a player picks up an XP orb ({@code ExperienceOrb.playerTouch}).
     */
    public static class PickupXp extends PlayerXpEvent {
        private final net.minecraft.world.entity.ExperienceOrb orb;

        public PickupXp(Player player, net.minecraft.world.entity.ExperienceOrb orb) {
            super(player);
            this.orb = orb;
        }

        public net.minecraft.world.entity.ExperienceOrb getOrb() {
            return orb;
        }
    }
}
