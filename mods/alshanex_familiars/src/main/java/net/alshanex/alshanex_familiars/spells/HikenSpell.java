package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.entity.misc.HikenEntity;
import net.alshanex.alshanex_familiars.util.ASpellAnimations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HikenSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "fire_fist");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster) / 3.0F, 2)}));
   }

   public HikenSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 10;
      this.baseManaCost = 60;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public boolean allowCrafting() {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.FIRE_BOMB_CHARGE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.BLAZE_SHOOT);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 origin = entity.getEyePosition();
      Vec3 forward = entity.getForward();
      double offset = 0.5;
      int numberOfEntities = 3;
      Vec3 right = new Vec3(forward.z, 0.0, -forward.x).normalize();

      for (int i = 0; i < numberOfEntities; i++) {
         HikenEntity fireball = new HikenEntity(world, entity);
         fireball.setDamage(this.getDamage(spellLevel, entity) / 3.0F);
         fireball.setExplosionRadius(this.getRadius(spellLevel, entity));
         fireball.setPos(origin.add(forward.scale(0.5)).add(right.scale(offset * i)).subtract(0.0, fireball.getBbHeight() / 2.0F, 0.0));
         fireball.shoot(entity.getLookAngle());
         fireball.setInfinitePiercing();
         world.addFreshEntity(fireball);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return 5.0F + 5.0F * this.getSpellPower(spellLevel, caster);
   }

   public int getRadius(int spellLevel, LivingEntity caster) {
      return 2 + (int)this.getSpellPower(spellLevel, caster);
   }

   public AnimationHolder getCastStartAnimation() {
      return ASpellAnimations.FIST_START_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return ASpellAnimations.FIST_RELEASE_ANIMATION;
   }
}
