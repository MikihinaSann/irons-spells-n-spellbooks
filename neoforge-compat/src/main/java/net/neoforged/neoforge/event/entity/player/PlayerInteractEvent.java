package net.neoforged.neoforge.event.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerInteractEvent extends PlayerEvent implements ICancellableEvent {
    private final InteractionHand hand;
    private final BlockPos pos;
    @Nullable
    private final Direction face;
    private InteractionResult cancellationResult = InteractionResult.PASS;
    private Result useItem = Result.DEFAULT;
    private Result useBlock = Result.DEFAULT;

    public PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face) {
        super(player);
        this.hand = hand;
        this.pos = pos.immutable();
        this.face = face;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public Direction getFace() {
        return face;
    }

    public Level getLevel() {
        return getEntity().level();
    }

    public ItemStack getItemStack() {
        return getEntity().getItemInHand(hand);
    }

    public InteractionResult getCancellationResult() {
        return cancellationResult;
    }

    public void setCancellationResult(InteractionResult result) {
        this.cancellationResult = result;
    }

    public Result getUseBlock() {
        return useBlock;
    }

    public void setUseBlock(Result useBlock) {
        this.useBlock = useBlock;
    }

    public Result getUseItem() {
        return useItem;
    }

    public void setUseItem(Result useItem) {
        this.useItem = useItem;
    }

    /** Right-click on a block (or air via {@code RightClickItem}). */
    public static class RightClickBlock extends PlayerInteractEvent {
        private final BlockHitResult hitVec;
        private Result useBlockResult = Result.DEFAULT;

        public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
            super(player, hand, pos, hitVec.getDirection());
            this.hitVec = hitVec;
        }

        public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
            super(player, hand, pos, face);
            this.hitVec = new BlockHitResult(Vec3.atCenterOf(pos), face != null ? face : Direction.UP, pos, false);
        }

        public BlockHitResult getHitVec() {
            return hitVec;
        }

        public Result getUseBlock() {
            return useBlockResult;
        }

        public void setUseBlock(Result result) {
            this.useBlockResult = result;
        }
    }

    /** Right-click air / generic item use. */
    public static class RightClickItem extends PlayerInteractEvent {
        public RightClickItem(Player player, InteractionHand hand) {
            super(player, hand, player.blockPosition(), null);
        }

        public RightClickItem(Player player, InteractionHand hand, BlockPos pos) {
            super(player, hand, pos, null);
        }
    }

    /** Right-click air with empty offhand considerations (left click air). */
    public static class LeftClickEmpty extends PlayerInteractEvent {
        public LeftClickEmpty(Player player) {
            super(player, InteractionHand.MAIN_HAND, player.blockPosition(), null);
        }
    }

    /** Left-click block. */
    public static class LeftClickBlock extends PlayerInteractEvent {
        public LeftClickBlock(Player player, BlockPos pos, Direction face) {
            super(player, InteractionHand.MAIN_HAND, pos, face);
        }
    }

    /** Interact with an entity. */
    public static class EntityInteract extends PlayerInteractEvent {
        private final Entity target;

        public EntityInteract(Player player, InteractionHand hand, Entity target) {
            super(player, hand, target.blockPosition(), null);
            this.target = target;
        }

        public Entity getTarget() {
            return target;
        }
    }

    /** Interact with a specific part of an entity (multipart). */
    public static class EntityInteractSpecific extends EntityInteract {
        private final Vec3 localPos;

        public EntityInteractSpecific(Player player, InteractionHand hand, Entity target, Vec3 localPos) {
            super(player, hand, target);
            this.localPos = localPos;
        }

        public Vec3 getLocalPos() {
            return localPos;
        }
    }
}
