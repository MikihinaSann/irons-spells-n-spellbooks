package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.CalderaEntity;
import com.grandsigil.entity.FieldEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CalderaSpell extends DomainSpell {
   public static final ResourceLocation ID = GrandSigil.id("caldera");
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(130.0)
      .build();

   public CalderaSpell() {
      this.baseManaCost = 420;
      this.manaCostPerLevel = 90;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 8;
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
   public Vector3f getTargetingColor() {
      return new Vector3f(1.0F, 0.42F, 0.1F);
   }

   @Override
   protected FieldEntity raise(ServerLevel level, Vec3 floor, LivingEntity caster, float power) {
      return new CalderaEntity(level, floor, caster, power, 30.0F);
   }

   @Override
   protected List<MutableComponent> describe() {
      return List.of(Component.translatable("ui.grandsigil.caldera.ally"), Component.translatable("ui.grandsigil.caldera.foe"));
   }
}
