package it.crystalnest.cobweb.api.pack.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class DynamicDataPack extends DynamicResourcePack {
   private final List<Supplier<DynamicTagBuilder<?>>> tagBuilders = new ArrayList<>();

   private DynamicDataPack(ResourceLocation name) {
      super(name, PackType.SERVER_DATA);
   }

   public static DynamicDataPack named(ResourceLocation name) {
      return new DynamicDataPack(name);
   }

   public DynamicResourcePack add(DynamicTagBuilder<?>... builders) {
      for (DynamicTagBuilder<?> builder : builders) {
         this.tagBuilders.add(() -> builder);
      }

      return this;
   }

   @SafeVarargs
   public final DynamicResourcePack add(Supplier<DynamicTagBuilder<?>>... builders) {
      this.tagBuilders.addAll(Arrays.asList(builders));
      return this;
   }

   @Override
   protected void build() {
      for (DynamicTagBuilder<?> builder : this.tagBuilders.stream().map(Supplier::get).toList()) {
         this.build(builder.getPaths(), builder::json);
      }
   }
}
