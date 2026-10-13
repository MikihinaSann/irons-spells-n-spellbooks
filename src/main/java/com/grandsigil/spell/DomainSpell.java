package com.grandsigil.spell;

import com.grandsigil.entity.FieldEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class DomainSpell extends AbstractSpell {
   public static final float RADIUS = 30.0F;

   protected abstract FieldEntity raise(ServerLevel var1, Vec3 var2, LivingEntity var3, float var4);

   protected abstract List<MutableComponent> describe();

   @Override
   public CastType getCastType() {
      return CastType.LONG;
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      List<MutableComponent> lines = new ArrayList<>();
      lines.add(Component.translatable("ui.grandsigil.field.terrain"));
      lines.addAll(this.describe());
      lines.add(Component.translatable("ui.grandsigil.field.stand"));
      lines.add(Component.translatable("ui.grandsigil.field.clash", Utils.stringTruncation((double)this.getSpellPower(spellLevel, caster), 1)));
      lines.add(Component.translatable("ui.grandsigil.field.duration", Utils.stringTruncation(60.0, 0)));
      lines.add(Component.translatable("ui.grandsigil.field.radius", Utils.stringTruncation(30.0, 0)));
      return lines;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.getEntitiesOfClass(FieldEntity.class, entity.getBoundingBox().inflate(75.0), field -> field.raisedBy(entity)).forEach(Entity::discard);
         Vec3 floor = Aim.beneath(serverLevel, entity);
         serverLevel.addFreshEntity(this.raise(serverLevel, floor, entity, this.getSpellPower(spellLevel, entity)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
