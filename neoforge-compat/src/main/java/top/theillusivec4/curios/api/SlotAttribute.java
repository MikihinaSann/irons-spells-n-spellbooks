package top.theillusivec4.curios.api;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * A wrapper class for representing slot types as attributes for use in attribute modifiers
 */
public class SlotAttribute extends Attribute {

  private static final Map<String, Holder<? extends Attribute>> SLOT_ATTRIBUTES = new HashMap<>();

  private final String identifier;

  @SuppressWarnings("unchecked")
  public static Holder<Attribute> getOrCreate(String id) {
    return (Holder<Attribute>) SLOT_ATTRIBUTES.computeIfAbsent(id,
        (k) -> new Holder.Direct<>(new SlotAttribute(id)));
  }

  protected SlotAttribute(String identifier) {
    super("curios.identifier." + identifier, 0);
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return this.identifier;
  }
}
