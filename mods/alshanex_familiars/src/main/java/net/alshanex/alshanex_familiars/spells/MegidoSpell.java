package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightning;
import java.util.List;
import net.alshanex.alshanex_familiars.entity.misc.MegidoEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MegidoSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "megido");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(), 2)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getRange(), 1)})
      );
   }

   public MegidoSpell() {
      this.manaCostPerLevel = 7;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 10;
      this.baseManaCost = 25;
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

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 30, 0.35F);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)world);
         if (targetEntity != null) {
            int numberOfEntities = 8;
            double maxRadius = this.getRadius();
            double minY = targetEntity.getY() + targetEntity.getBbHeight() + 1.0;
            double maxY = targetEntity.getY() + targetEntity.getBbHeight() + 4.0;

            for (int i = 0; i < numberOfEntities; i++) {
               double randomAngle = Math.random() * 2.0 * Math.PI;
               double randomRadius = Math.random() * maxRadius;
               double x = targetEntity.getX() + targetEntity.getBbWidth() / 2.0F + randomRadius * Math.cos(randomAngle);
               double z = targetEntity.getZ() + targetEntity.getBbWidth() / 2.0F + randomRadius * Math.sin(randomAngle);
               double y = minY + Math.random() * (maxY - minY);
               BallLightning ballLightning = new BallLightning(world, entity);
               ballLightning.setPos(new Vec3(x, y, z));
               ballLightning.setDamage(0.0F);
               world.addFreshEntity(ballLightning);
            }

            MegidoEntity chainLightning = new MegidoEntity(world, entity, targetEntity);
            chainLightning.setDamage(this.getDamage(spellLevel, entity));
            chainLightning.range = this.getRange();
            chainLightning.maxConnections = this.getMaxConnections(spellLevel, entity);
            world.addFreshEntity(chainLightning);
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   public float getRadius() {
      return 10.0F;
   }

   public int getMaxConnections(int spellLevel, LivingEntity caster) {
      return Integer.MAX_VALUE;
   }

   public float getRange() {
      return 15.0F;
   }
}
