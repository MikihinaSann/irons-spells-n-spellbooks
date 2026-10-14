package dev.shadowsoffire.apothic_attributes.mob_effect;

import dev.shadowsoffire.apothic_attributes.ALConfig;
import dev.shadowsoffire.apothic_attributes.ApothicAttributes;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class KnowledgeEffect extends MobEffect {

    public KnowledgeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xF4EE42);
        // NeoForge's 4-arg AttributeTemplate(id, amount, op, curve) — Fabric: the curve is
        // registered by template id and applied by MobEffectAttributeTemplateMixin.
        MobEffect.AttributeTemplate template = new MobEffect.AttributeTemplate(ApothicAttributes.loc("ancient_knowledge"), 0.0, Operation.ADD_MULTIPLIED_TOTAL);
        net.neoforged.neoforge.common.util.AttributeTemplateCurves.register(template.id(), amp -> ALConfig.knowledgeMultiplier * (amp + 1) - 1);
        this.attributeModifiers.put(ALObjects.Attributes.EXPERIENCE_GAINED, template);
    }

}
