package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import net.acetheeldritchking.aces_spell_utils.items.example.ExamplePassiveAbilitySpellbook;
import net.acetheeldritchking.aces_spell_utils.registries.ExampleItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class SpellbookAbilities {
   @SubscribeEvent
   public static void exampleAbility(LivingIncomingDamageEvent event) {
      ExamplePassiveAbilitySpellbook sheath = (ExamplePassiveAbilitySpellbook)ExampleItemRegistry.EXAMPLE_PASSIVE_ABILITY_SPELLBOOK.get();
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
}
