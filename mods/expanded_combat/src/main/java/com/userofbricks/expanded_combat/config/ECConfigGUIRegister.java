package com.userofbricks.expanded_combat.config;

import com.userofbricks.expanded_combat.config.gui.ConfigEntryGui;
import com.userofbricks.expanded_combat.config.gui.ResourceLocationFieldBuilder;
import com.userofbricks.expanded_combat.config.gui.ResourceLocationListBuilder;
import com.userofbricks.expanded_combat.init.PluginInit;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@OnlyIn(Dist.CLIENT)
public class ECConfigGUIRegister {
   private static final GuiRegistry registry = AutoConfig.getGuiRegistry(ECConfig.class);
   private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
   private static final Function<ResourceLocation, Component> DEFAULT_NAME_PROVIDER = t -> Component.literal(t.toString());
   private static final Component resetButtonKey = Component.translatable("text.cloth-config.reset_value");

   public static void registerModsPage() {
      registry.registerPredicateProvider(
         (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
            new ResourceLocationFieldBuilder(resetButtonKey, Component.translatable(i18n), (ResourceLocation)Utils.getUnsafely(field, config))
               .setAvailableValues(() -> BuiltInRegistries.SOUND_EVENT.keySet().stream().toList())
               .setDefaultValue((Supplier<ResourceLocation>)(() -> (ResourceLocation)Utils.getUnsafely(field, defaults)))
               .setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue))
               .setErrorSupplier(resourceLocation -> Optional.empty())
               .build()
         ),
         field -> field.getType() == ResourceLocation.class && field.isAnnotationPresent(ConfigEntryGui.SoundEvent.class)
      );
      registry.registerPredicateProvider(
         (i18n, field, config, defaults, giuProvider) -> Collections.singletonList(
            new ResourceLocationListBuilder(resetButtonKey, Component.translatable(i18n), (List<ResourceLocation>)Utils.getUnsafely(field, config))
               .setDefaultValue(() -> (List<ResourceLocation>)Utils.getUnsafely(field, defaults))
               .setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue))
               .setAvailableValues(() -> PluginInit.materials.keySet().stream().toList())
               .build()
         ),
         field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
               Type[] args = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
               return args.length == 1
                  && Stream.of(ResourceLocation.class).anyMatch(type -> Objects.equals(args[0], type))
                  && field.isAnnotationPresent(ConfigEntryGui.Material.class);
            } else {
               return false;
            }
         }
      );
      ModLoadingContext.get()
         .registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> (Screen)AutoConfig.getConfigScreen(ECConfig.class, parent).get());
   }
}
