package com.chadate.spellelemental.config;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ServerConfig {
   private static final Builder BUILDER = new Builder();
   public static final IntValue ELEMENT_ATTACHMENT_DEFAULT = BUILDER.comment("没有显式覆盖的法术的默认元素附着量").defineInRange("default", 200, 0, Integer.MAX_VALUE);
   public static final IntValue ELEMENT_ICD_HIT_STEP = BUILDER.comment(new String[]{"ICD 攻击步长：允许在第 1、1+步长、1+2*步长... 次攻击时应用元素", "设置为 3 表示允许第 1、4、7 次攻击等"})
      .defineInRange("icd_hit_step", 3, 1, Integer.MAX_VALUE);
   public static final IntValue ELEMENT_ICD_TIME_TICKS = BUILDER.comment(new String[]{"ICD 时间窗口（游戏刻）：如果上次应用时间超过此窗口则允许应用", "50 刻 = 2.5 秒（20 TPS）"})
      .defineInRange("icd_time_ticks", 50, 0, Integer.MAX_VALUE);
   public static final ConfigValue<String> ELEMENT_ATTACHMENT_OVERRIDES_TEXT = BUILDER.comment(
         new String[]{"单个法术覆盖，格式：modid:spell_id=数量", "多个条目用逗号分隔", "示例：irons_spellbooks:firebolt=250, irons_spellbooks:ice_shard=150", "未知或格式错误的条目将被忽略"}
      )
      .define("overrides", "");
   public static final ConfigValue<String> SPELL_ELEMENT_OVERRIDES_TEXT = BUILDER.comment(
         new String[]{
            "单个法术元素附着覆盖，格式：modid:spell_id=元素ID",
            "允许特定法术附着自定义元素而不是使用基于学派的映射",
            "多个条目用逗号分隔",
            "示例：irons_spellbooks:firebolt=lightning, irons_spellbooks:ice_shard=fire",
            "元素ID应与 element_attachments 数据文件中定义的匹配",
            "未知或格式错误的条目将被忽略"
         }
      )
      .define("spell_element_overrides", "");
   public static final IntValue WEAPON_MAX_ELEMENT_AMOUNT = BUILDER.comment("武器可附着的最大元素量").defineInRange("max_element_amount", 20000, 1, Integer.MAX_VALUE);
   public static final ModConfigSpec SPEC = BUILDER.build();
   private static volatile Map<ResourceLocation, Integer> overridesCache;
   private static volatile Map<ResourceLocation, String> spellElementOverridesCache;

   public static int getSpellAttachmentAmount(ResourceLocation spellId) {
      Map<ResourceLocation, Integer> map = getOverridesCache();
      Integer v = map.get(spellId);
      return v != null ? v : (Integer)ELEMENT_ATTACHMENT_DEFAULT.get();
   }

   private static Map<ResourceLocation, Integer> getOverridesCache() {
      Map<ResourceLocation, Integer> local = overridesCache;
      if (local != null) {
         return local;
      } else {
         synchronized (ServerConfig.class) {
            if (overridesCache == null) {
               overridesCache = parseOverrides((String)ELEMENT_ATTACHMENT_OVERRIDES_TEXT.get());
            }

            return overridesCache;
         }
      }
   }

   public static void invalidateCache() {
      overridesCache = null;
      spellElementOverridesCache = null;
   }

   public static int getIcdHitStep() {
      return (Integer)ELEMENT_ICD_HIT_STEP.get();
   }

   public static int getIcdTimeTicks() {
      return (Integer)ELEMENT_ICD_TIME_TICKS.get();
   }

   public static int getWeaponMaxElementAmount() {
      return (Integer)WEAPON_MAX_ELEMENT_AMOUNT.get();
   }

   private static Map<ResourceLocation, Integer> parseOverrides(String text) {
      Map<ResourceLocation, Integer> map = new HashMap<>();
      if (text != null && !text.isBlank()) {
         String normalized = text.replace('\n', ';').replace(',', ';');
         String[] parts = normalized.split(";");

         for (String raw : parts) {
            if (raw != null) {
               String s = raw.trim();
               if (!s.isEmpty()) {
                  int idx = s.indexOf(61);
                  if (idx > 0 && idx < s.length() - 1) {
                     String key = s.substring(0, idx).trim();
                     String val = s.substring(idx + 1).trim();

                     try {
                        int amount = Integer.parseInt(val);
                        if (amount >= 0) {
                           ResourceLocation id = ResourceLocation.tryParse(key);
                           if (id != null) {
                              map.put(id, amount);
                           }
                        }
                     } catch (NumberFormatException var14) {
                     }
                  }
               }
            }
         }

         return map;
      } else {
         return map;
      }
   }

   public static String getSpellElementOverride(ResourceLocation spellId) {
      Map<ResourceLocation, String> map = getSpellElementOverridesCache();
      return map.get(spellId);
   }

   private static Map<ResourceLocation, String> getSpellElementOverridesCache() {
      Map<ResourceLocation, String> local = spellElementOverridesCache;
      if (local != null) {
         return local;
      } else {
         synchronized (ServerConfig.class) {
            if (spellElementOverridesCache == null) {
               spellElementOverridesCache = parseSpellElementOverrides((String)SPELL_ELEMENT_OVERRIDES_TEXT.get());
            }

            return spellElementOverridesCache;
         }
      }
   }

   private static Map<ResourceLocation, String> parseSpellElementOverrides(String text) {
      Map<ResourceLocation, String> map = new HashMap<>();
      if (text != null && !text.isBlank()) {
         String normalized = text.replace('\n', ';').replace(',', ';');
         String[] parts = normalized.split(";");

         for (String raw : parts) {
            if (raw != null) {
               String s = raw.trim();
               if (!s.isEmpty()) {
                  int idx = s.indexOf(61);
                  if (idx > 0 && idx < s.length() - 1) {
                     String key = s.substring(0, idx).trim();
                     String val = s.substring(idx + 1).trim();
                     if (!val.isEmpty()) {
                        ResourceLocation id = ResourceLocation.tryParse(key);
                        if (id != null) {
                           map.put(id, val.toLowerCase());
                        }
                     }
                  }
               }
            }
         }

         return map;
      } else {
         return map;
      }
   }

   static {
      BUILDER.push("element_attachment");
      BUILDER.pop();
      BUILDER.push("weapon");
      BUILDER.pop();
   }
}
