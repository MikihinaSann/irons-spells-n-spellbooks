package top.theillusivec4.curios.api.type.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

/**
 * Per-stack curio capability object, Curios-style. Default methods no-op.
 */
public interface ICurio {

    default ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    default SlotContext getSlotContext() {
        return null;
    }

    default void curioTick(SlotContext slotContext) {
    }

    default void onEquip(SlotContext slotContext, ItemStack prevStack) {
    }

    default void onUnequip(SlotContext slotContext) {
    }

    default boolean canEquip(SlotContext slotContext) {
        return true;
    }

    default boolean canUnequip(SlotContext slotContext) {
        return true;
    }

    default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                               ResourceLocation id) {
        return HashMultimap.create();
    }

    default boolean canEquipFromUse(SlotContext slotContext) {
        return false;
    }

    default void onEquipFromUse(SlotContext slotContext) {
        playEquipSound(slotContext);
    }

    default void playEquipSound(SlotContext slotContext) {
        SoundInfo sound = getEquipSound(slotContext);
        if (sound != null && slotContext.entity() != null) {
            slotContext.entity().level().playSound(null, slotContext.entity().blockPosition(),
                    sound.soundEvent(), net.minecraft.sounds.SoundSource.NEUTRAL,
                    sound.volume(), sound.pitch());
        }
    }

    @Nullable
    default SoundInfo getEquipSound(SlotContext slotContext) {
        return null;
    }

    default boolean canSync(SlotContext slotContext) {
        return false;
    }

    default boolean isEnderMask(SlotContext slotContext, Player player) {
        return false;
    }

    default boolean makesPiglinsNeutral(SlotContext slotContext) {
        return false;
    }

    default boolean canWalkOnPowderedSnow(SlotContext slotContext) {
        return false;
    }

    default boolean canFireworkBoost(SlotContext slotContext) {
        return true;
    }

    default void curioBreak(SlotContext slotContext) {
        if (slotContext.entity() != null) {
            slotContext.entity().level().broadcastEntityEvent(slotContext.entity(), (byte) 47);
        }
    }

    default void onEquipTick(SlotContext slotContext) {
        curioTick(slotContext);
    }

    default DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
        return DropRule.DEFAULT;
    }

    default void modifyAttributesTooltip(java.util.List<net.minecraft.network.chat.Component> tooltips,
                                         net.minecraft.world.item.Item.TooltipContext tooltipContext,
                                         Player player) {
    }

    record SoundInfo(SoundEvent soundEvent, float volume, float pitch) {
    }

    enum DropRule {
        DEFAULT, ALWAYS_DROP, ALWAYS_KEEP, DESTROY
    }
}
