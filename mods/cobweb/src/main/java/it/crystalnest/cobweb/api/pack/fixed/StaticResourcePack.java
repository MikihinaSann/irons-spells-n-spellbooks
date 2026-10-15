package it.crystalnest.cobweb.api.pack.fixed;

import it.crystalnest.cobweb.platform.Services;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import org.jetbrains.annotations.NotNull;

public abstract class StaticResourcePack {
   private final ResourceLocation location;
   private final PackType type;
   private final Component name;
   private final PackSource source;
   private final Position position;
   private final boolean alwaysActive;

   protected StaticResourcePack(ResourceLocation location, PackType type, PackSource source, Position position, boolean alwaysActive) {
      this.location = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), this.directory() + "/" + location.getPath());
      this.type = type;
      this.name = Component.translatable(this.directory() + "." + location.getNamespace() + "." + location.getPath());
      this.source = source;
      this.position = position;
      this.alwaysActive = alwaysActive;
   }

   protected StaticResourcePack(ResourceLocation location, PackType type, Position position) {
      this(location, type, PackSource.BUILT_IN, position, false);
   }

   public Pack toPack() {
      if (Services.PLATFORM.isModLoaded(this.location.getNamespace())) {
         return Pack.readMetaAndCreate(
            new PackLocationInfo("mod/" + this.location, this.name, this.source, Optional.of(new KnownPack("cobweb", "mod/" + this.location, this.version()))),
            new StaticResourcePack.StaticResourcesSupplier(Services.PLATFORM.getResourcePath(this.location.getNamespace(), this.location.getPath())),
            this.type,
            new PackSelectionConfig(this.alwaysActive, this.position, false)
         );
      } else {
         throw new IllegalArgumentException("Mod not found: " + this.location.getNamespace());
      }
   }

   public ResourceLocation location() {
      return this.location;
   }

   public PackType type() {
      return this.type;
   }

   public Component name() {
      return this.name;
   }

   public PackSource source() {
      return this.source;
   }

   public Position position() {
      return this.position;
   }

   public boolean alwaysActive() {
      return this.alwaysActive;
   }

   public String version() {
      return Services.PLATFORM.getModVersion(this.location.getNamespace());
   }

   public abstract String directory();

   public void register() {
      Services.REGISTRY.registerStaticResourcePack(this);
   }

   public static class StaticResourcesSupplier implements ResourcesSupplier {
      private final Path path;

      public StaticResourcesSupplier(Path path) {
         this.path = path;
      }

      @NotNull
      public PackResources openPrimary(@NotNull PackLocationInfo info) {
         return this.openInfo(info);
      }

      @NotNull
      public PackResources openFull(@NotNull PackLocationInfo info, @NotNull Metadata metadata) {
         return this.openInfo(info);
      }

      private PackResources openInfo(@NotNull PackLocationInfo info) {
         return new PathPackResources(info, this.path);
      }
   }
}
