package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * NeoForge's {@code Sheets.addWoodType} — registers sign/hanging-sign
 * materials for a modded {@link WoodType}.
 */
public final class NeoForgeSheets {
    private NeoForgeSheets() {
    }

    public static void addWoodType(WoodType woodType) {
        ResourceLocation id = ResourceLocation.parse(woodType.name());
        Sheets.SIGN_MATERIALS.put(woodType,
                new Material(Sheets.SIGN_SHEET, id.withPrefix("entity/signs/")));
        Sheets.HANGING_SIGN_MATERIALS.put(woodType,
                new Material(Sheets.SIGN_SHEET, id.withPrefix("entity/signs/hanging/")));
    }
}
