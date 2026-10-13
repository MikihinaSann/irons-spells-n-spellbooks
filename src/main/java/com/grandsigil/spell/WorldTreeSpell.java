package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.TreeEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class WorldTreeSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("world_tree");
   private static final float RANGE = 40.0F;
   private static final float BASE_RADIUS = 36.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(160.0)
      .build();

   public WorldTreeSpell() {
      this.baseManaCost = 500;
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 16;
      this.spellPowerPerLevel = 5;
      this.castTime = 60;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return ID;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.config;
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
   }

   @Override
   public Vector3f getTargetingColor() {
      return new Vector3f(0.3F, 0.95F, 0.4F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 36.0F + 2.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.world_tree.roots", Utils.stringTruncation((double)(power * 0.26F), 1)),
         Component.translatable("ui.grandsigil.world_tree.canopy", Utils.stringTruncation((double)(power * 0.16F), 1)),
         Component.translatable("ui.grandsigil.world_tree.feed", 6),
         Component.translatable(
            "ui.grandsigil.world_tree.bloom", Utils.stringTruncation((double)(power * 1.2F), 1), Utils.stringTruncation((double)(power * 4.2F), 1)
         ),
         Component.translatable("ui.grandsigil.world_tree.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.getEntitiesOfClass(TreeEntity.class, entity.getBoundingBox().inflate(96.0), tree -> tree.plantedBy(entity)).forEach(Entity::discard);
         Vec3 target = Aim.ground(serverLevel, entity, 40.0F);
         serverLevel.addFreshEntity(new TreeEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
