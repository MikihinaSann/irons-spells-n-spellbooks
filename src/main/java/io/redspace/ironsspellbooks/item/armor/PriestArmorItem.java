package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.render.ClientArmorRenderers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorModel;
import io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorRenderer;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PriestArmorItem extends ImbuableChestplateArmorItem {
    public PriestArmorItem(ArmorItem.Type slot, Properties settings) {
        super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.HOLY_SPELL_POWER));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object supplyRenderer() { return ClientArmorRenderers.priest(); }
}
