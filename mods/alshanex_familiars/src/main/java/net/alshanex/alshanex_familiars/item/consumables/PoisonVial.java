package net.alshanex.alshanex_familiars.item.consumables;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import net.alshanex.alshanex_familiars.entity.DruidPetEntity;
import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public class PoisonVial extends Item {
   public PoisonVial() {
      super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
      if (player instanceof ServerPlayer serverPlayer) {
         if (interactionTarget instanceof DruidPetEntity pet && pet.getSummoner() != null && pet.getSummoner().is(player)) {
            PlaguePetEntity plague = new PlaguePetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.PLAGUE_PET.get(), pet.level());
            plague.setArmorStacks(pet.getArmorStacks());
            plague.setHealthStacks(pet.getHealthStacks());
            plague.setEnragedStacks(pet.getEnragedStacks());
            plague.setIsBlocking(pet.getIsBlocking());
            plague.setOwnerUUID(pet.getOwnerUUID());
            plague.setPos(pet.getX(), pet.getY(), pet.getZ());
            plague.setYRot(pet.getYRot());
            pet.level().addFreshEntity(plague);
            MagicManager.spawnParticles(
               pet.level(),
               new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.NATURE.get()).getTargetingColor(), 2.0F),
               pet.getX(),
               pet.getY() + 0.165F,
               pet.getZ(),
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            pet.remove(RemovalReason.DISCARDED);
            stack.shrink(1);
            ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_PLAGUE_TRIGGER.get()).trigger(serverPlayer);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.FAIL;
      }
   }

   public void appendHoverText(@NotNull ItemStack itemStack, TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      lines.add(Component.translatable("tooltip.alshanex_familiars.poison_vial").withStyle(ChatFormatting.WHITE));
   }
}
