package net.acetheeldritchking.discerning_the_eldritch.items.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber
public class FrostbourneSheathCurio extends SheathCurioItem {
   public static int COOLDOWN = DTEServerConfig.frostbourneSheathCooldown * 20;

   public FrostbourneSheathCurio() {
      super(new Properties().stacksTo(1).rarity((Rarity)ASRarities.GLACIAL_RARITY_PROXY.getValue()).fireResistant(), null);
   }

   protected int getCooldownTicks() {
      return DTEServerConfig.frostbourneSheathCooldown * 20;
   }

   @SubscribeEvent
   public static void handleAbility(LivingIncomingDamageEvent event) {
      FrostbourneSheathCurio sheath = (FrostbourneSheathCurio)ItemRegistries.FROSTBOURNE_SHEATH.get();
      Entity attacker = event.getSource().getEntity();
      if (attacker instanceof ServerPlayer player && sheath.isEquippedBy(player) && sheath.tryProcCooldown(player)) {
         LivingEntity victim = event.getEntity();
         float getBaseDamage = event.getOriginalAmount();
         if (victim instanceof LivingEntity && victim != attacker) {
            if (victim.hasEffect(MobEffectRegistry.CHILLED) || victim.getTicksFrozen() > 20) {
               event.setAmount(getBaseDamage * 1.5F);
               DiscerningTheEldritch.LOGGER.debug("Damage: " + event.getAmount());
            }

            if (victim instanceof Player victimPlayer && !ASUtils.hasCurio(victimPlayer, (Item)ItemRegistry.FROSTWARD_RING.get())) {
               victim.setTicksFrozen(100);
               victim.addEffect(new MobEffectInstance(MobEffectRegistry.CHILLED, 100, 1));
            }
         }
      }
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(AttributeRegistry.ICE_SPELL_POWER, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(AttributeRegistry.ICE_MAGIC_RESIST, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE));
      return attr;
   }
}
