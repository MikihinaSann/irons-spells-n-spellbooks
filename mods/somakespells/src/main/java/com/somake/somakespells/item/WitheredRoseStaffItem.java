package com.somake.somakespells.item;
import com.somake.somakespells.SomakespellsClientRenderers;

import com.somake.somakespells.client.renderer.item.WitheredRoseStaffRenderer;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.util.StaffHandCompat;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.render.ClientStaffItemExtensions;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WitheredRoseStaffItem extends StaffItem implements GeoItem, IPresetSpellContainer {
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WitheredRoseStaffItem(Properties properties) {
      super(properties);
      GeoItem.registerSyncedAnimatable(this);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         ISpellContainer existing = ISpellContainer.get(itemStack);
         if (existing == null || existing.getMaxSpellCount() != 1 || existing.getIndexForSpell((AbstractSpell)ModSpells.ROSE_SECRET.get()) < 0) {
            ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
            spellContainer.addSpellAtIndex((AbstractSpell)ModSpells.ROSE_SECRET.get(), 5, 0, true);
            ISpellContainer.set(itemStack, spellContainer.toImmutable());
         }
      }
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("somakespells", "withered_rose_staff_modifier");
      Builder builder = StaffHandCompat.createStaffAttributeBuilder(super.getDefaultAttributeModifiers(stack), id, 7.0, -2.4, 0.0);
      EquipmentSlotGroup slotGroup = StaffHandCompat.getConfiguredStaffSlot();
      builder.add(AttributeRegistry.BLOOD_SPELL_POWER, new AttributeModifier(id, 0.15, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.MANA_REGEN, new AttributeModifier(id, 0.25, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      super.inventoryTick(stack, level, entity, slotId, isSelected);
      if (!level.isClientSide) {
         this.initializeSpellContainer(stack);
      }
   }

   public boolean hasCustomRendering() {
      return true;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(SomakespellsClientRenderers.itemProvider(SomakespellsClientRenderers::witheredRoseStaffItem));
      }
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new ClientStaffItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return GeoRenderProvider.of(WitheredRoseStaffItem.this).getGeoItemRenderer();
         }
      });
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "Idle", 0, state -> state.setAndContinue(IDLE_ANIMATION)));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
