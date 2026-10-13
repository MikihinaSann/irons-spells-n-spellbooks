package net.neoforged.neoforge.server.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Enum argument type — NeoForge's command helper for enum-typed arguments.
 */
public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
    private static final DynamicCommandExceptionType INVALID_ENUM = new DynamicCommandExceptionType(
            obj -> Component.translatableEscape("argument.enum.invalid", obj));

    private final Class<T> enumClass;

    protected EnumArgument(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public static <T extends Enum<T>> EnumArgument<T> enumArgument(Class<T> enumClass) {
        return new EnumArgument<>(enumClass);
    }

    public static <T extends Enum<T>> T getEnum(CommandContext<?> context, String name, Class<T> enumClass) {
        return context.getArgument(name, enumClass);
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            for (T constant : enumClass.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(name)) {
                    return constant;
                }
            }
            throw INVALID_ENUM.createWithContext(reader, name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList()),
                builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
    }

    /**
     * Serializer registered through Fabric's ArgumentTypeRegistry so the arg
     * type survives command-tree sync (LAN, dedicated server join).
     */
    public static class Info implements ArgumentTypeInfo<EnumArgument<?>, Info.Template> {
        @Override
        public void serializeToJson(Template template, JsonObject json) {
            json.addProperty("enum", template.enumClass.getName());
        }

        @Override
        public void serializeToNetwork(Template template, FriendlyByteBuf buf) {
            buf.writeUtf(template.enumClass.getName());
        }

        @Override
        public Template deserializeFromNetwork(FriendlyByteBuf buf) {
            return new Template(classForName(buf.readUtf()));
        }

        @Override
        public Template unpack(EnumArgument<?> type) {
            return new Template(type.enumClass);
        }

        @SuppressWarnings("unchecked")
        private static Class<? extends Enum<?>> classForName(String name) {
            try {
                return (Class<? extends Enum<?>>) Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unknown enum class: " + name, e);
            }
        }

        public final class Template implements ArgumentTypeInfo.Template<EnumArgument<?>> {
            private final Class<? extends Enum<?>> enumClass;

            Template(Class<? extends Enum<?>> enumClass) {
                this.enumClass = enumClass;
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public EnumArgument<?> instantiate(CommandBuildContext context) {
                return new EnumArgument(enumClass);
            }

            @Override
            public ArgumentTypeInfo<EnumArgument<?>, ?> type() {
                return Info.this;
            }
        }
    }
}
