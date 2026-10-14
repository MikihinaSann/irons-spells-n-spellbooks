package com.gametechbc.traveloptics.spells.nature;

import com.github.L_Ender.cataclysm.entity.projectile.Ancient_Desert_Stele_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class SteleCascadeSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "stele_cascade");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(22.0)
      .build();

   public SteleCascadeSpell() {
      super();
      this.manaCostPerLevel = 25;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 35;
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
      return Optional.of((SoundEvent)ModSounds.REMNANT_TAIL_SWING.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (entity.isCrouching()) {
         this.spawnDesertSteleRing(entity, world, spellLevel);
      } else {
         this.spawnDesertSteleLine(entity, world, spellLevel);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnDesertSteleRing(LivingEntity caster, Level world, int spellLevel) {
      int numRings = this.getRingsAndRows(spellLevel);
      int initialNumTargetsPerRing = 10;
      int ringGrowth = 8;
      int circleRadius = 3;
      int gapBetweenRings = 1;
      int damage = (int)this.getDamage(spellLevel, caster);
      double centerX = caster.getX();
      double centerZ = caster.getZ();
      double initialYOffset = 8.0;
      double yOffsetIncrement = 2.0;

      for (int ring = 0; ring < numRings; ring++) {
         int numTargetsPerRing = initialNumTargetsPerRing + ring * ringGrowth;
         int currentRadius = circleRadius + ring * (gapBetweenRings + 1);
         double yOffset = initialYOffset + ring * yOffsetIncrement;
         double angleOffset = ring % 2 == 0 ? 0.0 : 360.0 / (2 * numTargetsPerRing);

         for (int i = 0; i < numTargetsPerRing; i++) {
            double angle = Math.toRadians(i * 360.0 / numTargetsPerRing + angleOffset);
            double offsetX = currentRadius * Math.cos(angle);
            double offsetZ = currentRadius * Math.sin(angle);
            double spawnX = centerX + offsetX;
            double spawnZ = centerZ + offsetZ;
            double spawnY = caster.getY() + yOffset;
            Vec3 spawnPos = new Vec3(spawnX, spawnY, spawnZ);
            this.spawnDesertSteleAtPosition(spawnPos, world, damage, caster);
         }
      }
   }

   private void spawnDesertSteleLine(LivingEntity caster, Level world, int spellLevel) {
      int numRows = this.getRingsAndRows(spellLevel) * 2;
      int initialNumColumns = 6;
      int rowGrowth = 2;
      int columnSpacing = 2;
      int rowSpacing = 2;
      double yOffsetIncrement = 2.0;
      double initialRowOffset = 3.0;
      int damage = (int)this.getDamage(spellLevel, caster);
      Vec3 lookDirection = new Vec3(caster.getLookAngle().x, 0.0, caster.getLookAngle().z).normalize();
      Vec3 rightDirection = lookDirection.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      double baseX = caster.getX();
      double baseY = caster.getY() + 8.0;
      double baseZ = caster.getZ();

      for (int row = 0; row < numRows; row++) {
         int numColumns = initialNumColumns + row * rowGrowth;
         double halfRowWidth = (numColumns - 1) * columnSpacing / 2.0;
         double rowOffsetX = (initialRowOffset + row * rowSpacing) * lookDirection.x;
         double rowOffsetZ = (initialRowOffset + row * rowSpacing) * lookDirection.z;
         double rowYOffset = baseY + row * yOffsetIncrement;

         for (int column = 0; column < numColumns; column++) {
            double columnOffsetX = (column * columnSpacing - halfRowWidth) * rightDirection.x;
            double columnOffsetZ = (column * columnSpacing - halfRowWidth) * rightDirection.z;
            double spawnX = baseX + rowOffsetX + columnOffsetX;
            double spawnZ = baseZ + rowOffsetZ + columnOffsetZ;
            Vec3 spawnPos = new Vec3(spawnX, rowYOffset, spawnZ);
            this.spawnDesertSteleAtPosition(spawnPos, world, damage, caster);
         }
      }
   }

   private void spawnDesertSteleAtPosition(Vec3 pos, Level world, float damage, LivingEntity caster) {
      Ancient_Desert_Stele_Entity desertStele = new Ancient_Desert_Stele_Entity(world, pos.x, pos.y, pos.z, caster.getYRot(), 10, damage, caster);
      desertStele.setDamage(damage);
      desertStele.setCaster(caster);
      world.addFreshEntity(desertStele);
   }

   private int getRingsAndRows(int spellLevel) {
      return spellLevel;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 4.0F + this.getSpellPower(spellLevel, caster) * 4.0F;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.rings", new Object[]{this.getRingsAndRows(spellLevel)}),
         Component.translatable("ui.traveloptics.row_count", new Object[]{this.getRingsAndRows(spellLevel) * 2}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
