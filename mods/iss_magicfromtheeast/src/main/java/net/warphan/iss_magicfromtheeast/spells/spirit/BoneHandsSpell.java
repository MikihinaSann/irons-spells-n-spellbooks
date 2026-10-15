package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands.BoneHandsEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;

@AutoSpellConfig
public class BoneHandsSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "bone_hands");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(7)
      .setCooldownSeconds(60.0)
      .build();

   public BoneHandsSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 40;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}),
         Component.translatable("ui.iss_magicfromtheeast.hand_attack_range", new Object[]{Utils.stringTruncation(9.0, 1)})
      );
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity caster) {
      return 1 + spellLevel / 3;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 60, castSource, null), playerMagicData);
      }

      Vec3 location = Utils.getTargetBlock(level, entity, Fluid.ANY, 24.0).getLocation();
      BoneHandsEntity boneHandsEntity = new BoneHandsEntity(level, entity, true);
      boneHandsEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getHandsDamage(spellLevel, entity));
      boneHandsEntity.setDuration(this.getDuration(spellLevel, entity));
      boneHandsEntity.setPos(location);
      boneHandsEntity.setOnGround(true);
      level.addFreshEntity(boneHandsEntity);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 240 + 40 * spellLevel;
   }

   private float getHandsDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
