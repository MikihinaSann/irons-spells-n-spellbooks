package it.crystalnest.prometheus.api.type;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface FireTypeSynched extends FireTypeChanger {
   EntityDataAccessor<String> fireTypeAccessor();
}
