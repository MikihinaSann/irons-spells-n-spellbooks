package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when an animal is tamed. Canceling prevents the tame. Ported from
 * NeoForge 21.1.
 */
public class AnimalTameEvent extends LivingEvent implements ICancellableEvent {
    private final Animal animal;
    private final Player tamer;

    public AnimalTameEvent(Animal animal, Player tamer) {
        super(animal);
        this.animal = animal;
        this.tamer = tamer;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Player getTamer() {
        return tamer;
    }
}
