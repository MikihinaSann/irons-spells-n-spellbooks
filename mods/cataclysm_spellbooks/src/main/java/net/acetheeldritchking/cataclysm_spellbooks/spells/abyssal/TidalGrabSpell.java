package net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal;

import com.github.L_Ender.cataclysm.Attachment.TidalTentacleAttachment;
import com.github.L_Ender.cataclysm.entity.projectile.Tidal_Tentacle_Entity;
import com.github.L_Ender.cataclysm.entity.util.TidalTentacleUtil;
import com.github.L_Ender.cataclysm.init.ModDataAttachments;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class TidalGrabSpell extends AbstractAbyssalSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "tidal_grab");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(CSSchoolRegistry.ABYSSAL_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(50.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.range", new Object[]{Utils.stringTruncation(this.getRange(spellLevel, caster) / 10.0F, 2)})
      );
   }

   public TidalGrabSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 5;
      this.castTime = 100;
      this.baseManaCost = 10;
   }

   public boolean canBeCraftedBy(Player player) {
      Item tidalClaws = (Item)ModItems.TIDAL_CLAWS.get();
      return player.getMainHandItem().is(tidalClaws);
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.TIDAL_TENTACLE.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, (int)this.getRange(spellLevel, entity) / 10, 0.15F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
         LivingEntity targetEntity = targetEntityCastData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            Entity validTarget = null;
            HitResult hitResult = Utils.raycastForEntity(level, entity, this.getRange(spellLevel, entity) / 10.0F, true, 0.25F);
            if (hitResult.getType() == Type.ENTITY) {
               Entity target = ((EntityHitResult)hitResult).getEntity();
               if (target.equals(entity)
                  || entity.isAlliedTo(target)
                  || target.isAlliedTo(entity)
                  || !(target instanceof Mob)
                  || !entity.hasLineOfSight(target)) {
                  for (Entity e : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(10.0))) {
                     if (!e.equals(entity)
                        && !entity.isAlliedTo(e)
                        && !e.isAlliedTo(entity)
                        && e instanceof Mob
                        && entity.hasLineOfSight(e)
                        && (validTarget == null || entity.distanceTo(e) < entity.distanceTo(validTarget))) {
                        validTarget = e;
                     }
                  }

                  this.launchTentacle(entity, (LivingEntity)validTarget, level);
                  return;
               }

               this.launchTentacle(entity, (LivingEntity)target, level);
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void launchTentacle(LivingEntity caster, LivingEntity target, Level level) {
      TidalTentacleAttachment attachment = (TidalTentacleAttachment)caster.getData(ModDataAttachments.TIDAL_TENTACLE_ATTACHMENT);
      if (!attachment.hasTentacle() && TidalTentacleUtil.canLaunchTentacles(level, caster) && !level.isClientSide && target != null) {
         Tidal_Tentacle_Entity tentacle = (Tidal_Tentacle_Entity)((EntityType)ModEntities.TIDAL_TENTACLE.get()).create(level);

         assert tentacle != null;

         tentacle.copyPosition(caster);
         level.addFreshEntity(tentacle);
         tentacle.setCreatorEntityUUID(caster.getUUID());
         tentacle.setFromEntityID(caster.getId());
         tentacle.setToEntityID(target.getId());
         tentacle.copyPosition(caster);
         tentacle.setProgress(0.0F);
         TidalTentacleUtil.setLastTentacle(caster, tentacle);
      }
   }

   private float getRange(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * (2 * spellLevel);
   }
}
