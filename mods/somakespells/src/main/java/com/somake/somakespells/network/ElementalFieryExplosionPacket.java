package com.somake.somakespells.network;

import com.somake.somakespells.particle.ElementalFierySmokeParticleOptions;
import com.somake.somakespells.registries.ModParticles;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ElementalFieryExplosionPacket(double x, double y, double z, float radius, int elementMode) implements CustomPacketPayload {
   public static final int ELEMENT_FIRE = 0;
   public static final int ELEMENT_SOUL_FIRE = 1;
   public static final int ELEMENT_INFERNAL_FIRE = 2;
   public static final Type<ElementalFieryExplosionPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("somakespells", "elemental_fiery_explosion"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ElementalFieryExplosionPacket> STREAM_CODEC = CustomPacketPayload.codec(
      ElementalFieryExplosionPacket::write, ElementalFieryExplosionPacket::new
   );

   public ElementalFieryExplosionPacket(Vec3 pos, float radius, int elementMode) {
      this(pos.x, pos.y, pos.z, radius, elementMode);
   }

   public ElementalFieryExplosionPacket(RegistryFriendlyByteBuf buffer) {
      this(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat(), buffer.readInt());
   }

   private void write(RegistryFriendlyByteBuf buffer) {
      buffer.writeDouble(this.x);
      buffer.writeDouble(this.y);
      buffer.writeDouble(this.z);
      buffer.writeFloat(this.radius);
      buffer.writeInt(this.elementMode);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ElementalFieryExplosionPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         Player player = context.player();
         if (player != null && player.level() != null) {
            spawnExplosion(player.level(), new Vec3(packet.x(), packet.y(), packet.z()), packet.radius(), packet.elementMode());
         }
      });
   }

   private static void spawnExplosion(Level level, Vec3 pos, float radius, int elementMode) {
      float[] smokeColor = getSmokeColor(elementMode);
      float[] accentColor = getAccentColor(elementMode);
      ParticleOptions innerFlame = getInnerFlameParticle(elementMode);
      ParticleOptions outerFlame = getOuterFlameParticle(elementMode);
      ParticleOptions sparks = new SparkParticleOptions(accentColor[0], accentColor[1], accentColor[2]);
      ParticleOptions ringSmoke = new ElementalFierySmokeParticleOptions(smokeColor[0], smokeColor[1], smokeColor[2], 0.75F);
      ParticleOptions cloudSmoke = new ElementalFierySmokeParticleOptions(smokeColor[0], smokeColor[1], smokeColor[2], 1.0F);
      ParticleOptions coreSmoke = new ElementalFierySmokeParticleOptions(smokeColor[0], smokeColor[1], smokeColor[2], 1.35F);
      double x = pos.x;
      double y = pos.y;
      double z = pos.z;
      int ringCount = Math.max(12, (int)(6.28 * radius) * 2);
      float step = 360.0F / ringCount * (float) (Math.PI / 180.0);
      float speed = (0.06F + 0.01F * radius) * 2.0F;

      for (int i = 0; i < ringCount; i++) {
         Vec3 motion = new Vec3(Math.cos(step * i), 0.0, Math.sin(step * i)).scale(speed);
         Vec3 posOffset = Utils.getRandomVec3(0.5).add(motion.scale(10.0));
         Vec3 finalMotion = motion.add(Utils.getRandomVec3(0.01));
         level.addParticle(
            ringSmoke, x + posOffset.x, y + posOffset.y, z + posOffset.z, finalMotion.x * 0.65, 0.015 + finalMotion.y * 0.35, finalMotion.z * 0.65
         );
         level.addParticle(
            outerFlame,
            x + posOffset.x * 0.18,
            y + posOffset.y * 0.18,
            z + posOffset.z * 0.18,
            finalMotion.x * 0.2,
            0.02 + finalMotion.y * 0.2,
            finalMotion.z * 0.2
         );
      }

      int cloudDensity = 50 + (int)(25.0F * radius);

      for (int i = 0; i < cloudDensity; i++) {
         Vec3 posOffset = Utils.getRandomVec3(1.0).scale(radius * 0.125F);
         Vec3 motion = posOffset.normalize().scale(speed * 0.5F);
         posOffset = posOffset.add(motion.scale(Utils.getRandomScaled(1.0)));
         motion = motion.add(Utils.getRandomVec3(speed * 0.1F));
         level.addParticle(cloudSmoke, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x * 0.5, 0.01 + motion.y * 0.35, motion.z * 0.5);
         if (i % 3 == 0) {
            level.addParticle(innerFlame, x + posOffset.x * 0.55, y + posOffset.y * 0.55, z + posOffset.z * 0.55, motion.x * 0.12, 0.03, motion.z * 0.12);
         }
      }

      for (int ix = 0; ix < cloudDensity; ix += 2) {
         Vec3 posOffset = Utils.getRandomVec3(1.0).scale(radius * 0.4F);
         Vec3 motion = posOffset.normalize().scale(speed * 0.5F).add(Utils.getRandomVec3(0.25));
         level.addParticle(sparks, true, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x, motion.y, motion.z);
         level.addParticle(innerFlame, x + posOffset.x * 0.5, y + posOffset.y * 0.5, z + posOffset.z * 0.5, motion.x * 0.4, motion.y * 0.4, motion.z * 0.4);
      }

      for (int ix = 0; ix < cloudDensity; ix += 2) {
         Vec3 posOffset = Utils.getRandomVec3(radius).scale(0.2F);
         Vec3 motion = posOffset.normalize().scale(0.8).add(Utils.getRandomVec3(0.18));
         level.addParticle(sparks, x + posOffset.x * 0.5, y + posOffset.y * 0.5, z + posOffset.z * 0.5, motion.x, motion.y, motion.z);
      }

      level.addParticle(coreSmoke, x, y + 0.08, z, 0.0, 0.02, 0.0);
      level.addParticle(innerFlame, x, y + 0.08, z, 0.0, 0.02, 0.0);
   }

   private static float[] getSmokeColor(int elementMode) {
      return switch (elementMode) {
         case 1 -> new float[]{0.1F, 0.1F, 0.85F};
         case 2 -> new float[]{0.1F, 0.78F, 0.22F};
         default -> new float[]{1.0F, 0.6F, 0.3F};
      };
   }

   private static float[] getAccentColor(int elementMode) {
      return switch (elementMode) {
         case 1 -> new float[]{0.08F, 0.78F, 1.0F};
         case 2 -> new float[]{0.1F, 0.78F, 0.22F};
         default -> new float[]{1.0F, 0.6F, 0.3F};
      };
   }

   private static ParticleOptions getInnerFlameParticle(int elementMode) {
      return switch (elementMode) {
         case 1 -> (SimpleParticleType)ModParticles.SOUL_FLAME_ANIM_FIRE.get();
         case 2 -> ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FLAME_ANIM_FIRE.get());
         default -> (SimpleParticleType)ModParticles.FLAME_ANIM_FIRE.get();
      };
   }

   private static ParticleOptions getOuterFlameParticle(int elementMode) {
      return switch (elementMode) {
         case 1 -> ParticleTypes.SOUL_FIRE_FLAME;
         case 2 -> ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get());
         default -> ParticleTypes.FLAME;
      };
   }
}
