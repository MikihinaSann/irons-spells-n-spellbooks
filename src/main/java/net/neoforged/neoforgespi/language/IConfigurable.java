package net.neoforged.neoforgespi.language;

import java.util.Optional;

public interface IConfigurable {
    IConfigurable EMPTY = new IConfigurable() {
    };

    default <T> Optional<T> getConfigElement(String... key) {
        return Optional.empty();
    }
}
