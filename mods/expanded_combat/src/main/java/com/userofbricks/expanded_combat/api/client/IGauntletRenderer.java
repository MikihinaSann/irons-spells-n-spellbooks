package com.userofbricks.expanded_combat.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public interface IGauntletRenderer extends ICurioRenderer {
   void renderFirstPersonArm(ItemStack var1, PoseStack var2, MultiBufferSource var3, int var4, AbstractClientPlayer var5, HumanoidArm var6, boolean var7);
}
