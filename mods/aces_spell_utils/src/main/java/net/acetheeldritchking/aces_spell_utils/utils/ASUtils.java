package net.acetheeldritchking.aces_spell_utils.utils;

import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager.SelectionOption;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;

public class ASUtils {
   public static boolean hasCurio(Player player, Item item) {
      return CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent();
   }

   public static boolean isLongAnimCast(AbstractSpell spell) {
      return spell.getCastType() == CastType.LONG;
   }

   public static boolean isContAnimCast(AbstractSpell spell) {
      return spell.getCastType() == CastType.CONTINUOUS;
   }

   public static List<AbstractSpell> getSpellsFromTag(TagKey<AbstractSpell> tag) {
      ArrayList<AbstractSpell> list = new ArrayList<>();

      for (AbstractSpell spell : SpellRegistry.getEnabledSpells()) {
         SpellRegistry.REGISTRY.getHolder(spell.getSpellResource()).ifPresent(s -> {
            if (s.is(tag)) {
               list.add(spell);
            }
         });
      }

      return list;
   }

   public static void spawnParticlesInCircle(int count, float radius, float yHeight, float particleSpeed, LivingEntity entity, ParticleOptions particleTypes) {
      for (int i = 0; i < count; i++) {
         double theta = Math.toRadians(360 / count) * i;
         double x = Math.cos(theta) * radius;
         double z = Math.sin(theta) * radius;
         MagicManager.spawnParticles(
            entity.level(),
            particleTypes,
            entity.position().x + x,
            entity.position().y + yHeight,
            entity.position().z + z,
            1,
            0.0,
            0.0,
            0.0,
            particleSpeed,
            false
         );
      }
   }

   public static void spawnParticlesInRing(
      int count, float radius1, float radius2, float radius3, float yHeight, float particleSpeed, LivingEntity entity, ParticleOptions particleTypes
   ) {
      for (int i = 0; i < count; i++) {
         double theta = Math.toRadians(360 / count) * i;
         double x = Math.cos(theta) * radius1;
         double z = Math.sin(theta) * radius1;
         MagicManager.spawnParticles(
            entity.level(),
            particleTypes,
            entity.position().x + x,
            entity.position().y + yHeight,
            entity.position().z + z,
            1,
            0.0,
            0.0,
            0.0,
            particleSpeed,
            false
         );
      }

      for (int i = 0; i < count; i++) {
         double theta = Math.toRadians(360 / count) * i;
         double x = Math.cos(theta) * radius2;
         double z = Math.sin(theta) * radius2;
         MagicManager.spawnParticles(
            entity.level(),
            particleTypes,
            entity.position().x + x,
            entity.position().y + yHeight,
            entity.position().z + z,
            1,
            0.0,
            0.0,
            0.0,
            particleSpeed,
            false
         );
      }

      for (int i = 0; i < count; i++) {
         double theta = Math.toRadians(360 / count) * i;
         double x = Math.cos(theta) * radius3;
         double z = Math.sin(theta) * radius3;
         MagicManager.spawnParticles(
            entity.level(),
            particleTypes,
            entity.position().x + x,
            entity.position().y + yHeight,
            entity.position().z + z,
            1,
            0.0,
            0.0,
            0.0,
            particleSpeed,
            false
         );
      }
   }

   public static String convertTicksToTime(int ticks) {
      int totalSeconds = ticks / 20;
      int minutes = totalSeconds / 60;
      int seconds = totalSeconds % 60;
      return String.format("%02d:%02d", minutes, seconds);
   }

   public static double getEyeHeight(LivingEntity entity) {
      return entity.getY() + entity.getEyeHeight() - 0.2;
   }

   public static boolean isValidUnlockItemInInventory(Item item, Player player) {
      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack itemStack = player.getInventory().getItem(i);
         if (itemStack.getItem() == item) {
            return true;
         }
      }

