package it.crystalnest.cobweb.api.pack.dynamic;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import it.crystalnest.cobweb.Constants;
import it.crystalnest.cobweb.platform.Services;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicResourcePack implements PackResources {
   private final PackType type;
   private final PackLocationInfo location;
   private final ResourceLocation name;
   private final String namespace;
   private final Set<String> namespaces = new HashSet<>();
   private final Supplier<PackMetadataSection> metadata;
   private final Map<ResourceLocation, Supplier<byte[]>> resources = new ConcurrentHashMap<>();

   protected DynamicResourcePack(ResourceLocation name, PackType type) {
      this.location = new PackLocationInfo(name.toString(), Component.translatable(name.toString()), PackSource.BUILT_IN, Optional.empty());
      this.type = type;
      this.name = name;
      this.namespace = name.getNamespace();
      this.namespaces.add(this.namespace);
      this.metadata = Suppliers.memoize(
         () -> new PackMetadataSection(
            Component.translatable(this.namespace + "_dynamic_" + name.getPath()), SharedConstants.getCurrentVersion().getPackVersion(type), Optional.empty()
         )
      );
   }

   public void register() {
      Services.REGISTRY
         .registerDynamicResourcePack(
            this.type,
            Suppliers.memoize(
               () -> {
                  this.build();
                  return new Pack(
                     this.location(),
                     new DynamicResourcePack.DynamicResourcesSupplier(this),
                     Services.PLATFORM.createPackMetadata(this.metadata.get().description()),
                     new PackSelectionConfig(true, Position.TOP, false)
                  );
               }
            )
         );
   }

   @Override
   public String toString() {
      return this.packId();
   }

   @NotNull
   public PackLocationInfo location() {
      return this.location;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(@NotNull String... strings) {
      return null;
   }

   public IoSupplier<InputStream> getResource(@NotNull PackType type, @NotNull ResourceLocation id) {
      return this.resources.containsKey(id) ? () -> {
         if (this.type == type) {
            return new ByteArrayInputStream(this.resources.get(id).get());
         } else {
            throw new IOException(String.format("Tried to access wrong type of resource on %s.", this.name));
         }
      } : null;
   }

   public void listResources(@NotNull PackType type, @NotNull String namespace, @NotNull String id, @NotNull ResourceOutput output) {
      if (this.type == type && this.namespaces.contains(namespace)) {
         this.resources
            .entrySet()
            .stream()
            .filter(resource -> resource.getKey().getNamespace().equals(namespace) && resource.getKey().getPath().startsWith(id))
            .forEach(resource -> output.accept(resource.getKey(), (IoSupplier)() -> new ByteArrayInputStream((byte[])((Supplier)resource.getValue()).get())));
      }
   }

   @NotNull
   public Set<String> getNamespaces(@NotNull PackType packType) {
      return this.namespaces;
   }

   public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
      return (T)(serializer.getMetadataSectionName().equals(PackMetadataSection.TYPE.getMetadataSectionName()) ? this.metadata : null);
   }

   public void close() {
   }

   private void build(ResourceLocation path, Supplier<byte[]> bytes) {
      this.namespaces.add(path.getNamespace());
      this.resources.put(path, Suppliers.memoize(bytes::get));
   }

   protected void build(List<ResourceLocation> paths, Supplier<JsonElement> json) {
      for (ResourceLocation path : paths) {
         JsonElement element = json.get();
         this.build(DynamicResourceType.GENERAL.getPath(path), () -> {
            try {
               byte[] var5x;
               try (StringWriter stringWriter = new StringWriter()) {
                  JsonWriter jsonWriter = new JsonWriter(stringWriter);

                  try {
                     jsonWriter.setIndent("  ");
                     Streams.write(element, jsonWriter);
                     var5x = stringWriter.toString().getBytes();
                  } catch (Throwable var9) {
                     try {
                        jsonWriter.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }

                     throw var9;
                  }

                  jsonWriter.close();
               }

               return var5x;
            } catch (IOException var11) {
               Constants.LOGGER.error("Failed to write JSON {} to resource pack\n{}.", new Object[]{path, this.name, var11});
               return new byte[0];
            }
         });
      }
   }

   protected abstract void build();

   public static class DynamicResourcesSupplier implements ResourcesSupplier {
      private final PackResources instance;

      DynamicResourcesSupplier(PackResources instance) {
         this.instance = instance;
      }

      @NotNull
      public PackResources openPrimary(@NotNull PackLocationInfo name) {
         return this.instance;
      }

      @NotNull
      public PackResources openFull(@NotNull PackLocationInfo name, @NotNull Metadata info) {
         return this.instance;
      }
   }
}
