package com.chadate.spellelemental.element.reaction.data;

import com.chadate.spellelemental.SpellElemental;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class ElementReactionDataLoader extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new Gson();

   public ElementReactionDataLoader() {
      super(GSON, "element_reactions");
   }

   protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
      ElementReactionRegistry.clear();
      int ok = 0;
      int err = 0;
      SpellElemental.LOGGER.info("开始加载元素反应 JSON，共 {} 项", object.size());

      for (Entry<ResourceLocation, JsonElement> e : object.entrySet()) {
         ResourceLocation id = e.getKey();

         try {
            JsonObject root = e.getValue().getAsJsonObject();
            String reactionId = root.has("reaction_id") && root.get("reaction_id").isJsonPrimitive() ? root.get("reaction_id").getAsString() : null;
            String triggerType = root.has("trigger_type") && root.get("trigger_type").isJsonPrimitive() ? root.get("trigger_type").getAsString() : null;
            String trigger = triggerType == null ? "damage" : triggerType.trim().toLowerCase();
            boolean consumeElements = true;
            if (root.has("consume_elements") && root.get("consume_elements").isJsonPrimitive()) {
               try {
                  consumeElements = root.get("consume_elements").getAsBoolean();
               } catch (Exception var38) {
               }
            }

            if (reactionId == null || reactionId.isBlank()) {
               SpellElemental.LOGGER.error("元素反应配置缺少 reaction_id: {}", id);
               err++;
            } else if (!"tick".equals(trigger)) {
               ElementReactionRegistry.add(reactionId.trim(), trigger);
               if ("damage".equals(trigger)) {
                  ElementReactionRegistry.setConsumeFlag(reactionId.trim(), consumeElements);
                  if (root.has("ordered") && root.get("ordered").isJsonArray()) {
                     for (JsonElement el : root.getAsJsonArray("ordered")) {
                        if (el.isJsonArray()) {
                           JsonArray pair = el.getAsJsonArray();
                           if (pair.size() >= 2 && pair.get(0).isJsonPrimitive() && pair.get(1).isJsonPrimitive()) {
                              ElementReactionRegistry.indexDamageCombination(
                                 reactionId.trim(), List.of(pair.get(0).getAsString(), pair.get(1).getAsString()), false
                              );
                           }
                        } else if (el.isJsonObject()) {
                           JsonObject obj = el.getAsJsonObject();
                           String src = obj.has("source") ? obj.get("source").getAsString() : null;
                           String tgt = obj.has("target") ? obj.get("target").getAsString() : null;
                           double ratio = 1.0;
                           if (obj.has("ratio") && obj.get("ratio").isJsonPrimitive()) {
                              try {
                                 ratio = Math.max(0.0, obj.get("ratio").getAsDouble());
                              } catch (Exception var34) {
                                 ratio = 1.0;
                              }
                           }

                           if (src != null && tgt != null) {
                              ElementReactionRegistry.indexDamageOrdered(reactionId.trim(), src, tgt);
                              if (ratio != 1.0) {
                                 ElementReactionRegistry.setConsumeRatio(src, tgt, ratio);
                              }

                              if (obj.has("effects")) {
                                 if (obj.get("effects").isJsonArray()) {
                                    for (JsonElement de : obj.getAsJsonArray("effects")) {
                                       if (de.isJsonObject()) {
                                          JsonObject eff = de.getAsJsonObject();
                                          String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                                          float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive()
                                             ? eff.get("multiplier").getAsFloat()
                                             : 1.0F;
                                          String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                                          float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                                          String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                             ? eff.get("damage_type").getAsString()
                                             : "";
                                          boolean damageAttacker = eff.has("damage_attacker")
                                             && eff.get("damage_attacker").isJsonPrimitive()
                                             && eff.get("damage_attacker").getAsBoolean();
                                          boolean damageVictim = !eff.has("damage_victim")
                                             || eff.get("damage_victim").isJsonPrimitive() && eff.get("damage_victim").getAsBoolean();
                                          ElementReactionRegistry.addDirectionalEffect(
                                             src,
                                             tgt,
                                             !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && damageVictim
                                                ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                                : new ElementReactionRegistry.ReactionEffect(
                                                   type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim
                                                )
                                          );
                                       }
                                    }
                                 } else if (obj.get("effects").isJsonObject()) {
                                    JsonObject grouped = obj.getAsJsonObject("effects");
                                    if (grouped.has("damage") && grouped.get("damage").isJsonArray()) {
                                       for (JsonElement dex : grouped.getAsJsonArray("damage")) {
                                          if (dex.isJsonObject()) {
                                             JsonObject eff = dex.getAsJsonObject();
                                             String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                                             float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive()
                                                ? eff.get("multiplier").getAsFloat()
                                                : 1.0F;
                                             String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive()
                                                ? eff.get("formula").getAsString()
                                                : "";
                                             float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                                             String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                                ? eff.get("damage_type").getAsString()
                                                : "";
                                             boolean damageAttacker = eff.has("damage_attacker")
                                                && eff.get("damage_attacker").isJsonPrimitive()
                                                && eff.get("damage_attacker").getAsBoolean();
                                             boolean damageVictim = !eff.has("damage_victim")
                                                || eff.get("damage_victim").isJsonPrimitive() && eff.get("damage_victim").getAsBoolean();
                                             ElementReactionRegistry.addDirectionalEffect(
                                                src,
                                                tgt,
                                                !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && damageVictim
                                                   ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                                   : new ElementReactionRegistry.ReactionEffect(
                                                      type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim
                                                   )
                                             );
                                          }
                                       }
                                    }

                                    if (grouped.has("attribute") && grouped.get("attribute").isJsonArray()) {
                                       for (JsonElement ae : grouped.getAsJsonArray("attribute")) {
                                          if (ae.isJsonObject()) {
                                             JsonObject aobj = ae.getAsJsonObject();
                                             String attrId = aobj.has("attribute_id") && aobj.get("attribute_id").isJsonPrimitive()
                                                ? aobj.get("attribute_id").getAsString()
                                                : "";
                                             String op = aobj.has("type") && aobj.get("type").isJsonPrimitive() ? aobj.get("type").getAsString() : "add";
                                             double val = aobj.has("value") && aobj.get("value").isJsonPrimitive() ? aobj.get("value").getAsDouble() : 0.0;
                                             int dur = aobj.has("duration") && aobj.get("duration").isJsonPrimitive()
                                                ? Math.max(0, aobj.get("duration").getAsInt())
                                                : 0;
                                             ElementReactionRegistry.addDirectionalAttributeEffect(
                                                src, tgt, new ElementReactionRegistry.AttributeEffect(attrId, op, val, dur)
                                             );
                                          }
                                       }
                                    }

                                    if (grouped.has("attachment") && grouped.get("attachment").isJsonArray()) {
                                       for (JsonElement aex : grouped.getAsJsonArray("attachment")) {
                                          if (aex.isJsonObject()) {
                                             JsonObject aobj = aex.getAsJsonObject();
                                             String elementId = aobj.has("element_id") && aobj.get("element_id").isJsonPrimitive()
                                                ? aobj.get("element_id").getAsString()
                                                : null;
                                             int amount = aobj.has("amount") && aobj.get("amount").isJsonPrimitive()
                                                ? Math.max(0, aobj.get("amount").getAsInt())
                                                : 0;
                                             float chance = aobj.has("chance") && aobj.get("chance").isJsonPrimitive() ? aobj.get("chance").getAsFloat() : 1.0F;
                                             int duration = aobj.has("duration") && aobj.get("duration").isJsonPrimitive()
                                                ? Math.max(0, aobj.get("duration").getAsInt())
                                                : 0;
                                             if (elementId != null && !elementId.isBlank() && amount > 0) {
                                                ElementReactionRegistry.addDirectionalEffect(
                                                   src, tgt, new ElementReactionRegistry.ReactionEffect("attachment", elementId, amount, chance, duration)
                                                );
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  } else if (root.has("elements") && root.get("elements").isJsonArray()) {
                     JsonArray elx = root.getAsJsonArray("elements");
                     if (elx.size() >= 2 && elx.get(0).isJsonPrimitive() && elx.get(1).isJsonPrimitive()) {
                        ElementReactionRegistry.indexDamageCombination(reactionId.trim(), List.of(elx.get(0).getAsString(), elx.get(1).getAsString()), true);
                     } else {
                        SpellElemental.LOGGER.warn("元素反应缺少有效的 elements 字段: {}", id);
                     }
                  } else {
                     SpellElemental.LOGGER.warn("元素反应缺少有效的 ordered/elements 组合字段: {}", id);
                  }
               }

               if (!"tick".equals(trigger) && root.has("effects")) {
                  if (root.get("effects").isJsonArray()) {
                     for (JsonElement effEl : root.getAsJsonArray("effects")) {
                        if (effEl.isJsonObject()) {
                           JsonObject eff = effEl.getAsJsonObject();
                           String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                           float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive() ? eff.get("multiplier").getAsFloat() : 1.0F;
                           String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                           float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                           String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                              ? eff.get("damage_type").getAsString()
                              : "";
                           ElementReactionRegistry.addEffect(
                              reactionId.trim(),
                              !(radius > 0.0F) && damageTypeStr.isEmpty()
                                 ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                 : new ElementReactionRegistry.ReactionEffect(type, multiplier, formula, radius, damageTypeStr)
                           );
                        }
                     }
                  } else if (root.get("effects").isJsonObject()) {
                     JsonObject groupedx = root.getAsJsonObject("effects");
                     if (groupedx.has("damage") && groupedx.get("damage").isJsonArray()) {
                        for (JsonElement effElx : groupedx.getAsJsonArray("damage")) {
                           if (effElx.isJsonObject()) {
                              JsonObject eff = effElx.getAsJsonObject();
                              String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                              float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive() ? eff.get("multiplier").getAsFloat() : 1.0F;
                              String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                              float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                              String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                 ? eff.get("damage_type").getAsString()
                                 : "";
                              ElementReactionRegistry.addEffect(
                                 reactionId.trim(),
                                 !(radius > 0.0F) && damageTypeStr.isEmpty()
                                    ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                    : new ElementReactionRegistry.ReactionEffect(type, multiplier, formula, radius, damageTypeStr)
                              );
                           }
                        }
                     }

                     if (groupedx.has("attribute") && groupedx.get("attribute").isJsonArray()) {
                        for (JsonElement aexx : groupedx.getAsJsonArray("attribute")) {
                           if (aexx.isJsonObject()) {
                              JsonObject aobj = aexx.getAsJsonObject();
                              String attrId = aobj.has("attribute_id") && aobj.get("attribute_id").isJsonPrimitive()
                                 ? aobj.get("attribute_id").getAsString()
                                 : "";
                              String op = aobj.has("type") && aobj.get("type").isJsonPrimitive() ? aobj.get("type").getAsString() : "add";
                              double val = aobj.has("value") && aobj.get("value").isJsonPrimitive() ? aobj.get("value").getAsDouble() : 0.0;
                              int dur = aobj.has("duration") && aobj.get("duration").isJsonPrimitive() ? Math.max(0, aobj.get("duration").getAsInt()) : 0;
                              ElementReactionRegistry.addAttributeEffect(reactionId.trim(), new ElementReactionRegistry.AttributeEffect(attrId, op, val, dur));
                           }
                        }
                     }
                  }
               }

               ok++;
            } else if (root.has("ordered")) {
               SpellElemental.LOGGER.error("[元素反应: {}] tick 类型不支持方向化配置(ordered)", reactionId);
               err++;
            } else if (root.has("variants") && root.get("variants").isJsonArray()) {
               for (JsonElement varEl : root.getAsJsonArray("variants")) {
                  if (varEl.isJsonObject()) {
                     JsonObject var = varEl.getAsJsonObject();
                     Map<String, Integer> reqMap = new HashMap<>();
                     if (var.has("requirements") && var.get("requirements").isJsonObject()) {
                        JsonObject req = var.getAsJsonObject("requirements");

                        for (Entry<String, JsonElement> en : req.entrySet()) {
                           if (en.getValue().isJsonPrimitive()) {
                              reqMap.put(en.getKey(), Math.max(0, en.getValue().getAsInt()));
                           }
                        }
                     }

                     Map<String, Integer> conMap = new HashMap<>();
                     if (var.has("consume") && var.get("consume").isJsonObject()) {
                        JsonObject con = var.getAsJsonObject("consume");

                        for (Entry<String, JsonElement> enx : con.entrySet()) {
                           if (enx.getValue().isJsonPrimitive()) {
                              conMap.put(enx.getKey(), Math.max(0, enx.getValue().getAsInt()));
                           }
                        }
                     }

                     int interval = 1;
                     if (var.has("interval") && var.get("interval").isJsonPrimitive()) {
                        try {
                           interval = Math.max(1, var.get("interval").getAsInt());
                        } catch (Exception var37) {
                           interval = 1;
                        }
                     } else if (root.has("interval") && root.get("interval").isJsonPrimitive()) {
                        try {
                           interval = Math.max(1, root.get("interval").getAsInt());
                        } catch (Exception var36) {
                           interval = 1;
                        }
                     }

                     List<ElementReactionRegistry.ReactionEffect> tickEffects = new ArrayList<>();
                     if (var.has("effects")) {
                        if (var.get("effects").isJsonArray()) {
                           for (JsonElement effElxx : var.getAsJsonArray("effects")) {
                              if (effElxx.isJsonObject()) {
                                 JsonObject eff = effElxx.getAsJsonObject();
                                 String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                                 if (!"attachment".equalsIgnoreCase(type) && !"consume".equalsIgnoreCase(type)) {
                                    float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive()
                                       ? eff.get("multiplier").getAsFloat()
                                       : 1.0F;
                                    String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                                    float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                                    String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                       ? eff.get("damage_type").getAsString()
                                       : "";
                                    boolean damageAttacker = eff.has("damage_attacker")
                                       && eff.get("damage_attacker").isJsonPrimitive()
                                       && eff.get("damage_attacker").getAsBoolean();
                                    boolean damageVictim = eff.has("damage_victim")
                                       && eff.get("damage_victim").isJsonPrimitive()
                                       && eff.get("damage_victim").getAsBoolean();
                                    tickEffects.add(
                                       !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && !damageVictim
                                          ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                          : new ElementReactionRegistry.ReactionEffect(
                                             type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim
                                          )
                                    );
                                 } else {
                                    String elemId = eff.has("element") && eff.get("element").isJsonPrimitive()
                                       ? eff.get("element").getAsString()
                                       : (eff.has("element_id") && eff.get("element_id").isJsonPrimitive() ? eff.get("element_id").getAsString() : null);
                                    int amount = eff.has("amount") && eff.get("amount").isJsonPrimitive()
                                       ? eff.get("amount").getAsInt()
                                       : (eff.has("value") && eff.get("value").isJsonPrimitive() ? eff.get("value").getAsInt() : 0);
                                    float chance = eff.has("chance") && eff.get("chance").isJsonPrimitive() ? eff.get("chance").getAsFloat() : 1.0F;
                                    int duration = eff.has("duration") && eff.get("duration").isJsonPrimitive() ? eff.get("duration").getAsInt() : 0;
                                    tickEffects.add(new ElementReactionRegistry.ReactionEffect(type, elemId, amount, chance, duration));
                                 }
                              }
                           }
                        } else if (var.get("effects").isJsonObject()) {
                           JsonObject groupedxx = var.getAsJsonObject("effects");
                           if (groupedxx.has("damage") && groupedxx.get("damage").isJsonArray()) {
                              for (JsonElement effElxxx : groupedxx.getAsJsonArray("damage")) {
                                 if (effElxxx.isJsonObject()) {
                                    JsonObject eff = effElxxx.getAsJsonObject();
                                    String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                                    float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive()
                                       ? eff.get("multiplier").getAsFloat()
                                       : 1.0F;
                                    String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                                    float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                                    String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                       ? eff.get("damage_type").getAsString()
                                       : "";
                                    boolean damageAttacker = eff.has("damage_attacker")
                                       && eff.get("damage_attacker").isJsonPrimitive()
                                       && eff.get("damage_attacker").getAsBoolean();
                                    boolean damageVictim = eff.has("damage_victim")
                                       && eff.get("damage_victim").isJsonPrimitive()
                                       && eff.get("damage_victim").getAsBoolean();
                                    tickEffects.add(
                                       !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && !damageVictim
                                          ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                          : new ElementReactionRegistry.ReactionEffect(
                                             type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim
                                          )
                                    );
                                 }
                              }
                           }

                           if (groupedxx.has("attachment") && groupedxx.get("attachment").isJsonArray()) {
                              for (JsonElement attEl : groupedxx.getAsJsonArray("attachment")) {
                                 if (attEl.isJsonObject()) {
                                    JsonObject att = attEl.getAsJsonObject();
                                    String elemId = att.has("element") && att.get("element").isJsonPrimitive()
                                       ? att.get("element").getAsString()
                                       : (att.has("element_id") && att.get("element_id").isJsonPrimitive() ? att.get("element_id").getAsString() : null);
                                    int amount = att.has("amount") && att.get("amount").isJsonPrimitive()
                                       ? att.get("amount").getAsInt()
                                       : (att.has("value") && att.get("value").isJsonPrimitive() ? att.get("value").getAsInt() : 0);
                                    float chance = att.has("chance") && att.get("chance").isJsonPrimitive() ? att.get("chance").getAsFloat() : 1.0F;
                                    int duration = att.has("duration") && att.get("duration").isJsonPrimitive() ? att.get("duration").getAsInt() : 0;
                                    tickEffects.add(new ElementReactionRegistry.ReactionEffect("attachment", elemId, amount, chance, duration));
                                 }
                              }
                           }

                           if (groupedxx.has("potion") && groupedxx.get("potion").isJsonArray()) {
                              for (JsonElement potionEl : groupedxx.getAsJsonArray("potion")) {
                                 if (potionEl.isJsonObject()) {
                                    JsonObject potion = potionEl.getAsJsonObject();
                                    String potionId = potion.has("potion_id") && potion.get("potion_id").isJsonPrimitive()
                                       ? potion.get("potion_id").getAsString()
                                       : null;
                                    int potionDuration = potion.has("duration") && potion.get("duration").isJsonPrimitive()
                                       ? potion.get("duration").getAsInt()
                                       : 200;
                                    int potionLevel = potion.has("level") && potion.get("level").isJsonPrimitive() ? potion.get("level").getAsInt() - 1 : 0;
                                    float potionChance = potion.has("chance") && potion.get("chance").isJsonPrimitive()
                                       ? potion.get("chance").getAsFloat()
                                       : 1.0F;
                                    if (potionId != null) {
                                       tickEffects.add(
                                          new ElementReactionRegistry.ReactionEffect("potion", potionId, potionDuration, potionLevel, potionChance)
                                       );
                                    }
                                 }
                              }
                           }
                        }
                     } else if (root.has("effects")) {
                        if (root.get("effects").isJsonArray()) {
                           for (JsonElement effElxxxx : root.getAsJsonArray("effects")) {
                              if (effElxxxx.isJsonObject()) {
                                 JsonObject eff = effElxxxx.getAsJsonObject();
                                 String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                                 if (!"attachment".equalsIgnoreCase(type) && !"consume".equalsIgnoreCase(type)) {
                                    if ("potion".equalsIgnoreCase(type)) {
                                       String potionId = eff.has("potion_id") && eff.get("potion_id").isJsonPrimitive()
                                          ? eff.get("potion_id").getAsString()
                                          : null;
                                       int potionDuration = eff.has("duration") && eff.get("duration").isJsonPrimitive() ? eff.get("duration").getAsInt() : 200;
                                       int potionLevel = eff.has("level") && eff.get("level").isJsonPrimitive() ? eff.get("level").getAsInt() - 1 : 0;
                                       float potionChance = eff.has("chance") && eff.get("chance").isJsonPrimitive() ? eff.get("chance").getAsFloat() : 1.0F;
                                       if (potionId != null) {
                                          tickEffects.add(
                                             new ElementReactionRegistry.ReactionEffect("potion", potionId, potionDuration, potionLevel, potionChance)
                                          );
                                       }
                                    } else {
                                       float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive()
                                          ? eff.get("multiplier").getAsFloat()
                                          : 1.0F;
                                       String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                                       float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                                       String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                          ? eff.get("damage_type").getAsString()
                                          : "";
                                       boolean damageAttacker = eff.has("damage_attacker")
                                          && eff.get("damage_attacker").isJsonPrimitive()
                                          && eff.get("damage_attacker").getAsBoolean();
                                       boolean damageVictim = eff.has("damage_victim")
                                          && eff.get("damage_victim").isJsonPrimitive()
                                          && eff.get("damage_victim").getAsBoolean();
                                       tickEffects.add(
                                          !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && !damageVictim
                                             ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                             : new ElementReactionRegistry.ReactionEffect(
                                                type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim
                                             )
                                       );
                                    }
                                 } else {
                                    String elemId = eff.has("element") && eff.get("element").isJsonPrimitive()
                                       ? eff.get("element").getAsString()
                                       : (eff.has("element_id") && eff.get("element_id").isJsonPrimitive() ? eff.get("element_id").getAsString() : null);
                                    int amount = eff.has("amount") && eff.get("amount").isJsonPrimitive()
                                       ? eff.get("amount").getAsInt()
                                       : (eff.has("value") && eff.get("value").isJsonPrimitive() ? eff.get("value").getAsInt() : 0);
                                    float chance = eff.has("chance") && eff.get("chance").isJsonPrimitive() ? eff.get("chance").getAsFloat() : 1.0F;
                                    int duration = eff.has("duration") && eff.get("duration").isJsonPrimitive() ? eff.get("duration").getAsInt() : 0;
                                    tickEffects.add(new ElementReactionRegistry.ReactionEffect(type, elemId, amount, chance, duration));
                                 }
                              }
                           }
                        } else if (root.get("effects").isJsonObject()) {
                           JsonObject groupedxxx = root.getAsJsonObject("effects");
                           if (groupedxxx.has("damage") && groupedxxx.get("damage").isJsonArray()) {
                              for (JsonElement effElxxxxx : groupedxxx.getAsJsonArray("damage")) {
                                 if (effElxxxxx.isJsonObject()) {
                                    JsonObject eff = effElxxxxx.getAsJsonObject();
                                    String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                                    float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive()
                                       ? eff.get("multiplier").getAsFloat()
                                       : 1.0F;
                                    String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                                    float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                                    String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                       ? eff.get("damage_type").getAsString()
                                       : "";
                                    boolean damageAttacker = eff.has("damage_attacker")
                                       && eff.get("damage_attacker").isJsonPrimitive()
                                       && eff.get("damage_attacker").getAsBoolean();
                                    boolean damageVictim = eff.has("damage_victim")
                                       && eff.get("damage_victim").isJsonPrimitive()
                                       && eff.get("damage_victim").getAsBoolean();
                                    tickEffects.add(
                                       !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && !damageVictim
                                          ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                          : new ElementReactionRegistry.ReactionEffect(
                                             type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim
                                          )
                                    );
                                 }
                              }
                           }

                           if (groupedxxx.has("attachment") && groupedxxx.get("attachment").isJsonArray()) {
                              for (JsonElement attElx : groupedxxx.getAsJsonArray("attachment")) {
                                 if (attElx.isJsonObject()) {
                                    JsonObject att = attElx.getAsJsonObject();
                                    String elemId = att.has("element") && att.get("element").isJsonPrimitive()
                                       ? att.get("element").getAsString()
                                       : (att.has("element_id") && att.get("element_id").isJsonPrimitive() ? att.get("element_id").getAsString() : null);
                                    int amount = att.has("amount") && att.get("amount").isJsonPrimitive()
                                       ? att.get("amount").getAsInt()
                                       : (att.has("value") && att.get("value").isJsonPrimitive() ? att.get("value").getAsInt() : 0);
                                    float chance = att.has("chance") && att.get("chance").isJsonPrimitive() ? att.get("chance").getAsFloat() : 1.0F;
                                    int duration = att.has("duration") && att.get("duration").isJsonPrimitive() ? att.get("duration").getAsInt() : 0;
                                    tickEffects.add(new ElementReactionRegistry.ReactionEffect("attachment", elemId, amount, chance, duration));
                                 }
                              }
                           }
                        }
                     }

                     ElementReactionRegistry.setTickRule(reactionId.trim(), reqMap, conMap, tickEffects, interval);
                  }
               }

               ElementReactionRegistry.add(reactionId.trim(), "tick");
               ok++;
            } else {
               Map<String, Integer> reqMapx = new HashMap<>();
               if (root.has("requirements") && root.get("requirements").isJsonObject()) {
                  JsonObject req = root.getAsJsonObject("requirements");

                  for (Entry<String, JsonElement> enxx : req.entrySet()) {
                     if (enxx.getValue().isJsonPrimitive()) {
                        reqMapx.put(enxx.getKey(), Math.max(0, enxx.getValue().getAsInt()));
                     }
                  }
               }

               Map<String, Integer> conMapx = new HashMap<>();
               if (root.has("consume") && root.get("consume").isJsonObject()) {
                  JsonObject con = root.getAsJsonObject("consume");

                  for (Entry<String, JsonElement> enxxx : con.entrySet()) {
                     if (enxxx.getValue().isJsonPrimitive()) {
                        conMapx.put(enxxx.getKey(), Math.max(0, enxxx.getValue().getAsInt()));
                     }
                  }
               }

               List<ElementReactionRegistry.ReactionEffect> tickEffects = new ArrayList<>();
               int intervalx = 1;
               if (root.has("interval") && root.get("interval").isJsonPrimitive()) {
                  try {
                     intervalx = Math.max(1, root.get("interval").getAsInt());
                  } catch (Exception var35) {
                     intervalx = 1;
                  }
               }

               if (root.has("effects")) {
                  if (root.get("effects").isJsonArray()) {
                     for (JsonElement effElxxxxxx : root.getAsJsonArray("effects")) {
                        if (effElxxxxxx.isJsonObject()) {
                           JsonObject eff = effElxxxxxx.getAsJsonObject();
                           String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                           float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive() ? eff.get("multiplier").getAsFloat() : 1.0F;
                           String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                           float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                           String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                              ? eff.get("damage_type").getAsString()
                              : "";
                           boolean damageAttacker = eff.has("damage_attacker")
                              && eff.get("damage_attacker").isJsonPrimitive()
                              && eff.get("damage_attacker").getAsBoolean();
                           boolean damageVictim = eff.has("damage_victim")
                              && eff.get("damage_victim").isJsonPrimitive()
                              && eff.get("damage_victim").getAsBoolean();
                           tickEffects.add(
                              !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && !damageVictim
                                 ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                 : new ElementReactionRegistry.ReactionEffect(type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim)
                           );
                        }
                     }
                  } else if (root.get("effects").isJsonObject()) {
                     JsonObject groupedxxxx = root.getAsJsonObject("effects");
                     if (groupedxxxx.has("damage") && groupedxxxx.get("damage").isJsonArray()) {
                        for (JsonElement effElxxxxxxx : groupedxxxx.getAsJsonArray("damage")) {
                           if (effElxxxxxxx.isJsonObject()) {
                              JsonObject eff = effElxxxxxxx.getAsJsonObject();
                              String type = eff.has("type") && eff.get("type").isJsonPrimitive() ? eff.get("type").getAsString() : "";
                              float multiplier = eff.has("multiplier") && eff.get("multiplier").isJsonPrimitive() ? eff.get("multiplier").getAsFloat() : 1.0F;
                              String formula = eff.has("formula") && eff.get("formula").isJsonPrimitive() ? eff.get("formula").getAsString() : "";
                              float radius = eff.has("radius") && eff.get("radius").isJsonPrimitive() ? eff.get("radius").getAsFloat() : 0.0F;
                              String damageTypeStr = eff.has("damage_type") && eff.get("damage_type").isJsonPrimitive()
                                 ? eff.get("damage_type").getAsString()
                                 : "";
                              boolean damageAttacker = eff.has("damage_attacker")
                                 && eff.get("damage_attacker").isJsonPrimitive()
                                 && eff.get("damage_attacker").getAsBoolean();
                              boolean damageVictim = eff.has("damage_victim")
                                 && eff.get("damage_victim").isJsonPrimitive()
                                 && eff.get("damage_victim").getAsBoolean();
                              tickEffects.add(
                                 !(radius > 0.0F) && damageTypeStr.isEmpty() && damageAttacker && !damageVictim
                                    ? new ElementReactionRegistry.ReactionEffect(type, multiplier, formula)
                                    : new ElementReactionRegistry.ReactionEffect(type, multiplier, formula, radius, damageTypeStr, damageAttacker, damageVictim)
                              );
                           }
                        }
                     }
                  }
               }

               ElementReactionRegistry.setTickRule(reactionId.trim(), reqMapx, conMapx, tickEffects, intervalx);
               ElementReactionRegistry.add(reactionId.trim(), "tick");
               ok++;
            }
         } catch (JsonParseException var39) {
            SpellElemental.LOGGER.error("解析元素反应失败 {}: {}", id, var39.getMessage());
            err++;
         } catch (Exception var40) {
            SpellElemental.LOGGER.error("加载元素反应出错 {}: {}", new Object[]{id, var40.getMessage(), var40});
            err++;
         }
      }

      SpellElemental.LOGGER.info("元素反应加载完成: {} 个成功, {} 个错误", ok, err);
   }
}
