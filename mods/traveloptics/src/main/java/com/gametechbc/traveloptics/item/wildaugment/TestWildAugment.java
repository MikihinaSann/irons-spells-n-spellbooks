package com.gametechbc.traveloptics.item.wildaugment;

import com.gametechbc.traveloptics.api.item.AbstractWildAugment;
import com.gametechbc.traveloptics.init.TOSounds;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item.Properties;

public class TestWildAugment extends AbstractWildAugment {
   public TestWildAugment(Properties properties) {
      super(properties);
   }

   @Override
   protected List<AbstractWildAugment.AttributeEntry> getPositiveAttributeEntries() {
      return List.of(
         new AbstractWildAugment.AttributeEntry((Attribute)AttributeRegistry.NATURE_SPELL_POWER.get(), Operation.ADD_MULTIPLIED_BASE, 0.2, 0.5, 5.0)
      );
   }

   @Override
   protected List<AbstractWildAugment.AttributeEntry> getNegativeAttributeEntries() {
      return List.of(
         new AbstractWildAugment.AttributeEntry((Attribute)AttributeRegistry.ENDER_SPELL_POWER.get(), Operation.ADD_MULTIPLIED_BASE, -0.2, 0.5, 4.0),
         new AbstractWildAugment.AttributeEntry((Attribute)AttributeRegistry.ELDRITCH_SPELL_POWER.get(), Operation.ADD_MULTIPLIED_BASE, -0.2, 0.5, 5.0)
      );
   }

   @Override
   protected List<AbstractWildAugment.AttributeEntry> getChanceAttributeEntries() {
      return List.of(new AbstractWildAugment.AttributeEntry((Attribute)AttributeRegistry.MAX_MANA.get(), Operation.ADD_VALUE, 25.0, 75.0, 5.0));
   }

   @Override
   protected double getGlobalChance() {
      return 0.2F;
   }

   @Override
   protected SoundEvent getAssignSound() {
      return (SoundEvent)TOSounds.ORBITAL_VOID_PULSE.get();
   }

   @Override
   protected Component getInitialHoverText() {
      return Component.literal("§2A mysterious Augment. §eRight-click §2to restore its natural balance.");
   }

   @Override
   protected Component getAssignedHoverText() {
      return Component.literal("§2The balance of this augment enhances nature while suppressing otherworldly forces.");
   }
}
