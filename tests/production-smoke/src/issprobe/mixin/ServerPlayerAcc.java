package issprobe.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAcc {
    @Accessor("field_13998") // spawnInvulnerableTime
    void iss_probe$setSpawnInvulnerableTime(int value);
}
