package it.crystalnest.cobweb.api.pack.dynamic;

import net.minecraft.resources.ResourceLocation;

public enum DynamicResourceType {
   GENERAL("%s"),
   TAG("tags/%s.json");

   private final String path;

   private DynamicResourceType(String path) {
      this.path = path;
   }

   public ResourceLocation getPath(ResourceLocation id) {
      return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), String.format(this.path, id.getPath()));
   }
}
