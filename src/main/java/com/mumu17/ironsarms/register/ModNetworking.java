package com.mumu17.ironsarms.register;

import com.mumu17.ironsarms.network.RequestSyncChargedManaMessage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, ModNetworking::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("ironsarms").versioned("1.0").optional();
        registrar.playToServer(RequestSyncChargedManaMessage.TYPE, RequestSyncChargedManaMessage.STREAM_CODEC, RequestSyncChargedManaMessage::handle);
    }
}
