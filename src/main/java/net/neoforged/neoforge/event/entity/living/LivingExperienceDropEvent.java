package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public class LivingExperienceDropEvent extends LivingEvent implements ICancellableEvent {
    private final Player attackingPlayer;
    private final int originalExperiencePoints;
    private int droppedExperiencePoints;

    public LivingExperienceDropEvent(LivingEntity entity, @Nullable Player attackingPlayer,
                                     int originalExperiencePoints, int droppedExperiencePoints) {
        super(entity);
        this.attackingPlayer = attackingPlayer;
        this.originalExperiencePoints = originalExperiencePoints;
        this.droppedExperiencePoints = droppedExperiencePoints;
    }

    @Nullable
    public Player getAttackingPlayer() {
        return attackingPlayer;
    }

    public int getOriginalExperience() {
        return originalExperiencePoints;
    }

    public int getDroppedExperience() {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int points) {
        this.droppedExperiencePoints = points;
    }
}
