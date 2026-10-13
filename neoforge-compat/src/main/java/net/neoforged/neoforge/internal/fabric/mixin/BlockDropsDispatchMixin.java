package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fires {@link BlockDropsEvent} from {@code Block#dropResources}. The event
 * receives the to-be-spawned {@link ItemEntity} drops so subscribers can modify
 * or cancel them; cancellation suppresses the vanilla drop spawn entirely.
 */
@Mixin(Block.class)
public abstract class BlockDropsDispatchMixin {

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"), cancellable = true)
    private static void irons_spellbooks$blockDrops(BlockState state, Level level, BlockPos pos,
                                                    @Nullable BlockEntity blockEntity, @Nullable Entity entity,
                                                    ItemStack tool, CallbackInfo ci) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        List<ItemEntity> drops = new ArrayList<>();
        for (ItemStack stack : Block.getDrops(state, serverLevel, pos, blockEntity, entity, tool)) {
            drops.add(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
        }
        boolean silk = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.SILK_TOUCH), tool) > 0;
        var event = new BlockDropsEvent(level, pos, state, blockEntity, entity, tool, drops, silk, 0);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            for (ItemEntity drop : event.getDrops()) {
                level.addFreshEntity(drop);
            }
            int xp = event.getDroppedExperience();
            if (xp > 0) {
                net.minecraft.world.entity.ExperienceOrb.award(serverLevel,
                        net.minecraft.world.phys.Vec3.atCenterOf(pos), xp);
            }
        }
        ci.cancel();
    }
}
