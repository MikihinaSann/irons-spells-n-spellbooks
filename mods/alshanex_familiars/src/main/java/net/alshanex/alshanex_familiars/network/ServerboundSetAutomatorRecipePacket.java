package net.alshanex.alshanex_familiars.network;

import net.alshanex.alshanex_familiars.util.familiars.AlshanexFamiliarManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSetAutomatorRecipePacket(BlockPos pos, ResourceLocation recipeId) implements CustomPacketPayload {
   public static final Type<ServerboundSetAutomatorRecipePacket> TYPE = new Type(new ResourceLocation("alshanex_familiars", "set_automator_recipe"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetAutomatorRecipePacket> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      ServerboundSetAutomatorRecipePacket::pos,
      ResourceLocation.STREAM_CODEC,
      ServerboundSetAutomatorRecipePacket::recipeId,
      ServerboundSetAutomatorRecipePacket::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ServerboundSetAutomatorRecipePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> AlshanexFamiliarManager.onAutomationScreenSelection(context.player(), packet.pos(), packet.recipeId()));
   }
}
