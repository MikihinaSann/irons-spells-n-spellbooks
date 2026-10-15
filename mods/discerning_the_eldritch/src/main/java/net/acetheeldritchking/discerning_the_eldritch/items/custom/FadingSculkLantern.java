package net.acetheeldritchking.discerning_the_eldritch.items.custom;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FadingSculkLantern extends Item {
   public FadingSculkLantern() {
      super(ItemPropertiesHelper.material().rarity((Rarity)ASRarities.SCULK_RARITY_PROXY.getValue()).stacksTo(1));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.translatable("item.discerning_the_eldritch.fading_sculk_lantern.description")
            .withStyle(ChatFormatting.GRAY)
            .withStyle(ChatFormatting.ITALIC)
      );
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      ItemStack itemStack = player.getItemInHand(usedHand);
      if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
         player.getCooldowns().addCooldown((Item)ItemRegistries.FADING_SCULK_LANTERN.get(), 200);
         serverPlayer.connection
            .send(
               new ClientboundSetActionBarTextPacket(
                  Component.translatable("display.discerning_the_eldritch.fading_sculk_lantern_fail").withStyle(s -> s.withColor(TextColor.fromRgb(37525)))
               )
            );
         this.onUseEffects(serverPlayer, serverLevel, player.position(), false);
      }

      player.swing(usedHand);
      return InteractionResultHolder.pass(itemStack);
   }

   public void onUseEffects(Player player, ServerLevel serverLevel, Vec3 usePosition, boolean success) {
      serverLevel.playSound(
         null,
         usePosition.x,
         usePosition.y,
         usePosition.z,
         success ? DTESoundRegistry.SCULK_LANTERN_TOLL_SUCCESS : DTESoundRegistry.SCULK_LANTERN_TOLL,
         SoundSource.PLAYERS,
         6.0F,
         1.0F
      );
      ASUtils.spawnParticlesInRing(16, 0.25F, 1.25F, 2.25F, 0.5F, 0.1F, player, ParticleTypes.SCULK_SOUL);
   }
}
