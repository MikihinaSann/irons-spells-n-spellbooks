package net.acetheeldritchking.aces_spell_utils.items.example;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.registries.ExampleItemRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber
public class ExampleSheathCurioItem extends SheathCurioItem {
   public static final int COOLDOWN = 100;

   public ExampleSheathCurioItem() {
      super(new Properties().stacksTo(1).rarity((Rarity)ASRarities.COSMIC_RARITY_PROXY.getValue()).fireResistant(), null);
   }

   @Override
   protected int getCooldownTicks() {
      return 100;
   }

   @SubscribeEvent
   public static void handleAbility(LivingIncomingDamageEvent event) {
      ExampleSheathCurioItem sheath = (ExampleSheathCurioItem)ExampleItemRegistry.EXAMPLE_SHEATH.get();
      if (event.getSource().getEntity() instanceof ServerPlayer player && sheath.isEquippedBy(player) && sheath.tryProcCooldown(player)) {
         LivingEntity victim = event.getEntity();
         Comet comet = new Comet(player.level(), player);
         comet.setDamage(5.0F);
         comet.setPos(victim.getX(), victim.getY() + 7.0, victim.getZ());
         Vec3 trajectory = new Vec3(0.05F, -0.85F, 0.0).normalize();
         comet.shoot(trajectory, 0.045F);
         comet.setExplosionRadius(4.5F);
         player.level().addFreshEntity(comet);
      }
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
      attr.put(AttributeRegistry.ENDER_SPELL_POWER, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE));
      attr.put(ASAttributeRegistry.EVASIVE, new AttributeModifier(id, 50.0, Operation.ADD_VALUE));
      attr.put(Attributes.ATTACK_SPEED, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE));
      return attr;
   }
}
