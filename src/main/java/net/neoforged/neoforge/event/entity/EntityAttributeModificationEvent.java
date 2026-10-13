package net.neoforged.neoforge.event.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collects attribute modifications applied to entity defaults.
 * On Fabric these are applied via {@code FabricDefaultAttributeRegistry} merge style
 * at bootstrap.
 */
public class EntityAttributeModificationEvent extends Event {
    private final Map<EntityType<? extends LivingEntity>, List<AttributeModification>> map;
    private final List<AttributeModification> entries = new ArrayList<>();

    public record AttributeModification(EntityType<? extends LivingEntity> type, Holder<Attribute> attribute, double value) {
    }

    public EntityAttributeModificationEvent(Map<EntityType<? extends LivingEntity>, ?> ignored) {
        this.map = null;
    }

    public EntityAttributeModificationEvent() {
        this.map = null;
    }

    public void add(EntityType<? extends LivingEntity> entityType, Holder<? extends Attribute> attribute, double defaultValue) {
        entries.add(new AttributeModification(entityType, unwrap(attribute), defaultValue));
    }

    public void add(EntityType<? extends LivingEntity> entityType, Holder<? extends Attribute> attribute) {
        add(entityType, attribute, attribute.value().getDefaultValue());
    }

    @SuppressWarnings("unchecked")
    private static Holder<Attribute> unwrap(Holder<? extends Attribute> holder) {
        if (holder instanceof net.neoforged.neoforge.registries.DeferredHolder<?, ?> shim) {
            Holder<Attribute> bound = (Holder<Attribute>) shim.asHolder();
            if (bound != null) {
                return bound;
            }
        }
        return (Holder<Attribute>) holder;
    }

    public boolean has(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        return entries.stream().anyMatch(e -> e.type == entityType && e.attribute.equals(attribute));
    }

    public List<AttributeModification> getEntries() {
        return entries;
    }

    public List<AttributeModification> getList(EntityType<? extends LivingEntity> entityType) {
        return entries.stream().filter(e -> e.type == entityType).toList();
    }

    /**
     * All entity types that have a default attribute supplier (matches
     * NeoForge semantics, which enumerates the vanilla attribute map).
     */
    public List<EntityType<? extends LivingEntity>> getTypes() {
        List<EntityType<? extends LivingEntity>> types = new ArrayList<>();
        for (EntityType<?> type : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE) {
            if (net.minecraft.world.entity.ai.attributes.DefaultAttributes.hasSupplier(type)) {
                @SuppressWarnings("unchecked")
                EntityType<? extends LivingEntity> cast = (EntityType<? extends LivingEntity>) type;
                types.add(cast);
            }
        }
        return types;
    }
}
