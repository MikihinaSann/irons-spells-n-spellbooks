package net.neoforged.neoforge.event.entity.living;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Fired when an entity's armor takes durability damage (NeoForge's
 * {@code doHurtEquipment} hook). Cancel to skip all armor damage, or adjust
 * per-slot {@link ArmorEntry#newDamage}.
 */
public class ArmorHurtEvent extends LivingEvent implements ICancellableEvent {
    private final EnumMap<EquipmentSlot, ArmorEntry> armorEntries;

    @Internal
    public ArmorHurtEvent(EnumMap<EquipmentSlot, ArmorEntry> armorMap, LivingEntity player) {
        super(player);
        this.armorEntries = armorMap;
    }

    public ItemStack getArmorItemStack(EquipmentSlot slot) {
        return this.armorEntries.containsKey(slot) ? this.armorEntries.get(slot).armorItemStack : ItemStack.EMPTY;
    }

    public Float getOriginalDamage(EquipmentSlot slot) {
        return this.armorEntries.containsKey(slot) ? this.armorEntries.get(slot).originalDamage : 0.0F;
    }

    public Float getNewDamage(EquipmentSlot slot) {
        return this.armorEntries.containsKey(slot) ? this.armorEntries.get(slot).newDamage : 0.0F;
    }

    public void setNewDamage(EquipmentSlot slot, float damage) {
        if (this.armorEntries.containsKey(slot)) {
            this.armorEntries.get(slot).newDamage = damage;
        }
    }

    public Map<EquipmentSlot, ArmorEntry> getArmorMap() {
        return this.armorEntries;
    }

    public static class ArmorEntry {
        public ItemStack armorItemStack;
        public final float originalDamage;
        public float newDamage;

        public ArmorEntry(ItemStack armorStack, float damageIn) {
            this.armorItemStack = armorStack;
            this.originalDamage = damageIn;
            this.newDamage = damageIn;
        }
    }
}
