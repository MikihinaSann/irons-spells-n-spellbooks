package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.TunesNTomesConfig;
import net.alshanex.tunes_n_tomes.data.PlayerSoundMarkerData;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.network.SyncSoundMarkerDataPacket;
import net.alshanex.tunes_n_tomes.registry.TAttachmentRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.network.PacketDistributor;

public class SwiftMelodySpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "swift_melody");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(3.0)
      .build();

   public SwiftMelodySpell() {
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 10;
      this.baseManaCost = 20;
      this.manaCostPerLevel = 5;
      this.castTime = 0;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      PlayerSoundMarkerData markerData = (PlayerSoundMarkerData)entity.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      if (!recasts.hasRecastForSpell(this)) {
         HitResult raycast = RaycastBuilder.begin(level, entity).range(this.getDistance(spellLevel, entity)).checkForBlocks(true).bbInflation(0.25F).build();
         Vec3 spawn;
         if (raycast.getType() == Type.ENTITY) {
            spawn = ((EntityHitResult)raycast).getEntity().position();
         } else {
            spawn = raycast.getLocation().subtract(entity.getForward().normalize());
         }

         if (entity instanceof ServerPlayer player) {
            SegnoMarkerEntity newMarker = new SegnoMarkerEntity(level, player);
            newMarker.setPos(spawn);
            level.addFreshEntity(newMarker);
            int maxMarkers = (Integer)TunesNTomesConfig.MAX_SOUND_MARKERS.get();
            UUID removedId = markerData.addMarker(newMarker.getUUID(), maxMarkers);
            if (removedId != null) {
               removeMarkerEntity((ServerLevel)level, removedId);
            }

            syncMarkerData(player);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TSoundRegistry.CLEF.get(), entity.getSoundSource(), 2.0F, 1.0F);
         }

         RecastInstance recastInstance = new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 100, castSource, null);
         recasts.addRecast(recastInstance, playerMagicData);
      } else {
         UUID lastMarkerId = markerData.getMarkerIds().isEmpty() ? null : markerData.getMarkerIds().getLast();
         if (lastMarkerId != null && level instanceof ServerLevel serverLevel) {
            Entity markerEntity = serverLevel.getEntity(lastMarkerId);
            if (markerEntity != null) {
               Vec3 dest = markerEntity.position();
               Vec3 initialPos = entity.position();
               if (entity.isPassenger()) {
                  entity.stopRiding();
               }

               Utils.handleSpellTeleport(this, entity, dest);
               entity.resetFallDistance();
               playerMagicData.resetAdditionalCastData();
               level.playSound(null, initialPos.x, initialPos.y, initialPos.z, (SoundEvent)TSoundRegistry.DAL_SEGNO.get(), entity.getSoundSource(), 2.0F, 1.0F);
               level.playSound(null, dest.x, dest.y, dest.z, (SoundEvent)TSoundRegistry.DAL_SEGNO.get(), entity.getSoundSource(), 2.0F, 1.0F);
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static void removeMarkerEntity(ServerLevel level, UUID markerUUID) {
      for (Entity entity : level.getAllEntities()) {
         if (entity instanceof SegnoMarkerEntity marker && marker.getUUID().equals(markerUUID)) {
            marker.discard();
            return;
         }
      }
   }

   public static void syncMarkerData(ServerPlayer player) {
      PlayerSoundMarkerData data = (PlayerSoundMarkerData)player.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      int maxMarkers = (Integer)TunesNTomesConfig.MAX_SOUND_MARKERS.get();
      PacketDistributor.sendToPlayer(player, new SyncSoundMarkerDataPacket(data.getMarkerCount(), maxMarkers), new CustomPacketPayload[0]);
   }

   private float getDistance(int spellLevel, LivingEntity sourceEntity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(sourceEntity)) * this.getSpellPower(spellLevel, null));
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}));
   }

   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }
}
