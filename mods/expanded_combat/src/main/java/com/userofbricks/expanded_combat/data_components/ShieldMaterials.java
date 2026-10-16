package com.userofbricks.expanded_combat.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.datagen.LangStrings;
import com.userofbricks.expanded_combat.init.ECBasePlugin;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipProvider;

@ParametersAreNonnullByDefault
public record ShieldMaterials(Material ULMaterial, Material URMaterial, Material DLMaterial, Material DRMaterial, Material MMaterial, int lastRepairNumber)
   implements TooltipProvider {
   public static final Codec<ShieldMaterials> CODEC = RecordCodecBuilder.create(
      shieldMaterialsInstance -> shieldMaterialsInstance.group(
            Material.CODEC.fieldOf("ul_material").forGetter(ShieldMaterials::getULMaterial),
            Material.CODEC.fieldOf("ur_material").forGetter(ShieldMaterials::getURMaterial),
            Material.CODEC.fieldOf("dl_material").forGetter(ShieldMaterials::getDLMaterial),
            Material.CODEC.fieldOf("dr_material").forGetter(ShieldMaterials::getDRMaterial),
            Material.CODEC.fieldOf("m_material").forGetter(ShieldMaterials::getMMaterial),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("last_repair_number").forGetter(ShieldMaterials::getLastRepairNumber)
         )
         .apply(shieldMaterialsInstance, ShieldMaterials::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ShieldMaterials> STREAM_CODEC = StreamCodec.composite(
      Material.STREAM_CODEC,
      ShieldMaterials::getULMaterial,
      Material.STREAM_CODEC,
      ShieldMaterials::getURMaterial,
      Material.STREAM_CODEC,
      ShieldMaterials::getDLMaterial,
      Material.STREAM_CODEC,
      ShieldMaterials::getDRMaterial,
      Material.STREAM_CODEC,
      ShieldMaterials::getMMaterial,
      ByteBufCodecs.INT,
      ShieldMaterials::getLastRepairNumber,
      ShieldMaterials::new
   );
   public static final ShieldMaterials DEFAULT = new ShieldMaterials(
      ECBasePlugin.WOOD_PLANK, ECBasePlugin.WOOD_PLANK, ECBasePlugin.WOOD_PLANK, ECBasePlugin.WOOD_PLANK, ECBasePlugin.IRON, 0
   );

   public void addToTooltip(TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
      String ul = this.ULMaterial.id().toString();
      String ur = this.URMaterial.id().toString();
      String dl = this.DLMaterial.id().toString();
      String dr = this.DRMaterial.id().toString();
      String m = this.MMaterial.id().toString();
      pTooltipAdder.accept(
         Component.translatable(LangStrings.UPPER_LEFT_MATERIAL)
            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            .append(
               Component.translatable("tooltip.expanded_combat.shield_material." + ul)
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            )
      );
      pTooltipAdder.accept(
         Component.translatable(LangStrings.UPPER_RIGHT_MATERIAL)
            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            .append(
               Component.translatable("tooltip.expanded_combat.shield_material." + ur)
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            )
      );
      pTooltipAdder.accept(
         Component.translatable(LangStrings.CENTER_MATERIAL)
            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            .append(
               Component.translatable("tooltip.expanded_combat.shield_material." + m)
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            )
      );
      pTooltipAdder.accept(
         Component.translatable(LangStrings.LOWER_LEFT_MATERIAL)
            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            .append(
               Component.translatable("tooltip.expanded_combat.shield_material." + dl)
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            )
      );
      pTooltipAdder.accept(
         Component.translatable(LangStrings.LOWER_RIGHT_MATERIAL)
            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            .append(
               Component.translatable("tooltip.expanded_combat.shield_material." + dr)
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
            )
      );
   }

   public Material getULMaterial() {
      return this.ULMaterial;
   }

   public Material getURMaterial() {
      return this.URMaterial;
   }

   public Material getDLMaterial() {
      return this.DLMaterial;
   }

   public Material getDRMaterial() {
      return this.DRMaterial;
   }

   public Material getMMaterial() {
      return this.MMaterial;
   }

   public int getLastRepairNumber() {
      return this.lastRepairNumber;
   }

   public ShieldMaterials updateLastRepair(int lastRepairNumber) {
      return new ShieldMaterials(this.ULMaterial, this.URMaterial, this.DLMaterial, this.DRMaterial, this.MMaterial, lastRepairNumber);
   }

   public boolean canReplaceUL(Material replacement) {
      List<ResourceLocation> canReplace = replacement.crafting().onlyReplaceResource();
      return canReplace.isEmpty() ? true : canReplace.contains(this.ULMaterial.id());
   }

   public boolean canReplaceUR(Material replacement) {
      List<ResourceLocation> canReplace = replacement.crafting().onlyReplaceResource();
      return canReplace.isEmpty() ? true : canReplace.contains(this.URMaterial.id());
   }

   public boolean canReplaceDL(Material replacement) {
      List<ResourceLocation> canReplace = replacement.crafting().onlyReplaceResource();
      return canReplace.isEmpty() ? true : canReplace.contains(this.DLMaterial.id());
   }

   public boolean canReplaceDR(Material replacement) {
      List<ResourceLocation> canReplace = replacement.crafting().onlyReplaceResource();
      return canReplace.isEmpty() ? true : canReplace.contains(this.DRMaterial.id());
   }

   public boolean canReplaceM(Material replacement) {
      List<ResourceLocation> canReplace = replacement.crafting().onlyReplaceResource();
      return canReplace.isEmpty() ? true : canReplace.contains(this.MMaterial.id());
   }
}
