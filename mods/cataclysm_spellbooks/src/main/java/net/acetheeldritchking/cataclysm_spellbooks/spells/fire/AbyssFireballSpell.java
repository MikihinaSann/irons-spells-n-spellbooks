package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Abyss_Fireball_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Fireball_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbyssFireballSpell extends AbstractIgnisSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "abyss_fireball");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(50.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.fireball_details"),
         Component.translatable("ui.cataclysm_spellbooks.recast_count", new Object[]{Utils.stringTruncation(this.recastCount(spellLevel), 2)})
      );
   }

   public AbyssFireballSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 130;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return this.recastCount(spellLevel);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 100, castSource, null), playerMagicData);
      }

      float MAX_HEALTH = entity.getMaxHealth();
      float baseHealth = entity.getHealth();
      double percent = baseHealth / MAX_HEALTH * 100.0F;
      if (percent <= 30.0) {
         this.shootAbyssFireball(entity, level);
      } else if (percent <= 50.0) {
         this.shootFireball(entity, level, true);
      } else {
         this.shootFireball(entity, level, false);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (recastResult == RecastResult.USED_ALL_RECASTS) {
         Level level = serverPlayer.level();
         this.shootAbyssFireball(serverPlayer, level);
      }

      super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
   }

   private void shootAbyssFireball(LivingEntity caster, Level level) {
      Ignis_Abyss_Fireball_Entity fireball = new Ignis_Abyss_Fireball_Entity(level, caster);
      fireball.setPos(caster.position().add(0.0, caster.getEyeHeight() - fireball.getBoundingBox().getYsize() * 0.5, 0.0));
      fireball.shootFromRotation(caster, caster.getXRot(), caster.getYHeadRot(), 0.0F, 1.0F, 1.0F);
      level.addFreshEntity(fireball);
   }

   private void shootFireball(LivingEntity caster, Level level, boolean soul) {
      Ignis_Fireball_Entity fireball = new Ignis_Fireball_Entity(level, caster);
      fireball.setPos(caster.position().add(0.0, caster.getEyeHeight() - fireball.getBoundingBox().getYsize() * 0.5, 0.0));
      fireball.shootFromRotation(caster, caster.getXRot(), caster.getYHeadRot(), 0.0F, 1.0F, 1.0F);
      fireball.setSoul(soul);
      level.addFreshEntity(fireball);
   }

   private int recastCount(int count) {
      return count;
   }
}
