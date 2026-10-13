package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge routes {@code ClientboundBlockEntityDataPacket} through
 * {@link IBlockEntityExtension#onDataPacket}; vanilla only applies data
 * components. When the block entity overrides the hook, dispatch to it and
 * consume the packet.
 */
@Mixin(ClientPacketListener.class)
public abstract class BlockEntityDataPacketMixin {
    /** Whether a block entity class declares its own {@code onDataPacket}. */
    private static final ClassValue<Boolean> OVERRIDES_ON_DATA_PACKET = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                return type.getMethod("onDataPacket", Connection.class, ClientboundBlockEntityDataPacket.class,
                        HolderLookup.Provider.class).getDeclaringClass() != IBlockEntityExtension.class;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
    };

    @Inject(method = "handleBlockEntityData", cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
                    shift = At.Shift.AFTER))
    private void irons_spellbooks$dispatchOnDataPacket(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(packet.getPos());
        if (blockEntity != null && blockEntity.getType() == packet.getType()
                && OVERRIDES_ON_DATA_PACKET.get(blockEntity.getClass())) {
            ClientPacketListener self = (ClientPacketListener) (Object) this;
            blockEntity.onDataPacket(self.getConnection(), packet, self.registryAccess());
            ci.cancel();
        }
    }
}
