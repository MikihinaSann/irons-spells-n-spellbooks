package io.redspace.ironslib.statue.client;

import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.statue.PlayerStatueModelType;
import net.minecraft.resources.ResourceLocation;

public record StatueTextureHolder(PatreonPermissions permissions, ResourceLocation textureLocation, PlayerStatueModelType modelType) {
}
