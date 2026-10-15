package it.crystalnest.prometheus.platform.services;

import it.crystalnest.prometheus.api.Fire;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface NetworkHelper {
   void register();

   void sendToClient(@Nullable ServerPlayer var1, Fire var2);

   void sendToClient(@Nullable ServerPlayer var1, ResourceLocation var2);
}
