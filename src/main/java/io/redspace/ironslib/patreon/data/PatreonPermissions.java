package io.redspace.ironslib.patreon.data;

import io.redspace.ironslib.transmog.TransmogHolder;
import net.neoforged.fml.loading.FMLLoader;

public enum PatreonPermissions {
   None("tooltip.irons_lib.patreon.tier.none"),
   Acolyte("tooltip.irons_lib.patreon.tier.acolyte"),
   Wizard("tooltip.irons_lib.patreon.tier.wizard"),
   AncientMagician("tooltip.irons_lib.patreon.tier.ancient_magician");

   final String descriptionId;

   private PatreonPermissions(String descriptionId) {
      this.descriptionId = descriptionId;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }

   public boolean canUse(TransmogHolder holder) {
      return !FMLLoader.isProduction() || this.compareTo(holder.requiredPermission()) >= 0;
   }

   public boolean supportsStatues() {
      return this.compareTo(Wizard) >= 0;
   }
}
