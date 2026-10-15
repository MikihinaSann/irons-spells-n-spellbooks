package net.neoforged.neoforgespi.language;

import java.nio.file.Path;
import java.util.List;

public interface IModFileInfo {
    default net.neoforged.neoforgespi.locating.IModFile getFile() {
        return null;
    }

    default IConfigurable getConfig() {
        return IConfigurable.EMPTY;
    }

    default List<IModInfo> getMods() {
        return List.of();
    }
}
