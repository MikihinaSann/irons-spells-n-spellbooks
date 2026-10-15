package com.chadate.spellelemental.element.attachment.data;

import com.chadate.spellelemental.SpellElemental;
import com.chadate.spellelemental.element.attachment.config.UnifiedElementAttachmentConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public class UnifiedElementAttachmentDataLoader extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new Gson();

   public UnifiedElementAttachmentDataLoader() {
      super(GSON, "element_attachments");
   }

   protected void apply(Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
      UnifiedElementAttachmentAssets.clear();
      SpellElemental.LOGGER.info("开始加载元素附着资产配置(最小化)...");
      int loadedCount = 0;
      int errorCount = 0;

      for (Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
         ResourceLocation id = entry.getKey();
         JsonElement json = entry.getValue();

         try {
            UnifiedElementAttachmentConfig config = (UnifiedElementAttachmentConfig)GSON.fromJson(json, UnifiedElementAttachmentConfig.class);
            if (config != null && config.getElementId() != null && !config.getElementId().isBlank()) {
               String elementId = config.getElementId();
               if (config.getVisual() != null) {
                  if (config.getVisual().getIcon() != null && !config.getVisual().getIcon().isBlank()) {
                     UnifiedElementAttachmentAssets.setIcon(elementId, config.getVisual().getIcon());
                  }

                  if (config.getVisual().getParticleEffect() != null && !config.getVisual().getParticleEffect().isBlank()) {
                     UnifiedElementAttachmentAssets.setParticleEffect(elementId, config.getVisual().getParticleEffect());
                  }
               }

               if (config.getSchool() != null && !config.getSchool().isBlank()) {
                  UnifiedElementAttachmentAssets.setSchool(elementId, config.getSchool());
               }

               loadedCount++;
            } else {
               SpellElemental.LOGGER.error("元素附着配置缺少 element_id: {}", id);
               errorCount++;
            }
         } catch (JsonParseException var12) {
            SpellElemental.LOGGER.error("解析元素附着配置失败: {} - {}", id, var12.getMessage());
            errorCount++;
         } catch (Exception var13) {
            SpellElemental.LOGGER.error("加载元素附着配置时发生错误: {} - {}", new Object[]{id, var13.getMessage(), var13});
            errorCount++;
         }
      }

      SpellElemental.LOGGER.info("元素附着资产加载完成: {} 条成功, {} 条错误", loadedCount, errorCount);
   }
}
