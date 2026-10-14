package dev.shadowsoffire.placebo.menu;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

/**
 * Regression for the pos-menu opening-data contract: the menu type's codec
 * must speak BlockPos, and the provider must hand that same BlockPos to the
 * opening packet — never a raw buffer.
 */
public class MenuOpenDataTest {

    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void posTypeCodecRoundTripsBlockPos() {
        var type = MenuUtil.posType((id, inv, pos) -> null);
        assertInstanceOf(
                net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType.class, type);
        @SuppressWarnings("unchecked")
        var ext = (net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType<?, BlockPos>) type;
        assertSame(BlockPos.STREAM_CODEC, ext.getPacketCodec(),
                "posType's packet codec must be BlockPos.STREAM_CODEC");
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        BlockPos pos = new BlockPos(12, 64, -40);
        ext.getPacketCodec().encode(buf, pos);
        assertEquals(pos, ext.getPacketCodec().decode(buf));
    }

    @Test
    void simplerMenuProviderOpeningDataIsBlockPos() throws Exception {
        // The ctor reads level.getBlockState(pos) for the title, so a real Level
        // is needed — skip it and set the pos field directly; the contract under
        // test is that getScreenOpeningData returns that BlockPos.
        BlockPos pos = new BlockPos(1, 2, 3);
        SimplerMenuProvider<?> provider = (SimplerMenuProvider<?>)
                theUnsafe().allocateInstance(SimplerMenuProvider.class);
        Field posField = SimplerMenuProvider.class.getDeclaredField("pos");
        posField.setAccessible(true);
        posField.set(provider, pos);
        Object data = provider.getScreenOpeningData(null);
        assertInstanceOf(BlockPos.class, data, "opening data must be a BlockPos, not a buffer");
        assertEquals(pos, data);
    }

    private static Unsafe theUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}
