package net.acetheeldritchking.discerning_the_eldritch.spells.holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.spells.CastResult.Type;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttachmentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ExorcismSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "exorcism");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(25.0)
      .build();

   public ExorcismSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 10;
      this.baseManaCost = 75;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.CLEANSE_CAST.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CAST_KNEELING_PRAYER;
   }

   public boolean allowCrafting() {
      return DTEServerConfig.enableInsanitySystem;
   }

   public boolean allowLooting() {
      return DTEServerConfig.enableInsanitySystem;
   }

   public CastResult canBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player) {
      if (!DTEServerConfig.enableInsanitySystem) {
         if (player instanceof ServerPlayer serverPlayer) {
            return new CastResult(
               Type.FAILURE,
               Component.translatable("display.discerning_the_eldritch.no_insanity_system", new Object[]{this.getDisplayName(player)})
                  .withStyle(ChatFormatting.RED)
            );
         }
      } else if (player.hasData(DTEAttachmentRegistry.INSANITY_METER)
         && player.hasData(DTEAttachmentRegistry.IS_INSANE)
         && (Integer)player.getData(DTEAttachmentRegistry.INSANITY_METER) <= 0
         && !(Boolean)player.getData(DTEAttachmentRegistry.IS_INSANE)
         && player instanceof ServerPlayer serverPlayer) {
         return new CastResult(
            Type.FAILURE,
            Component.translatable("display.discerning_the_eldritch.exorcism_no_insanity", new Object[]{this.getDisplayName(player)})
               .withStyle(ChatFormatting.RED)
         );
      }

      return super.canBeCastedBy(spellLevel, castSource, playerMagicData, player);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (entity.hasData(DTEAttachmentRegistry.INSANITY_METER) && entity.hasData(DTEAttachmentRegistry.IS_INSANE)) {
         if ((Integer)entity.getData(DTEAttachmentRegistry.INSANITY_METER) <= 0) {
            if (entity instanceof ServerPlayer player) {
               player.connection
                  .send(
                     new ClientboundSetActionBarTextPacket(
                        Component.translatable("display.discerning_the_eldritch.exorcism_no_insanity").withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                     )
                  );
            }
         } else {
            entity.setData(DTEAttachmentRegistry.INSANITY_METER, 0);
            entity.setData(DTEAttachmentRegistry.IS_INSANE, false);
         }
      } else if (entity instanceof ServerPlayer player) {
         player.connection
            .send(
               new ClientboundSetActionBarTextPacket(
                  Component.translatable("display.discerning_the_eldritch.exorcism_no_insanity").withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
               )
            );
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
