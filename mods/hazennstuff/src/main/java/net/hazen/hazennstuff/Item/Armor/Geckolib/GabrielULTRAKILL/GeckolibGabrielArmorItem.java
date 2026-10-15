package net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import java.util.List;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.Level;
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

public class GeckolibGabrielArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GeckolibGabrielArmorItem(Type type, Properties settings) {
      super(HnSArmorMaterials.PURE_ARMOR_TIER_MATERIAL, type, settings, pureTier(AttributeRegistry.HOLY_SPELL_POWER));
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addArcaneResonance(attributes, group);
      ArsNoveauCompat.addMaxMana(attributes, group);
      ArsNoveauCompat.addManaRegen(attributes, group);
      return attributes.build().modifiers();
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.ultrakill.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC}));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.geckolibGabrielArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.MAGE_SET_BONUS, 320, 0, false, false, false));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof GeckolibGabrielArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof GeckolibGabrielElytraItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof GeckolibGabrielArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof GeckolibGabrielArmorItem;
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return true;
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return ElytraItem.isFlyEnabled(stack);
   }

   @Override
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

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "arbitrium_armor", 10, this::wings));
   }
}
