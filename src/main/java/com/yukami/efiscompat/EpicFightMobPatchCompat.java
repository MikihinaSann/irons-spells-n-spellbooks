package com.yukami.efiscompat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import yesman.epicfight.registry.entries.EpicFightAttributes;

import java.util.Set;

/**
 * Adds Epic Fight combat attributes to modded entities covered by our
 * {@code epicfight_mobpatch} datapack entries. Vanilla mobs receive these
 * through Epic Fight's own EntityAttributeModificationEvent; datapack-patched
 * mod entities get none, so every patched entity would spam
 * "does not have attribute epicfight:stun_armor" and fight without stun/impact.
 */
public final class EpicFightMobPatchCompat {

    /** Entity ids that have an entry under data/&lt;namespace&gt;/epicfight_mobpatch/. */
    private static final Set<String> PATCHED_ENTITIES = Set.of(
            // irons_spellbooks — vanilla-model summons
            "irons_spellbooks:summoned_skeleton",
            "irons_spellbooks:summoned_zombie",
            "irons_spellbooks:summoned_vex",
            // cataclysm — biped humanoids
            "cataclysm:drowned_host", "cataclysm:koboleton", "cataclysm:kobolediator",
            "cataclysm:draugr", "cataclysm:royal_draugr", "cataclysm:elite_draugr",
            "cataclysm:aptrgangr", "cataclysm:ignited_revenant", "cataclysm:ignited_berserker",
            "cataclysm:deepling", "cataclysm:deepling_brute", "cataclysm:deepling_angler",
            "cataclysm:deepling_priest", "cataclysm:deepling_warlock",
            "cataclysm:the_prowler", "cataclysm:clawdian", "cataclysm:cindaria",
            // traveloptics summoned cataclysm mirrors
            "traveloptics:summoned_aptrgangr", "traveloptics:summoned_draugr",
            "traveloptics:summoned_royal_draugr", "traveloptics:summoned_elite_draugr",
            "traveloptics:summoned_koboleton", "traveloptics:summoned_kobolediator",
            "traveloptics:summoned_ignited_revenant", "traveloptics:summoned_ignited_berserker",
            "traveloptics:summoned_the_prowler",
            // born_in_chaos_v1 — biped skeletons/zombies/illagers/knights
            "born_in_chaos_v1:decrepit_skeleton", "born_in_chaos_v1:skeleton_demoman",
            "born_in_chaos_v1:baby_skeleton", "born_in_chaos_v1:baby_skeleton_minion",
            "born_in_chaos_v1:controlled_baby_skeleton", "born_in_chaos_v1:skeleton_thrasher",
            "born_in_chaos_v1:skeleton_thrasher_not_despawn", "born_in_chaos_v1:siamese_skeletons",
            "born_in_chaos_v1:siamese_skeletonsleft", "born_in_chaos_v1:siamese_skeletonsright",
            "born_in_chaos_v1:decaying_zombie", "born_in_chaos_v1:decaying_zombie_not_despawn",
            "born_in_chaos_v1:barrel_zombie", "born_in_chaos_v1:zombie_clown",
            "born_in_chaos_v1:zombie_clown_not_despawn", "born_in_chaos_v1:zombie_fisherman",
            "born_in_chaos_v1:zombie_lumberjack", "born_in_chaos_v1:zombie_bruiser",
            "born_in_chaos_v1:missioner", "born_in_chaos_v1:missionary_charge",
            "born_in_chaos_v1:missionary_raider", "born_in_chaos_v1:bonescaller",
            "born_in_chaos_v1:bonescaller_not_despawn", "born_in_chaos_v1:supreme_bonescaller",
            "born_in_chaos_v1:supreme_bonescaller_stage_2", "born_in_chaos_v1:supreme_bonescaller_not_despawn",
            "born_in_chaos_v1:door_knight", "born_in_chaos_v1:door_knight_not_despawn",
            "born_in_chaos_v1:fallen_chaos_knight", "born_in_chaos_v1:sir_pumpkinhead",
            "born_in_chaos_v1:sir_pumpkinhead_without_horse", "born_in_chaos_v1:lord_pumpkinhead_withouta_horse",
            "born_in_chaos_v1:lord_the_headless", "born_in_chaos_v1:pumpkin_bruiser",
            "born_in_chaos_v1:pumpkin_dunce", "born_in_chaos_v1:senor_pumpkin",
            "born_in_chaos_v1:mrs_pumpkin", "born_in_chaos_v1:mr_pumpkin",
            "born_in_chaos_v1:mr_pumpkin_controlled", "born_in_chaos_v1:lifestealer",
            "born_in_chaos_v1:lifestealer_true_form", "born_in_chaos_v1:scarlet_persecutor",
            "born_in_chaos_v1:krampus", "born_in_chaos_v1:krampus_henchman"
    );

    private EpicFightMobPatchCompat() {
    }

    @SuppressWarnings("unchecked")
    public static void registerMobPatchAttributes() {
        int patched = 0;
        for (String id : PATCHED_ENTITIES) {
            var type = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(id));
            if (type.isEmpty()) {
                EpicFightIronCompat.LOGGER.debug("epicfight mobpatch: entity {} not found, skipping attributes", id);
                continue;
            }
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.WEIGHT.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.ARMOR_NEGATION.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.IMPACT.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.MAX_STRIKES.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.STUN_ARMOR.get());
            // humanoid extras
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.OFFHAND_ATTACK_SPEED.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.OFFHAND_MAX_STRIKES.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get());
            addAttribute((EntityType<? extends LivingEntity>) type.get(), EpicFightAttributes.OFFHAND_IMPACT.get());
            patched++;
        }
        EpicFightIronCompat.LOGGER.info("Epic Fight mobpatch attributes applied to {} modded entities", patched);
    }

    private static void addAttribute(EntityType<? extends LivingEntity> type,
                                     net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        if (attribute == null) {
            return;
        }
        var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
        var map = DefaultAttributes.SUPPLIERS;
        var existing = map.get(type);
        if (existing != null && existing.hasAttribute(holder)) {
            return;
        }
        var builder = AttributeSupplier.builder();
        if (existing != null) {
            existing.instances.forEach((h, instance) -> builder.add(h, instance.getBaseValue()));
        }
        map.put(type, builder.add(holder, attribute.getDefaultValue()).build());
    }
}
