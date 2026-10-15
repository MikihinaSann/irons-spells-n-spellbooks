package com.userofbricks.ecisasbplugin.config;

import com.userofbricks.expanded_combat.config.MaterialConfig;
import com.userofbricks.expanded_combat.config.MaterialConfig.Builder;
import com.userofbricks.expanded_combat.config.gui.ConfigName;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import net.minecraft.sounds.SoundEvents;

@Config(
   name = "ec_isasb_plugin"
)
public class ECISaSbConfig implements ConfigData {
   @Category("Materials")
   @CollapsibleObject
   @ConfigName("Wandering Magician Settings")
   public MaterialConfig wanderingMagician = new Builder()
      .offenseEnchantability(15)
      .defenseEnchantability(15)
      .toolDurability(540)
      .gauntletDurability(540)
      .addedAttackDamage(1.0F)
      .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER.getRegisteredName())
      .gauntletArmorAmount(2)
      .armorToughness(0.0)
      .build();
   @Category("Materials")
   @CollapsibleObject
   @ConfigName("Scarecrow Settings")
   public MaterialConfig scarecrow = new Builder()
      .offenseEnchantability(15)
      .defenseEnchantability(15)
      .toolDurability(1782)
      .gauntletDurability(1782)
      .addedAttackDamage(1.0F)
      .equipSound(SoundEvents.ARMOR_EQUIP_TURTLE.getRegisteredName())
      .gauntletArmorAmount(3)
      .armorToughness(0.0)
      .build();
   @Category("Materials")
   @CollapsibleObject
   @ConfigName("Magic Clothes Settings")
   public MaterialConfig magicClothes = new Builder()
      .offenseEnchantability(15)
      .defenseEnchantability(15)
      .toolDurability(2052)
      .gauntletDurability(2052)
      .addedAttackDamage(2.0F)
      .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER.getRegisteredName())
      .gauntletArmorAmount(3)
      .armorToughness(0.0)
      .build();
   @Category("Materials")
   @CollapsibleObject
   @ConfigName("Netherite Battlemage Settings")
   public MaterialConfig netheriteBattlemage = new Builder()
      .offenseEnchantability(15)
      .defenseEnchantability(15)
      .toolDurability(2052)
      .gauntletDurability(2052)
      .addedAttackDamage(3.0F)
      .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE.getRegisteredName())
      .gauntletArmorAmount(3)
      .armorToughness(3.0)
      .fireResistant()
      .build();
}
