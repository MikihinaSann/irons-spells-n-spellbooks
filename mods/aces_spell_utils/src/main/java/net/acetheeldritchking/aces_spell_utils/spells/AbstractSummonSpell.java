package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

public abstract class AbstractSummonSpell extends AbstractSpell {
   public ICastDataSerializable getEmptyCastData() {
      return new SummonedEntitiesCastData();
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
         super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      }
   }

   protected abstract int onSummoningCast(Level var1, int var2, LivingEntity var3, CastSource var4, MagicData var5, SummonedEntitiesCastData var6);

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this)) {
         SummonedEntitiesCastData castData = new SummonedEntitiesCastData();
         int summonTimer = this.onSummoningCast(level, spellLevel, entity, castSource, playerMagicData, castData);
         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTimer, castSource, castData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   protected <K extends Mob> void spawnHelper(
      double x,
      double y,
      double z,
      LivingEntity caster,
      Level level,
      int summonTimer,
      SummonedEntitiesCastData castData,
      Supplier<K> entitySupplier,
      ResourceLocation spellId,
      int spellLevel
   ) {
      K entity = (K)entitySupplier.get();
      entity.moveTo(x, y, z);
      K event = (K)((SpellSummonEvent)NeoForge.EVENT_BUS.post(new SpellSummonEvent(caster, entity, spellId, spellLevel))).getCreature();
      level.addFreshEntity(event);
      SummonManager.initSummon(caster, event, summonTimer, castData);
   }
}
