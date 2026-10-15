package net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GeckolibArbitriumRobesElytraItem extends ImbuableChestplateArmorItem implements IDisableJacket {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GeckolibArbitriumRobesElytraItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.NERFED_DEUS_MATERIAL,
         type,
         settings,
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, 500.0, Operation.ADD_VALUE),
            new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
         }
      );
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(
         Component.translatable("item.hazennstuff.arbitrium_robes.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC})
      );
   }

   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addArcaneResonance(attributes, group);
      ArsNoveauCompat.addMaxMana(attributes, group);
      ArsNoveauCompat.addManaRegen(attributes, group);
      return attributes.build().modifiers();
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return HazennstuffClientRenderers.geckolibArbitriumRobesElytraItem(); }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return true;
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return ElytraItem.isFlyEnabled(stack);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

      private PlayState wings(AnimationState animationState) {
      Entity rawEntity = (Entity) animationState.getData(DataTickets.ENTITY);
      boolean flying = rawEntity instanceof LivingEntity living && (living instanceof Player player
         ? player.getAbilities().flying || player.isFallFlying() && !player.onGround()
         : living.isFallFlying());
      animationState.getController().setAnimation(RawAnimation.begin().then(flying ? "flying" : "idle", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "arbitrium_robes", 10, this::wings));
   }
}