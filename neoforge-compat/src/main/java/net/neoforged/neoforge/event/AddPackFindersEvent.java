package net.neoforged.neoforge.event;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AddPackFindersEvent extends Event {
    private final PackType packType;
    private final List<RepositorySource> sources = new ArrayList<>();

    public AddPackFindersEvent(PackType packType) {
        this.packType = packType;
    }

    public PackType getPackType() {
        return packType;
    }

    public void addRepositorySource(RepositorySource source) {
        sources.add(source);
    }

    /**
     * NeoForge-style helper: registers a pack that lives inside a mod jar at
     * {@code pathPrefix} (namespace = mod id, path = directory in the jar).
     */
    public void addPackFinders(net.minecraft.resources.ResourceLocation pathPrefix, PackType packType,
                               net.minecraft.network.chat.Component packName,
                               net.minecraft.server.packs.repository.PackSource packSource, boolean alwaysActive,
                               net.minecraft.server.packs.repository.Pack.Position position) {
        sources.add(consumer -> {
            var modContainer = net.fabricmc.loader.api.FabricLoader.getInstance()
                    .getModContainer(pathPrefix.getNamespace());
            if (modContainer.isEmpty()) {
                return;
            }
            java.nio.file.Path path = modContainer.get().findPath(pathPrefix.getPath()).orElse(null);
            if (path == null) {
                return;
            }
            var location = new net.minecraft.server.packs.PackLocationInfo(
                    pathPrefix.toString(), packName, packSource, java.util.Optional.empty());
            var pack = net.minecraft.server.packs.repository.Pack.readMetaAndCreate(location,
                    new net.minecraft.server.packs.PathPackResources.PathResourcesSupplier(path),
                    packType, new net.minecraft.server.packs.PackSelectionConfig(alwaysActive, position, false));
            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }

    public List<RepositorySource> getSources() {
        return sources;
    }
}
