package com.somake.somakespells.network;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WaterBallExplosionVfxPacket(double x, double y, double z, float radius) implements CustomPacketPayload {
   public static final Type<WaterBallExplosionVfxPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("somakespells", "water_ball_explosion_vfx"));
   public static final StreamCodec<RegistryFriendlyByteBuf, WaterBallExplosionVfxPacket> STREAM_CODEC = CustomPacketPayload.codec(
      WaterBallExplosionVfxPacket::write, WaterBallExplosionVfxPacket::new
   );

   public WaterBallExplosionVfxPacket(Vec3 pos, float radius) {
      this(pos.x, pos.y, pos.z, radius);
   }

   public WaterBallExplosionVfxPacket(RegistryFriendlyByteBuf buffer) {
      this(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat());
   }

   private void write(RegistryFriendlyByteBuf buffer) {
      buffer.writeDouble(this.x);
      buffer.writeDouble(this.y);
      buffer.writeDouble(this.z);
      buffer.writeFloat(this.radius);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(WaterBallExplosionVfxPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         Player player = context.player();
         if (player != null && player.level() != null) {
            spawnExplosion(player.level(), new Vec3(packet.x(), packet.y(), packet.z()), packet.radius());
         }
      });
   }

   private static void spawnExplosion(Level level, Vec3 pos, float radius) {
      SparkParticleOptions sparks = new SparkParticleOptions(0.15F, 0.45F, 0.95F);
      float speed = (0.06F + 0.01F * radius) * 2.15F;
      int sparkDensity = 28 + (int)(14.0F * radius);
      float waterPrimarySpeed = speed * 2.35F;
      float waterSecondarySpeed = 2.1F;
      int waterDensity = 96 + (int)(36.0F * radius);
      double x = pos.x;
      double y = pos.y;
      double z = pos.z;

      for (int i = 0; i < sparkDensity; i += 2) {
         Vec3 posOffset = Utils.getRandomVec3(1.0).scale(radius * 0.28F);
         Vec3 motion = posOffset.normalize().scale(speed * 0.32F);
         motion = motion.add(Utils.getRandomVec3(0.14F));
         level.addParticle(sparks, true, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x, motion.y, motion.z);
      }

      for (int i = 0; i < sparkDensity; i += 2) {
         Vec3 posOffset = Utils.getRandomVec3(radius).scale(0.12F);
         Vec3 motion = posOffset.normalize().scale(0.55);
         motion = motion.add(Utils.getRandomVec3(0.1F));
         level.addParticle(sparks, x + posOffset.x * 0.5, y + posOffset.y * 0.5, z + posOffset.z * 0.5, motion.x, motion.y, motion.z);
      }

      for (int i = 0; i < waterDensity; i += 2) {
         Vec3 posOffset = randomBurstOffset(radius * 0.55F, 0.18F);
         Vec3 motion = posOffset.normalize().scale(waterPrimarySpeed * 0.65F);
         motion = motion.add(Utils.getRandomVec3(0.38F)).add(0.0, 0.08 + Math.random() * 0.1, 0.0);
         level.addParticle(ParticleTypes.SPLASH, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x, motion.y, motion.z);
         if ((i & 3) == 0) {
            level.addParticle(
               ParticleTypes.BUBBLE_POP, x + posOffset.x * 0.8, y + posOffset.y * 0.8, z + posOffset.z * 0.8, motion.x * 0.7, motion.y * 0.7, motion.z * 0.7
            );
         }
      }

      for (int ix = 0; ix < waterDensity; ix += 2) {
         Vec3 posOffset = randomBurstOffset(radius * 0.7F, 0.3F);
         Vec3 motion = posOffset.normalize().scale(waterSecondarySpeed * (1.15 + Math.random() * 0.5));
         motion = motion.add(Utils.getRandomVec3(0.3F)).add(0.0, 0.08 + Math.random() * 0.1, 0.0);
         level.addParticle(ParticleTypes.SPLASH, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x, motion.y, motion.z);
         level.addParticle(ParticleTypes.SPLASH, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x * 0.85, motion.y * 0.85, motion.z * 0.85);
      }

      int centerBurstDensity = 36 + (int)(18.0F * radius);

      for (int ix = 0; ix < centerBurstDensity; ix++) {
         double angle = Math.random() * Math.PI * 2.0;
         Vec3 outward = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Vec3 motion = outward.scale(waterPrimarySpeed * (0.75 + Math.random() * 0.35))
            .add(Utils.getRandomVec3(0.18F))
            .add(0.0, 0.1 + Math.random() * 0.12, 0.0);
         level.addParticle(ParticleTypes.SPLASH, x, y + 0.08 + Math.random() * 0.18, z, motion.x, motion.y, motion.z);
      }
   }

   private static Vec3 randomBurstOffset(float scale, float minLength) {
      Vec3 posOffset = Utils.getRandomVec3(1.0).scale(scale);
      if (posOffset.length() < minLength) {
         double angle = Math.random() * Math.PI * 2.0;
         posOffset = new Vec3(Math.cos(angle), Utils.getRandomScaled(0.15F), Math.sin(angle)).normalize().scale(minLength + Math.random() * scale * 0.35);
      }

      return posOffset;
   }
}
