package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.command.SpellArgument;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.neoforged.bus.api.IEventBus;


public class CommandArgumentRegistry {
    public static void register(IEventBus modEventBus) {
        net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(
                IronsSpellbooks.id("spell"), SpellArgument.class, SingletonArgumentInfo.contextFree(SpellArgument::spellArgument));
    }
}
