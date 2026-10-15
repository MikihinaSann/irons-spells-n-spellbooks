package net.hazen.hazennstuff.Spells.Schools.Eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Eldritch.SoulSeeker.SoulSeeker;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulSeekersSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "soul_seekers");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public SoulSeekersSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 0;
      this.castTime = 20;
      this.baseManaCost = 90;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)HnSSounds.BRIMSTONE_HELLBLAST_CAST.get());
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)HnSSounds.BRIMSTONE_CAST.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!world.isClientSide) {
         Vec3 look = entity.getLookAngle();
         Vec3 origin = entity.position().add(0.0, entity.getEyeHeight() - 0.25F, 0.0);
         float spreadAngle = 10.0F;

         for (int i = 0; i < 3; i++) {
            SoulSeeker seeker = new SoulSeeker(world, entity);
            double angleOffset = Math.toRadians((i - 1) * spreadAngle);
            Vec3 direction = look.yRot((float)angleOffset);
            seeker.setPos(origin);
            seeker.shoot(direction);
            seeker.setDamage(this.getDamage(spellLevel, entity));
            world.addFreshEntity(seeker);
         }

         super.onCast(world, spellLevel, entity, castSource, playerMagicData);
      }
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.75F;
   }
}