      return false;
   }

   public static boolean isValidConsumableUnlockItemInInventory(Item item, Player player) {
      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack itemStack = player.getInventory().getItem(i);
         if (itemStack.getItem() == item) {
            itemStack.shrink(1);
            return true;
         }
      }

      return false;
   }

   public static float getDamageForAttributes(AbstractSpell spell, LivingEntity entity, int spellLevel, Holder<Attribute> attr1, float modifier) {
      double attrValue1 = entity.getAttributeValue(attr1);
      return (float)(modifier * (spell.getSpellPower(spellLevel, entity) + attrValue1));
   }

   public static float getDamageForAttributes(
      AbstractSpell spell, LivingEntity entity, int spellLevel, Holder<Attribute> attr1, Holder<Attribute> attr2, float modifier
   ) {
      double attrValue1 = entity.getAttributeValue(attr1);
      double attrValue2 = entity.getAttributeValue(attr2);
      return (float)(modifier * (spell.getSpellPower(spellLevel, entity) + attrValue1 + attrValue2));
   }

   public static float getDamageForAttributes(
      AbstractSpell spell, LivingEntity entity, int spellLevel, Holder<Attribute> attr1, Holder<Attribute> attr2, Holder<Attribute> attr3, float modifier
   ) {
      double attrValue1 = entity.getAttributeValue(attr1);
      double attrValue2 = entity.getAttributeValue(attr2);
      double attrValue3 = entity.getAttributeValue(attr3);
      return (float)(modifier * (spell.getSpellPower(spellLevel, entity) + attrValue1 + attrValue2 + attrValue3));
   }

   public static boolean isUnderTheSun(Level level, LivingEntity entity) {
      if (level.isDay() && !level.isClientSide) {
         float light = entity.getLightLevelDependentMagicValue();
         BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
         boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;
         if (light > 0.5F && !flag && level.canSeeSky(blockPos)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBossEntity(EntityType<?> entity) {
      return entity.is(ASTags.BOSS_LIKE_ENTITES);
   }

   public static Component rainbowName(String baseName, float speed) {
      MutableComponent rainbowName = Component.literal("");
      Minecraft mc = Minecraft.getInstance();
      long ticks = mc.level != null ? mc.level.getGameTime() : 0L;

      for (int i = 0; i < baseName.length(); i++) {
         float hue = (i * 40 + (float)ticks * speed) % 360.0F / 360.0F;
         int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
         String hex = String.format("#%06X", 16777215 & rgb);
         TextColor color = (TextColor)TextColor.parseColor(hex).getOrThrow();
         rainbowName = rainbowName.append(Component.literal(String.valueOf(baseName.charAt(i))).withStyle(style -> style.withColor(color)));
      }

      return rainbowName;
   }

   public static Vector3f rbgToVector3F(int r, int g, int b) {
      float rf = r / 255.0F;
      float gf = g / 255.0F;
      float bf = b / 255.0F;
      return new Vector3f(rf, gf, bf);
   }

   public static float basicDamageCap(float amount, float min, float max) {
      return Mth.clamp(amount, min, max);
   }

   public static void handleCastingImplementTooltip(ItemStack stack, LocalPlayer player, List<Component> lines, boolean advanced) {
      SelectionOption spellSlot = ClientMagicData.getSpellSelectionManager().getSelection();
      if (spellSlot != null && spellSlot.spellData != SpellData.EMPTY) {
         List<MutableComponent> addLines = TooltipsUtils.formatActiveSpellTooltip(stack, spellSlot.spellData, spellSlot.getCastSource(), player);
         addLines.add(1, Component.translatable("tooltip.irons_spellbooks.casting_implement_tooltip").withStyle(ChatFormatting.GRAY));
         addLines.set(2, Component.literal(" ").append((Component)addLines.get(2)));
         addLines.add(
            Component.literal(" ")
               .append(
                  Component.translatable("tooltip.irons_spellbooks.press_to_cast_active", new Object[]{Component.keybind("key.use")})
                     .withStyle(ChatFormatting.GOLD)
               )
         );
         int i = advanced ? TooltipsUtils.indexOfAdvancedText(lines, stack) : lines.size();
         lines.addAll(i < 0 ? lines.size() : i, addLines);
      }
   }
}
