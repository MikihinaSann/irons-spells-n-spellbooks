package issprobe;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

/** Test-only fake player so Player.actuallyHurt (which does NOT call
 * super.actuallyHurt) is exercised at runtime without an online user. */
public class ProbeFakePlayer extends ServerPlayer {
    public ProbeFakePlayer(MinecraftServer server, ServerLevel level) {
        super(server, level,
                new GameProfile(UUID.fromString("00000000-0000-0000-0000-00000000beef"), "iss_probe"),
                new ClientInformation("en_us", 0, ChatVisiblity.FULL, false, 0, HumanoidArm.RIGHT, false, false));
        this.connection = new ProbeConnection(server, this);
    }

}
