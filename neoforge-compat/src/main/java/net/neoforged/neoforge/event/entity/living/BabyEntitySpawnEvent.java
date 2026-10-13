package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public class BabyEntitySpawnEvent extends LivingEvent implements ICancellableEvent {
    private final Mob parentA;
    private final Mob parentB;
    private final AgeableMob proposedChild;
    @Nullable
    private final net.minecraft.world.entity.player.Player causedByPlayer;
    private AgeableMob child;

    public BabyEntitySpawnEvent(Mob parentA, Mob parentB, @Nullable AgeableMob proposedChild) {
        super(parentA);
        this.parentA = parentA;
        this.parentB = parentB;
        this.proposedChild = proposedChild;
        this.child = proposedChild;
        this.causedByPlayer = null;
    }

    public Mob getParentA() {
        return parentA;
    }

    public Mob getParentB() {
        return parentB;
    }

    @Nullable
    public AgeableMob getProposedChild() {
        return proposedChild;
    }

    @Nullable
    public net.minecraft.world.entity.player.Player getCausedByPlayer() {
        return causedByPlayer;
    }

    public void setChild(AgeableMob child) {
        this.child = child;
    }

    @Nullable
    public AgeableMob getChild() {
        return child;
    }
}
