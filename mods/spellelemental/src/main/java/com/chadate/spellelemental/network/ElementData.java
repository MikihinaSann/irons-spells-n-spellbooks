package com.chadate.spellelemental.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ElementData implements CustomPacketPayload {
   static String element;
   public final int entityIdMessage;
   public final String elementMessage;
   public final int durationMessage;
   public static final Type<ElementData> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("spellelemental", "element_data"));
   public static final StreamCodec<FriendlyByteBuf, ElementData> STREAM_CODEC = CustomPacketPayload.codec(ElementData::write, ElementData::new);

   public static String getElement() {
      return element;
   }

   private ElementData(FriendlyByteBuf buf) {
      this.entityIdMessage = buf.readInt();
      this.elementMessage = buf.readUtf();
      this.durationMessage = buf.readInt();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.entityIdMessage);
      buf.writeUtf(this.elementMessage);
      buf.writeInt(this.durationMessage);
   }

   public ElementData(int entityId, String element, int duration) {
      this.entityIdMessage = entityId;
      this.elementMessage = element;
      this.durationMessage = duration;
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public record ElementDebugToggle(boolean enabled) implements CustomPacketPayload {
      public static final Type<ElementData.ElementDebugToggle> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("spellelemental", "element_debug_toggle"));
      public static final StreamCodec<FriendlyByteBuf, ElementData.ElementDebugToggle> STREAM_CODEC = CustomPacketPayload.codec(
         ElementData.ElementDebugToggle::write, ElementData.ElementDebugToggle::new
      );

      private ElementDebugToggle(FriendlyByteBuf buf) {
         this(buf.readBoolean());
      }

      private void write(FriendlyByteBuf buf) {
         buf.writeBoolean(this.enabled);
      }

      @NotNull
      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }
   }

   public record ElementInspectRequest(int entityId) implements CustomPacketPayload {
      public static final Type<ElementData.ElementInspectRequest> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("spellelemental", "element_inspect_req")
      );
      public static final StreamCodec<FriendlyByteBuf, ElementData.ElementInspectRequest> STREAM_CODEC = CustomPacketPayload.codec(
         ElementData.ElementInspectRequest::write, ElementData.ElementInspectRequest::new
      );

      private ElementInspectRequest(FriendlyByteBuf buf) {
         this(buf.readInt());
      }

      private void write(FriendlyByteBuf buf) {
         buf.writeInt(this.entityId);
      }

      @NotNull
      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }
   }

   public static class ElementInspectResponse implements CustomPacketPayload {
      public static final Type<ElementData.ElementInspectResponse> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("spellelemental", "element_inspect_resp")
      );
      public static final StreamCodec<FriendlyByteBuf, ElementData.ElementInspectResponse> STREAM_CODEC = CustomPacketPayload.codec(
         ElementData.ElementInspectResponse::write, ElementData.ElementInspectResponse::new
      );
      public final int entityId;
      public final String[] keys;
      public final int[] values;

      public ElementInspectResponse(int entityId, String[] keys, int[] values) {
         this.entityId = entityId;
         this.keys = keys;
         this.values = values;
      }

      private ElementInspectResponse(FriendlyByteBuf buf) {
         this.entityId = buf.readInt();
         int n = buf.readVarInt();
         this.keys = new String[n];
         this.values = new int[n];

         for (int i = 0; i < n; i++) {
            this.keys[i] = buf.readUtf();
            this.values[i] = buf.readVarInt();
         }
      }

      private void write(FriendlyByteBuf buf) {
         buf.writeInt(this.entityId);
         buf.writeVarInt(this.keys.length);

         for (int i = 0; i < this.keys.length; i++) {
            buf.writeUtf(this.keys[i]);
            buf.writeVarInt(this.values[i]);
         }
      }

      @NotNull
      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }
   }

   public static class ElementSnapshot implements CustomPacketPayload {
      public static final Type<ElementData.ElementSnapshot> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("spellelemental", "element_snapshot"));
      public static final StreamCodec<FriendlyByteBuf, ElementData.ElementSnapshot> STREAM_CODEC = CustomPacketPayload.codec(
         ElementData.ElementSnapshot::write, ElementData.ElementSnapshot::new
      );
      public final int entityId;
      public final String[] keys;
      public final int[] values;

      public ElementSnapshot(int entityId, String[] keys, int[] values) {
         this.entityId = entityId;
         this.keys = keys;
         this.values = values;
      }

      private ElementSnapshot(FriendlyByteBuf buf) {
         this.entityId = buf.readInt();
         int n = buf.readVarInt();
         this.keys = new String[n];
         this.values = new int[n];

         for (int i = 0; i < n; i++) {
            this.keys[i] = buf.readUtf();
            this.values[i] = buf.readVarInt();
         }
      }

      private void write(FriendlyByteBuf buf) {
         buf.writeInt(this.entityId);
         buf.writeVarInt(this.keys.length);

         for (int i = 0; i < this.keys.length; i++) {
            buf.writeUtf(this.keys[i]);
            buf.writeVarInt(this.values[i]);
         }
      }

      @NotNull
      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }
   }
}
