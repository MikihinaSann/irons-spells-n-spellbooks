package top.theillusivec4.curios.api.type.capability;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

/**
 * Trinkets {@link Trinket} adapter registered for items implementing
 * {@link ICurioItem}, translating the Trinkets callbacks into Curios-style item
 * calls and posting the Curios events onto the mod event bus.
 */
public class TrinketCurioAdapter implements Trinket {
    private final ICurioItem curioItem;

    public TrinketCurioAdapter(ICurioItem curioItem) {
        this.curioItem = curioItem;
    }

    private static SlotContext ctx(SlotReference ref, LivingEntity entity) {
        return new SlotContext(ref.inventory().getSlotType().getName(), entity, ref.index(), false, true);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        curioItem.curioTick(ctx(slot, entity), stack);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                new CurioChangeEvent(entity, ctx(slot, entity), ItemStack.EMPTY, stack));
        curioItem.onEquip(ctx(slot, entity), ItemStack.EMPTY, stack);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                new CurioChangeEvent(entity, ctx(slot, entity), stack, ItemStack.EMPTY));
        curioItem.onUnequip(ctx(slot, entity), stack);
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var context = ctx(slot, entity);
        if (!top.theillusivec4.curios.mixin.CuriosImplMixinHooks.isStackValid(context, stack)) {
            return false;
        }
        return curioItem.canEquip(context, stack);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return curioItem.canUnequip(ctx(slot, entity), stack);
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent> getEquipSound(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var sound = curioItem.getEquipSound(ctx(slot, entity), stack);
        return sound != null ? Holder.direct(sound.soundEvent()) : Trinket.super.getEquipSound(stack, slot, entity);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
                                                                      LivingEntity entity, ResourceLocation id) {
        var context = ctx(slot, entity);
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = curioItem.getAttributeModifiers(context, id, stack);
        CurioAttributeModifierEvent event = new CurioAttributeModifierEvent(stack, context, id, modifiers);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference slot, LivingEntity entity) {
        curioItem.curioBreak(ctx(slot, entity), stack);
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var rule = curioItem.getDropRule(ctx(slot, entity), null, 0, false, stack);
        return switch (rule) {
            case ALWAYS_DROP -> TrinketEnums.DropRule.DROP;
            case ALWAYS_KEEP -> TrinketEnums.DropRule.KEEP;
            case DESTROY -> TrinketEnums.DropRule.DESTROY;
            default -> TrinketEnums.DropRule.DEFAULT;
        };
    }
}
