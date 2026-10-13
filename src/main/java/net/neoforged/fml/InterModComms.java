package net.neoforged.fml;

import java.util.function.Supplier;

public class InterModComms {
    public record IMCMessage(String senderModId, String modId, String method, Supplier<Object> messageSupplier) {
        public Supplier<Object> messageSupplier() {
            return messageSupplier;
        }
    }
}
