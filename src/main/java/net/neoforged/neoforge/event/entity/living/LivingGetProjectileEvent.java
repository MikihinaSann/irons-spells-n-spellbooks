package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LivingGetProjectileEvent extends LivingEvent {
    private final ItemStack projectileWeaponItemStack;
    private final ItemStack projectileItemStack;
    private final ItemStack ammoItemStack;

    public LivingGetProjectileEvent(LivingEntity entity, ItemStack projectileWeaponItemStack, ItemStack ammoItemStack, ItemStack projectileItemStack) {
        super(entity);
        this.projectileWeaponItemStack = projectileWeaponItemStack;
        this.ammoItemStack = ammoItemStack;
        this.projectileItemStack = projectileItemStack;
    }

    public ItemStack getProjectileWeaponItemStack() {
        return projectileWeaponItemStack;
    }

    public ItemStack getProjectileItemStack() {
        return projectileItemStack;
    }

    public ItemStack getAmmoItemStack() {
        return ammoItemStack;
    }
}
