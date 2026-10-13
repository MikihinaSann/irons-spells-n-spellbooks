package net.neoforged.neoforge.client.model.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Additional model data passed during baking (attached data block entities
 * provide one).
 */
public final class ModelData {
    public static final ModelData EMPTY = new ModelData(Map.of());

    private final Map<ModelProperty<?>, Object> properties;

    private ModelData(Map<ModelProperty<?>, Object> properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ModelProperty<T> property) {
        return (T) properties.get(property);
    }

    public <T> boolean has(ModelProperty<T> property) {
        return properties.containsKey(property);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final ImmutableMap.Builder<ModelProperty<?>, Object> properties = ImmutableMap.builder();

        public <T> Builder with(ModelProperty<T> property, T value) {
            properties.put(property, value);
            return this;
        }

        public ModelData build() {
            Map<ModelProperty<?>, Object> built = properties.buildOrThrow();
            if (built.isEmpty()) {
                return EMPTY;
            }
            return new ModelData(built);
        }
    }

    public static final class ModelProperty<T> {
    }
}
