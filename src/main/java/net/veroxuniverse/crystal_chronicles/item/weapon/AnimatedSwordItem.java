package net.veroxuniverse.crystal_chronicles.item.weapon;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager.SelectionOption;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.level.Level;

public class AnimatedSwordItem extends ExtendedSwordItem {
   public AnimatedSwordItem(Tier pTier, Properties pProperties) {
      super(pTier, pProperties);
   }

   public static ItemAttributeModifiers createAttributes(IronsWeaponTier pTier) {
      Builder builder = ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)pTier.getAttackDamageBonus(), Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)pTier.getSpeed(), Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);

      for (AttributeContainer holder : pTier.getAdditionalAttributes()) {
         builder.add(holder.attribute(), holder.createModifier(EquipmentSlot.MAINHAND.getName()), EquipmentSlotGroup.MAINHAND);
      }

      return builder.build();
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      SpellSelectionManager spellSelectionManager = new SpellSelectionManager(player);
      SelectionOption selectionOption = spellSelectionManager.getSelection();
      if (selectionOption != null && !selectionOption.spellData.equals(SpellData.EMPTY)) {
         SpellData spellData = selectionOption.spellData;
         int spellLevel = spellData.getSpell().getLevelFor(spellData.getLevel(), player);
         if (!level.isClientSide()) {
            String castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND : SpellSelectionManager.OFFHAND;
            return spellData.getSpell().attemptInitiateCast(itemStack, spellLevel, level, player, selectionOption.getCastSource(), true, castingSlot)
               ? InteractionResultHolder.consume(itemStack)
               : InteractionResultHolder.fail(itemStack);
         } else if (ClientMagicData.isCasting()) {
            return InteractionResultHolder.consume(itemStack);
         } else {
            return ClientMagicData.getPlayerMana() >= spellData.getSpell().getManaCost(spellLevel)
                  && !ClientMagicData.getCooldowns().isOnCooldown(spellData.getSpell())
                  && ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellData.getSpell())
               ? InteractionResultHolder.consume(itemStack)
               : InteractionResultHolder.pass(itemStack);
         }
      } else {
         return InteractionResultHolder.pass(itemStack);
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged;
   }
}
