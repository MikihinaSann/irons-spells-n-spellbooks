package io.redspace.ironsspellbooks.mixin.fabric;

import io.redspace.ironsspellbooks.platform.fabric.DeathDropCapture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * Dispatches NeoForge-style living-entity events from vanilla call sites that
 * no longer fire them on Fabric. Mirrors NeoForge's hook placement: incoming
 * damage at the head of {@code hurt}, damage Pre/Post inside
 * {@code actuallyHurt}, death in {@code die}, drops around
 * {@code dropAllDeathLoot}, experience at the {@code ExperienceOrb.award} call,
 * equipment changes from {@code collectEquipmentChanges}, plus heal, knockback
 * and item-use lifecycle events.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEventDispatchMixin {

    @Shadow
    protected ItemStack useItem;

    @Shadow
    protected int useItemRemaining;

    @Shadow
    protected int lastHurtByPlayerTime;

    @Shadow
    private ItemStack lastBodyItemStack;

    @Shadow
    abstract ItemStack getLastHandItem(EquipmentSlot slot);

    @Shadow
    abstract ItemStack getLastArmorItem(EquipmentSlot slot);

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Unique
    private LivingIncomingDamageEvent irons_spellbooks$incomingEvent;
    @Unique
    private LivingDamageEvent.Pre irons_spellbooks$damagePreEvent;
    @Unique
    private LivingEntityUseItemEvent.Start irons_spellbooks$useStartEvent;
    @Unique
    private ItemStack irons_spellbooks$finishingItem;
    @Unique
    private Map<EquipmentSlot, ItemStack> irons_spellbooks$lastEquipmentSnapshot;

    // ---- LivingIncomingDamageEvent: fired at the head of hurt (before invulnerability checks,
    // matching NeoForge's onEntityIncomingDamage placement). Declared before the
    // ModifyVariable below so the stored event is visible to it. ----
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onIncomingDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        irons_spellbooks$incomingEvent = null;
        LivingEntity self = (LivingEntity) (Object) this;
        LivingIncomingDamageEvent event = new LivingIncomingDamageEvent(self, source, amount);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
            return;
        }
        irons_spellbooks$incomingEvent = event;
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float irons_spellbooks$adjustIncomingAmount(float amount) {
        LivingIncomingDamageEvent event = irons_spellbooks$incomingEvent;
        return event == null ? amount : event.getAmount();
    }

    // ---- LivingDamageEvent.Pre / Post: wrapped around actuallyHurt. ----
    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onDamagePre(DamageSource source, float amount, CallbackInfo ci) {
        irons_spellbooks$damagePreEvent = null;
        LivingEntity self = (LivingEntity) (Object) this;
        LivingDamageEvent.Pre event = new LivingDamageEvent.Pre(self, source, amount);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        irons_spellbooks$damagePreEvent = event;
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float irons_spellbooks$adjustDamageAmount(float amount) {
        LivingDamageEvent.Pre event = irons_spellbooks$damagePreEvent;
        return event == null ? amount : event.getNewDamage();
    }

    @Inject(method = "actuallyHurt", at = @At("RETURN"))
    private void irons_spellbooks$onDamagePost(DamageSource source, float amount, CallbackInfo ci) {
        LivingDamageEvent.Pre pre = irons_spellbooks$damagePreEvent;
        irons_spellbooks$damagePreEvent = null;
        if (pre != null) {
            LivingEntity self = (LivingEntity) (Object) this;
            NeoForge.EVENT_BUS.post(new LivingDamageEvent.Post(self, source,
                    pre.getOriginalDamage(), pre.getNewDamage(), 0.0F, 0.0F));
        }
    }

    // ---- LivingDeathEvent. ----
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onDeath(DamageSource source, CallbackInfo ci) {
        LivingDeathEvent event = new LivingDeathEvent((LivingEntity) (Object) this, source);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    // ---- LivingDropsEvent: capture ItemEntities spawned during dropAllDeathLoot. ----
    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void irons_spellbooks$beginDropCapture(net.minecraft.server.level.ServerLevel level, DamageSource source, CallbackInfo ci) {
        DeathDropCapture.begin((LivingEntity) (Object) this);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
    private void irons_spellbooks$finishDropCapture(net.minecraft.server.level.ServerLevel level, DamageSource source, CallbackInfo ci) {
        DeathDropCapture.Capture capture = DeathDropCapture.end();
        if (capture == null) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        java.util.List<ItemEntity> spawned = new ArrayList<>(capture.drops());
        LivingDropsEvent event = new LivingDropsEvent(self, source, capture.drops(), 0, this.lastHurtByPlayerTime > 0);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            for (ItemEntity drop : spawned) {
                drop.discard();
            }
        } else {
            for (ItemEntity drop : spawned) {
                if (!capture.drops().contains(drop)) {
                    drop.discard();
                }
            }
            for (ItemEntity drop : capture.drops()) {
                if (!spawned.contains(drop) && !drop.isRemoved()) {
                    self.level().addFreshEntity(drop);
                }
            }
        }
    }

    // ---- LivingExperienceDropEvent: intercept the award amount. ----
    @ModifyArg(
            method = "dropExperience",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"),
            index = 2
    )
    private int irons_spellbooks$experienceDropped(int xp) {
        LivingEntity self = (LivingEntity) (Object) this;
        // The attacker entity is dropExperience's only parameter; it is on the
        // stack but ModifyArg cannot see it, so recover it from the damage record.
        Entity attacker = self.getLastDamageSource() != null ? self.getLastDamageSource().getEntity() : null;
        Player player = attacker instanceof Player p ? p : null;
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(self, player, xp, xp);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? 0 : event.getDroppedExperience();
    }

    // ---- LivingHealEvent. ----
    @ModifyVariable(method = "heal", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float irons_spellbooks$onHeal(float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) {
            return amount;
        }
        LivingHealEvent event = new LivingHealEvent(self, amount);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }

    // ---- LivingKnockBackEvent (cancel support only). ----
    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingKnockBackEvent event = new LivingKnockBackEvent(self, (float) strength, x, z);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    // ---- LivingEquipmentChangeEvent: snapshot last-known stacks before the
    // tracking arrays update, then diff against the change map. ----
    @Inject(method = "collectEquipmentChanges", at = @At("HEAD"))
    private void irons_spellbooks$snapshotEquipment(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        Map<EquipmentSlot, ItemStack> snapshot = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack old = switch (slot.getType()) {
                case HAND -> getLastHandItem(slot);
                case HUMANOID_ARMOR -> getLastArmorItem(slot);
                default -> this.lastBodyItemStack;
            };
            snapshot.put(slot, old);
        }
        irons_spellbooks$lastEquipmentSnapshot = snapshot;
    }

    @Inject(method = "collectEquipmentChanges", at = @At("RETURN"))
    private void irons_spellbooks$fireEquipmentChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        Map<EquipmentSlot, ItemStack> snapshot = irons_spellbooks$lastEquipmentSnapshot;
        irons_spellbooks$lastEquipmentSnapshot = null;
        Map<EquipmentSlot, ItemStack> changes = cir.getReturnValue();
        if (snapshot == null || changes == null || changes.isEmpty()) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        for (Map.Entry<EquipmentSlot, ItemStack> entry : changes.entrySet()) {
            ItemStack old = snapshot.getOrDefault(entry.getKey(), ItemStack.EMPTY);
            NeoForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(self, entry.getKey(), old, entry.getValue()));
        }
    }

    // ---- LivingEntityUseItemEvent lifecycle. ----
    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$useItemStart(InteractionHand hand, CallbackInfo ci) {
        irons_spellbooks$useStartEvent = null;
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack stack = getItemInHand(hand);
        LivingEntityUseItemEvent.Start event = new LivingEntityUseItemEvent.Start(self, stack, stack.getUseDuration(self));
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        irons_spellbooks$useStartEvent = event;
    }

    @Inject(method = "startUsingItem", at = @At("RETURN"))
    private void irons_spellbooks$useItemStartTail(InteractionHand hand, CallbackInfo ci) {
        LivingEntityUseItemEvent.Start event = irons_spellbooks$useStartEvent;
        irons_spellbooks$useStartEvent = null;
        if (event != null) {
            this.useItemRemaining = event.getDuration();
        }
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$useItemTick(ItemStack stack, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingEntityUseItemEvent.Tick event = new LivingEntityUseItemEvent.Tick(self, stack, this.useItemRemaining);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            self.releaseUsingItem();
            ci.cancel();
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void irons_spellbooks$useItemFinishHead(CallbackInfo ci) {
        irons_spellbooks$finishingItem = this.useItem;
    }

    @Inject(method = "completeUsingItem", at = @At("RETURN"))
    private void irons_spellbooks$useItemFinish(CallbackInfo ci) {
        ItemStack original = irons_spellbooks$finishingItem;
        irons_spellbooks$finishingItem = null;
        LivingEntity self = (LivingEntity) (Object) this;
        int usedDuration = original == null ? 0 : original.getUseDuration(self) - this.useItemRemaining;
        NeoForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Finish(self,
                original == null ? ItemStack.EMPTY : original, Math.max(usedDuration, 0), this.useItem));
    }

    @Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$useItemStop(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingEntityUseItemEvent.Stop event = new LivingEntityUseItemEvent.Stop(self, this.useItem, this.useItemRemaining);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
