package net.hazen.hazennstuff.Item.Curios.Sheaths.GalvanizedSheath;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.hazen.hazennstuff.HnSConfig;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Entity.Spells.Lightning.InstantLightningStrike.LightningStrike;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber
public class GalvanizedSheath extends SheathCurioItem {
   public final HnSDispatcher dispatcher = new HnSDispatcher();
   public static int COOLDOWN = HnSConfig.galvanizedSheathCooldown * 20;

   public GalvanizedSheath() {
      super(new Properties().stacksTo(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant(), null);
   }

   protected int getCooldownTicks() {
      return HnSConfig.galvanizedSheathCooldown * 20;
   }

   @SubscribeEvent
   public static void handleAbility(LivingIncomingDamageEvent event) {
      GalvanizedSheath sheath = (GalvanizedSheath)HnSItemRegistry.GALVANIZED_SHEATH.get();
      Entity attacker = event.getSource().getEntity();
      if (attacker instanceof ServerPlayer player && sheath.isEquippedBy(player) && sheath.tryProcCooldown(player)) {
         LivingEntity victim = event.getEntity();
         if (victim != attacker) {
            float getBaseDamage = event.getOriginalAmount();
            if (victim instanceof LivingEntity && victim != attacker) {
               if (victim.hasEffect(HnSEffects.ELECTROCUTED)) {
                  event.setAmount(getBaseDamage * 1.5F);
               }

               victim.addEffect(new MobEffectInstance(HnSEffects.ELECTROCUTED, 100, 0));
               spawnLightningLine(player.level(), player, victim);
            }
         }
      }
   }

   private static void spawnLightningLine(Level level, LivingEntity attacker, LivingEntity victim) {
      if (!level.isClientSide) {
         Vec3 start = attacker.position().add(0.0, attacker.getBbHeight() * 0.5, 0.0);
         Vec3 end = victim.position().add(0.0, victim.getBbHeight() * 0.5, 0.0);
         Vec3 direction = end.subtract(start).normalize();
         double extraDistance = 16.0;
         double totalDistance = start.distanceTo(end) + extraDistance;
         int maxStrikes = 3;
         double stepDistance = totalDistance / maxStrikes;
         Vec3 step = direction.scale(stepDistance);
         int delayPerStrike = 1;

         for (int i = 0; i < maxStrikes; i++) {
            Vec3 pos = start.add(step.scale(i)).with(Axis.Y, start.y);
            LightningStrike strike = new LightningStrike(level);
            strike.setPos(pos.x, pos.y, pos.z);
            strike.setOwner(attacker);
            strike.setDamage(3.0F);
            strike.setWaitTime(i * delayPerStrike);
            level.addFreshEntity(strike);
         }
      }
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(AttributeRegistry.LIGHTNING_SPELL_POWER, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(AttributeRegistry.LIGHTNING_MAGIC_RESIST, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE));
      return attr;
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && entity instanceof Player player) {
         this.dispatcher.idle(player, stack);
      }
   }
}
