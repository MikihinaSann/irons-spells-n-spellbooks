package net.acetheeldritchking.discerning_the_eldritch.items.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.minecraft.world.item.Rarity;

public class EchoVibrationRingCurio extends SimpleDescriptiveCurio {
   public EchoVibrationRingCurio() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).rarity((Rarity)ASRarities.SCULK_RARITY_PROXY.getValue()).fireResistant(), Curios.RING_SLOT);
   }
}
