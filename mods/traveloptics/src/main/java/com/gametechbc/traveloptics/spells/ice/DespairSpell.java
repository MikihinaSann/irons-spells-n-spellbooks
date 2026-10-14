package com.gametechbc.traveloptics.spells.ice;

import com.gametechbc.traveloptics.api.utils.CurioUtils;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.projectile.Axe_Blade_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class DespairSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "despair");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(18.0)
      .build();

   public DespairSpell() {
      super();
      this.manaCostPerLevel = 18;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 18;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.DESPAIR_SLASH;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.MALEDICTUS_MACE_SWING.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.recast", new Object[]{Utils.stringTruncation(this.getRecastCount(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 100, castSource, null), playerMagicData);
      }

      Vec3 lookVector = entity.getLookAngle();
      boolean hasRingEquipped = CurioUtils.getWearingCurio(entity, (Item)TOItems.AETHERIAL_DESPAIR_RING.get());
      double speedMultiplier = hasRingEquipped ? 3.0 : 2.0;
      if (!hasRingEquipped) {
         lookVector = new Vec3(lookVector.x, 0.0, lookVector.z).normalize();
      }

      float damage = this.getDamage(spellLevel, entity);
      Axe_Blade_Entity axeBlade = new Axe_Blade_Entity(entity, lookVector.x, lookVector.y, lookVector.z, level, damage, entity.getYRot());
      axeBlade.setDeltaMovement(lookVector.scale(speedMultiplier));
      level.addFreshEntity(axeBlade);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 3.0F + this.getSpellPower(spellLevel, caster) * 1.5F;
   }

   public int getRecastCount(int spellLevel, LivingEntity entity) {
      return 2 + spellLevel / 2;
   }
}
