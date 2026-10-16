package com.userofbricks.expanded_combat.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.client.renderer.GauntletRenderer;
import com.userofbricks.expanded_combat.init.ECAttributes;
import com.userofbricks.expanded_combat.init.ECEnchantments;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo;

public class GauntletItem extends Item implements ICurioItem, IMaterialItem {
   public final GauntletItem.Layer[] GAUNTLET_TEXTURE_LAYERS;
   public final Material material;
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      @NotNull
      protected ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack itemStack) {
         return GauntletItem.dispenseGauntlet(blockSource, itemStack) ? itemStack : super.execute(blockSource, itemStack);
      }
   };

   public static boolean dispenseGauntlet(BlockSource blockSource, ItemStack stack) {
      BlockPos blockpos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
      List<LivingEntity> list = blockSource.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(blockpos),
            EntitySelector.NO_SPECTATORS
               .and(entity -> entity instanceof LivingEntity livingEntity ? CuriosApi.getEntitySlots(livingEntity).containsKey("hands") : false)
         );
      if (list.isEmpty()) {
         return false;
      } else {
         LivingEntity livingentity = list.getFirst();
         Optional<SlotResult> optionalSlotResult = CuriosApi.getCuriosInventory(livingentity).flatMap(curiosInventory -> curiosInventory.findCurio("hands", 0));
         if (optionalSlotResult.isPresent() && !optionalSlotResult.get().stack().isEmpty()) {
            return false;
         } else {
            ItemStack itemstack = stack.split(1);
            ((ICuriosItemHandler)CuriosApi.getCuriosInventory(livingentity).get()).setEquippedCurio("hands", 0, itemstack);
            if (livingentity instanceof Mob) {
               ((Mob)livingentity).setPersistenceRequired();
            }

            return true;
         }
      }
   }

   public GauntletItem(Properties properties, Material material) {
      this(properties, material, new GauntletItem.Layer());
   }

   public GauntletItem(Properties properties, Material materialIn, GauntletItem.Layer... layers) {
      super(
         materialIn.defense().fireResistant()
            ? properties.durability(materialIn.durability().gauntletDurability()).fireResistant()
            : properties.durability(materialIn.durability().gauntletDurability())
      );
      this.material = materialIn;
      this.GAUNTLET_TEXTURE_LAYERS = layers;
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
   }

   @Override
   public Material getMaterial() {
      return this.material;
   }

   @ParametersAreNonnullByDefault
   @Override
   public int getEnchantmentValue(ItemStack stack) {
      return this.getMaterial().enchanting().offenseEnchantability() / 2 + this.getMaterial().enchanting().defenseEnchantability() / 2;
   }

   public int getArmorAmount() {
      return this.getMaterial().defense().gauntletArmorAmount();
   }

   public double getAttackDamage() {
      return this.getMaterial().offense().addedAttackDamage();
   }

   public Supplier<ICurioRenderer> getGauntletRenderer() {
      return GauntletRenderer::new;
   }

   @NotNull
   public SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
      GauntletItem gauntletItem = (GauntletItem)stack.getItem();
      return new SoundInfo((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(gauntletItem.getMaterial().defense().equipSound()), 1.0F, 1.0F);
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation uuid, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> atts = HashMultimap.create();
      LivingEntity entity = slotContext.entity();
      double totalBaseDamage = Math.max(((GauntletItem)stack.getItem()).getAttackDamage(), 0.5);
      double totalExtraDamage = this.getAdditionalDamageAfterEnchantments(totalBaseDamage);
      AttributeModifier attackModifier = new AttributeModifier(uuid, (totalBaseDamage + totalExtraDamage) / 2.0, Operation.ADD_VALUE);
      atts.put(Attributes.ATTACK_DAMAGE, attackModifier);
      atts.put(ECAttributes.GAUNTLET_DMG_WITHOUT_WEAPON, attackModifier);
      atts.put(Attributes.ARMOR, new AttributeModifier(uuid, ((GauntletItem)stack.getItem()).getArmorAmount(), Operation.ADD_VALUE));
      double toughness = ((GauntletItem)stack.getItem()).getMaterial().defense().armorToughness();
      atts.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, toughness, Operation.ADD_VALUE));
      double knockbackResistance = ((GauntletItem)stack.getItem()).getMaterial().defense().knockbackResistance();
      atts.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, knockbackResistance, Operation.ADD_VALUE));
      if (entity != null) {
         Registry<Enchantment> enchantmentRegistry = entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
         double totalEnchantedDamage = stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(Enchantments.PUNCH));
         atts.put(
            Attributes.ATTACK_DAMAGE, new AttributeModifier(ExpandedCombat.modLoc("gauntlet_ench_atk_dmg"), totalEnchantedDamage / 2.0, Operation.ADD_VALUE)
         );
         atts.put(
            ECAttributes.GAUNTLET_DMG_WITHOUT_WEAPON,
            new AttributeModifier(ExpandedCombat.modLoc("gauntlet_ench_dmg_no_weapon"), totalEnchantedDamage, Operation.ADD_VALUE)
         );
         atts.put(
            Attributes.KNOCKBACK_RESISTANCE,
            new AttributeModifier(
               ExpandedCombat.modLoc("gauntlet_ench_knock_resist"),
               stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.KNOCKBACK_RESISTANCE)) / 10.0F,
               Operation.ADD_VALUE
            )
         );
         atts.put(
            Attributes.ATTACK_KNOCKBACK,
            new AttributeModifier(
               ExpandedCombat.modLoc("gauntlet_ench_knock"),
               stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(Enchantments.KNOCKBACK)),
               Operation.ADD_VALUE
            )
         );
         if (stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.AGILITY)) > 0) {
            atts.put(
               Attributes.ATTACK_SPEED,
               new AttributeModifier(
                  ExpandedCombat.modLoc("gauntlet_ench_atk_speed"),
                  stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.AGILITY)) * 0.02,
                  Operation.ADD_VALUE
               )
            );
         }
      }

      return atts;
   }

   public double getAdditionalDamageAfterEnchantments(double totalBaseDamage) {
      return 0.0;
   }

   public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
      return true;
   }

   @ParametersAreNonnullByDefault
   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment != Enchantments.KNOCKBACK && enchantment != Enchantments.PUNCH ? super.supportsEnchantment(stack, enchantment) : true;
   }

   public static final class Layer {
      private final String suffix;
      private final boolean dyeable;
      private final BiFunction<ResourceLocation, ItemStack, ResourceLocation> texture;

      public Layer(String suffix, boolean pDyeable) {
         this.suffix = suffix;
         this.dyeable = pDyeable;
         this.texture = this.resolveTexture();
      }

      public Layer(String suffix) {
         this(suffix, false);
      }

      public Layer() {
         this("", false);
      }

      public Layer(boolean dyeable) {
         this("", dyeable);
      }

      public Layer(ResourceLocation relativeTexture, boolean pDyeable) {
         this.suffix = "";
         this.dyeable = pDyeable;
         this.texture = (assetName, stack) -> relativeTexture.withPath(p_324187_ -> "textures/model/gauntlet/" + relativeTexture.getPath() + ".png");
      }

      public Layer(BiFunction<ResourceLocation, ItemStack, ResourceLocation> relativeTexture, boolean pDyeable) {
         this.suffix = "";
         this.dyeable = pDyeable;
         this.texture = relativeTexture;
      }

      private BiFunction<ResourceLocation, ItemStack, ResourceLocation> resolveTexture() {
         return (assetName, stack) -> assetName.withPath(
            p_324187_ -> "textures/model/gauntlet/" + assetName.getPath() + (!this.suffix.isEmpty() ? "_" + this.suffix : "") + ".png"
         );
      }

      public ResourceLocation texture(ResourceLocation material, ItemStack stack) {
         return this.texture.apply(material, stack);
      }

      public boolean dyeable() {
         return this.dyeable;
      }
   }
}
