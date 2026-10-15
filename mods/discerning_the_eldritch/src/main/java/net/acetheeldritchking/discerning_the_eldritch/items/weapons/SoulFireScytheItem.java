package net.acetheeldritchking.discerning_the_eldritch.items.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.item.UniqueItem;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import it.crystalnest.prometheus.api.FireManager;
import java.util.List;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEDataComponentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class SoulFireScytheItem extends MagicSwordItem implements UniqueItem {
   public static final int COOLDOWN = 300;

   public SoulFireScytheItem() {
      super(
         DTEWeaponTiers.SOUL_FIRE_SCYTHE,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)ASRarities.AQUATIC_RARITY_PROXY.getValue())
            .component(DTEDataComponentRegistry.SOUL_FIRE_STACKS, 0)
            .attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.SOUL_FIRE_SCYTHE)),
         SpellDataRegistryHolder.of(
            new SpellDataRegistryHolder[]{
               new SpellDataRegistryHolder(SpellRegistries.SOUL_SLICE, 1), new SpellDataRegistryHolder(SpellRegistries.SOUL_SET_ABLAZE, 1)
            }
         )
      );
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (Screen.hasShiftDown()) {
         LivingEntity attacker = MinecraftInstanceHelper.getPlayer();
         tooltipComponents.add(
            Component.translatable(
                  "tooltip.discerning_the_eldritch.active_ability",
                  new Object[]{Component.literal(Utils.timeFromTicks(300.0F, 1)).withStyle(ChatFormatting.LIGHT_PURPLE)}
               )
               .withStyle(ChatFormatting.DARK_PURPLE)
         );
         tooltipComponents.add(Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(ChatFormatting.YELLOW));
         tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".desc2").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
         if (stack.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS) != null && attacker != null) {
            tooltipComponents.add(
               Component.translatable(this.getDescriptionId() + ".desc3")
                  .append(soulFireStacks(attacker))
                  .withStyle(ChatFormatting.BLUE)
                  .withStyle(ChatFormatting.ITALIC)
            );
         }
      } else {
         tooltipComponents.add(Component.translatable("item.discerning_the_eldritch.more_details").withStyle(ChatFormatting.GRAY));
      }
   }

   private static String soulFireStacks(LivingEntity entity) {
      ItemStack mainhandItem = entity.getMainHandItem();
      return String.format("%2d", mainhandItem.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS));
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 100;
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
      if (livingEntity instanceof Player player) {
         int ticks = this.getUseDuration(stack, player) - timeCharged;
         boolean success = false;
         Integer soulFireStacks = (Integer)stack.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS);

         assert soulFireStacks != null;

         double radius = 10.0;
         if (ticks >= 10) {
            if (soulFireStacks >= 5 && this.doSoulFireBurn(level, player, radius)) {
               success = true;
            }

            if (success) {
               livingEntity.level()
                  .playLocalSound(
                     livingEntity.getX(),
                     livingEntity.getY(),
                     livingEntity.getZ(),
                     (SoundEvent)SoundRegistry.FLAMING_STRIKE_SWING.get(),
                     SoundSource.PLAYERS,
                     1.0F,
                     1.0F,
                     false
                  );
               level.addParticle(
                  new BlastwaveParticleOptions(ASUtils.rbgToVector3F(39, 166, 245), (float)radius),
                  player.getX(),
                  player.getY() + 0.35F,
                  player.getZ(),
                  0.0,
                  0.0,
                  0.0
               );
               stack.set(DTEDataComponentRegistry.SOUL_FIRE_STACKS, soulFireStacks - 5);
               player.getCooldowns().addCooldown((Item)ItemRegistries.SOUL_FIRE_SCYTHE.get(), 300);
            }
         }
      }
   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
      int ticks = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
      if (ticks == 10) {
         level.addParticle(
            new BlastwaveParticleOptions(ASUtils.rbgToVector3F(39, 166, 245), 0.75F),
            livingEntity.getX(),
            livingEntity.getY() + 0.165F,
            livingEntity.getZ(),
            0.0,
            0.0,
            0.0
         );
         livingEntity.level()
            .playLocalSound(
               livingEntity.getX(),
               livingEntity.getY(),
               livingEntity.getZ(),
               (SoundEvent)SoundRegistry.FIRE_BOMB_CHARGE.get(),
               SoundSource.PLAYERS,
               1.0F,
               1.0F,
               false
            );
      }
   }

   private boolean doSoulFireBurn(Level level, Player player, double radius) {
      boolean success = false;

      for (LivingEntity targets : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
         if (targets != player) {
            FireManager.setOnFire(targets, 10.0F, FireManager.SOUL_FIRE_TYPE);
            success = true;
         }
      }

      return success;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      ItemStack mainhandItem = player.getMainHandItem();
      Integer soulFireStacks = (Integer)mainhandItem.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS);

      assert soulFireStacks != null;

      if (soulFireStacks >= 5) {
         player.startUsingItem(usedHand);
         return InteractionResultHolder.consume(mainhandItem);
      } else {
         return InteractionResultHolder.fail(mainhandItem);
      }
   }
}
