package net.neoforged.neoforgespi.language;

import java.util.Optional;

public interface IModInfo {
    String getModId();

    default String getDisplayName() {
        return getModId();
    }

    default String getNamespace() {
        return getModId();
    }

    ModVersion getVersion();

    default IModFileInfo getOwningFile() {
        return null;
    }

    default Optional<String> getModURL() {
        return Optional.empty();
    }

    default IConfigurable getConfig() {
        return IConfigurable.EMPTY;
    }

    interface ModVersion {
        String toString();
    }

    /** Basic Fabric-backed implementation. */
    class FabricModInfo implements IModInfo {
        private final net.fabricmc.loader.api.ModContainer container;

        public FabricModInfo(net.fabricmc.loader.api.ModContainer container) {
            this.container = container;
        }

        @Override
        public String getModId() {
            return container.getMetadata().getId();
        }

        @Override
        public String getDisplayName() {
            return container.getMetadata().getName();
        }

        @Override
        public ModVersion getVersion() {
            String v = container.getMetadata().getVersion().getFriendlyString();
            return new ModVersion() {
                @Override
                public String toString() {
                    return v;
                }
            };
        }

        @Override
        public IModFileInfo getOwningFile() {
            return new IModFileInfo() {
                @Override
                public net.neoforged.neoforgespi.locating.IModFile getFile() {
                    return new net.neoforged.fml.ModFileInfo.ModFile(container.getRootPaths());
                }

                @Override
                public IConfigurable getConfig() {
                    return IConfigurable.EMPTY;
                }
            };
        }

        @Override
        public Optional<String> getModURL() {
            return container.getMetadata().getContact().get("homepage")
                    .or(() -> container.getMetadata().getContact().get("sources"));
        }
    }
}
