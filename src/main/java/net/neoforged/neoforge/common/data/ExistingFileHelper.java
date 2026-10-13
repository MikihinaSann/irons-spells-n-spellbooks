package net.neoforged.neoforge.common.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

/**
 * Datagen-only stub for NeoForge's ExistingFileHelper.
 */
public class ExistingFileHelper {

    public boolean exists(ResourceLocation loc, PackType packType) {
        return true;
    }

    public boolean exists(ResourceLocation loc, IResourceType type) {
        return true;
    }

    public enum PackType {
        CLIENT_ASSETS, SERVER_DATA
    }

    public interface IResourceType {
        PackType getPackType();
    }
}
