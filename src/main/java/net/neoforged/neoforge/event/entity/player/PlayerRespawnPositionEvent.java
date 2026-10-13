package net.neoforged.neoforge.event.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlayerRespawnPositionEvent extends Event {
    private final Player player;
    private final Level level;
    private Optional<BlockPos> respawnPos;
    private ResourceKey<Level> dimension;
    private DimensionTransition dimensionTransition;
    private final boolean fromEndFight;

    public PlayerRespawnPositionEvent(Player player, Level level, @Nullable BlockPos respawnPos, ResourceKey<Level> dimension) {
        this.player = player;
        this.level = level;
        this.respawnPos = Optional.ofNullable(respawnPos);
        this.dimension = dimension;
        this.dimensionTransition = null;
        this.fromEndFight = false;
    }

    public PlayerRespawnPositionEvent(Player player, DimensionTransition transition, boolean fromEndFight) {
        this.player = player;
        this.level = player.level();
        this.respawnPos = Optional.empty();
        this.dimension = transition != null ? transition.newLevel().dimension() : null;
        this.dimensionTransition = transition;
        this.fromEndFight = fromEndFight;
    }

    public Player getEntity() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public Optional<BlockPos> getOriginalRespawnPos() {
        return respawnPos;
    }

    public Optional<BlockPos> getRespawnPos() {
        return respawnPos;
    }

    public void setRespawnPos(Optional<BlockPos> pos) {
        this.respawnPos = pos;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public DimensionTransition getDimensionTransition() {
        return dimensionTransition;
    }

    public void setDimensionTransition(DimensionTransition dimensionTransition) {
        this.dimensionTransition = dimensionTransition;
    }

    public boolean isFromEndFight() {
        return fromEndFight;
    }
}
