package it.crystalnest.prometheus.api.client;

import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public final class FireClient {
   private final ResourceLocation fireType;
   private final Material material0;
   private final Material material1;

   FireClient(ResourceLocation fireType) {
      this.fireType = fireType;
      String modId = fireType.getNamespace();
      String fireId = fireType.getPath();
      String joiner = FireManager.DEFAULT_FIRE_TYPE.equals(fireType) ? "" : "_";
      this.material0 = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(modId, "block/" + fireId + joiner + "fire_0"));
      this.material1 = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(modId, "block/" + fireId + joiner + "fire_1"));
   }

   public ResourceLocation getFireType() {
      return this.fireType;
   }

   public Material getMaterial0() {
      return this.material0;
   }

   public Material getMaterial1() {
      return this.material1;
   }

   public TextureAtlasSprite getSprite0() {
      return this.material0.sprite();
   }

   public TextureAtlasSprite getSprite1() {
      return this.material1.sprite();
   }

   @Override
   public String toString() {
      return "FireClient [fireType=" + this.fireType + ", material0=" + this.material0 + ", material1=" + this.material1 + "]";
   }
}
