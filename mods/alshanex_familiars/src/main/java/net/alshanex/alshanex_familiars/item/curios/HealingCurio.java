package net.alshanex.alshanex_familiars.item.curios;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.util.CurioUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;

public class HealingCurio extends SimpleDescriptiveCurio {
   public HealingCurio(Properties properties) {
      super(properties.rarity(Rarity.UNCOMMON), Curios.RING_SLOT);
   }

   private void handleHeal(SlotContext slotContext) {
      LivingEntity entity = slotContext.entity();
      if (entity != null && !slotContext.entity().level.isClientSide && CurioUtils.isWearingCurio(slotContext.entity(), (Item)ItemRegistry.HEALING_CURIO.get())
         )
       {
         this.healPets(slotContext.entity().level, slotContext.entity());
      }
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      super.curioTick(slotContext, stack);
      if (slotContext.entity().tickCount % 10 == 0) {
         this.handleHeal(slotContext);
      }
   }

   private void healPets(Level level, LivingEntity player) {
      for (Entity entity : level.getEntities(
         player,
         player.getBoundingBox().inflate(20.0),
         entityx -> entityx instanceof AbstractSpellCastingPet petx && petx.getSummoner() != null && petx.getSummoner().is(player)
      )) {
         AbstractSpellCastingPet pet = (AbstractSpellCastingPet)entity;
         if (pet.getHealth() < pet.getMaxHealth() && player instanceof ServerPlayer serverPlayer) {
            MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
            if (magicData.getMana() >= 5.0F) {
               float newMana = Math.max(magicData.getMana() - 5.0F, 0.0F);
               magicData.setMana(newMana);
               pet.heal(1.0F);
               PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData), new CustomPacketPayload[0]);
            }
         }
      }
   }
}
