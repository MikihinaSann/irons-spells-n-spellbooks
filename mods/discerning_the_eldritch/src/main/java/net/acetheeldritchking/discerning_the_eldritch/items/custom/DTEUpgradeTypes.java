package net.acetheeldritchking.discerning_the_eldritch.items.custom;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.UpgradeType;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;

public enum DTEUpgradeTypes implements UpgradeType {
   ELDRITCH_SPELL_POWER("eldritch_power", ItemRegistries.ELDRITCH_UPGRADE_ORB, AttributeRegistry.ELDRITCH_SPELL_POWER, Operation.ADD_MULTIPLIED_BASE, 0.05F);

   final Holder<Attribute> attribute;
   final Operation operation;
   final float amountPerUpgrade;
   final ResourceLocation id;
   final Optional<Holder<Item>> containerItem;

   private DTEUpgradeTypes(String key, Holder<Item> containerItem, Holder<Attribute> attribute, Operation operation, float amountPerUpgrade) {
      this(key, Optional.of(containerItem), attribute, operation, amountPerUpgrade);
   }

   private DTEUpgradeTypes(String key, Optional<Holder<Item>> containerItem, Holder<Attribute> attribute, Operation operation, float amountPerUpgrade) {
      this.id = DiscerningTheEldritch.id(key);
      this.attribute = attribute;
      this.operation = operation;
      this.amountPerUpgrade = amountPerUpgrade;
      this.containerItem = containerItem;
      UpgradeType.registerUpgrade(this);
   }

   public Holder<Attribute> getAttribute() {
      return this.attribute;
   }

   public Operation getOperation() {
      return this.operation;
   }

   public float getAmountPerUpgrade() {
      return this.amountPerUpgrade;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Optional<Holder<Item>> getContainerItem() {
      return this.containerItem;
   }
}
