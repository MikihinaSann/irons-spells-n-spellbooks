package net.neoforged.neoforge.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForge;

/** NeoForge ClientHooks - client helpers; layer definitions drain into Fabric's EntityModelLayerRegistry. */
public class ClientHooks {

    /**
     * Registers a model layer definition. NeoForge queues these into
     * EntityRenderersEvent.RegisterLayerDefinitions; Fabric accepts direct
     * registration at any point before model baking.
     */
    /**
     * NeoForge's {@code LocalPlayer#aiStep} hook — fires
     * {@link net.neoforged.neoforge.client.event.MovementInputUpdateEvent}
     * after {@code Input#tick}. Kept as a named static so mod mixins can
     * inject around the call site like they do on NeoForge.
     */
    public static void onMovementInputUpdate(net.minecraft.world.entity.player.Player player,
            net.minecraft.client.player.Input input) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                new net.neoforged.neoforge.client.event.MovementInputUpdateEvent(
                        (net.minecraft.client.player.LocalPlayer) player, input));
    }

    /**
     * NeoForge's armor-model resolution: delegates to the item's client
     * extensions.
     */
    public static net.minecraft.client.model.Model getArmorModel(
            net.minecraft.world.entity.LivingEntity entityLiving, net.minecraft.world.item.ItemStack itemStack,
            net.minecraft.world.entity.EquipmentSlot slot, net.minecraft.client.model.HumanoidModel<?> _default) {
        return net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemStack)
                .getGenericArmorModel(entityLiving, itemStack, slot, _default);
    }

    /**
     * NeoForge's armor-texture resolution: item override first, then the
     * material layer texture.
     */
    public static net.minecraft.resources.ResourceLocation getArmorTexture(
            net.minecraft.world.entity.Entity entity, net.minecraft.world.item.ItemStack armor,
            net.minecraft.world.item.ArmorMaterial.Layer layer, boolean innerModel,
            net.minecraft.world.entity.EquipmentSlot slot) {
        net.minecraft.resources.ResourceLocation result = armor.getItem().getArmorTexture(armor, entity, slot, layer,
                innerModel);
        return result != null ? result : layer.texture(innerModel);
    }

    /**
     * Copies the original humanoid model's pose/visibility onto a replacement.
     */
    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.world.entity.LivingEntity> void copyModelProperties(
            net.minecraft.client.model.HumanoidModel<T> original,
            net.minecraft.client.model.HumanoidModel<?> replacement) {
        original.copyPropertiesTo((net.minecraft.client.model.HumanoidModel<T>) replacement);
        replacement.head.visible = original.head.visible;
        replacement.hat.visible = original.hat.visible;
        replacement.body.visible = original.body.visible;
        replacement.rightArm.visible = original.rightArm.visible;
        replacement.leftArm.visible = original.leftArm.visible;
        replacement.rightLeg.visible = original.rightLeg.visible;
        replacement.leftLeg.visible = original.leftLeg.visible;
    }

    public static void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> supplier) {
        net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
                .registerModelLayer(location, supplier::get);
    }

    /** NeoForge's tooltip font hook — IClientItemExtensions.getFont or the fallback. */
    public static Font getTooltipFont(ItemStack stack, Font fallbackFont) {
        Font stackFont = IClientItemExtensions.of(stack).getFont(stack, fallbackFont);
        return stackFont == null ? fallbackFont : stackFont;
    }

    /** NeoForge 1.21.1 — ClientHooks.gatherTooltipComponents (text variant). */
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        return gatherTooltipComponents(stack, textElements, Optional.empty(), mouseX, screenWidth, screenHeight, fallbackFont);
    }

    /** NeoForge 1.21.1 — ClientHooks.gatherTooltipComponents (text + tooltip component). */
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
                .map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
                .collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));
        return gatherTooltipComponentsFromElements(stack, elements, mouseX, screenWidth, screenHeight, fallbackFont);
    }

    /** NeoForge 1.21.1 — ClientHooks.gatherTooltipComponentsFromElements, verbatim logic. */
    public static List<ClientTooltipComponent> gatherTooltipComponentsFromElements(ItemStack stack, List<Either<FormattedText, TooltipComponent>> elements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        Font font = getTooltipFont(stack, fallbackFont);

        var event = new RenderTooltipEvent.GatherComponents(stack, screenWidth, screenHeight, elements, -1);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return List.of();

        // text wrapping
        int tooltipTextWidth = event.getTooltipElements().stream()
                .mapToInt(either -> either.map(font::width, component -> 0))
                .max()
                .orElse(0);

        boolean needsWrap = false;

        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (event.getMaxWidth() > 0 && tooltipTextWidth > event.getMaxWidth()) {
            tooltipTextWidth = event.getMaxWidth();
            needsWrap = true;
        }

        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap) {
            return event.getTooltipElements().stream()
                    .flatMap(either -> either.map(
                            text -> splitLine(text, font, tooltipTextWidthF),
                            component -> Stream.of(ClientTooltipComponent.create(component))))
                    .toList();
        }
        return event.getTooltipElements().stream()
                .map(either -> either.map(
                        text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
                        ClientTooltipComponent::create))
                .toList();
    }

    private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
        if (text instanceof Component component && component.getString().isEmpty()) {
            return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
        }
        return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
    }

    /**
     * NeoForge's {@code ClientHooks.handleCameraTransforms} — applies the
     * model's {@link net.minecraft.client.renderer.block.model.ItemTransform}
     * for the display context and returns the model to render.
     */
    public static net.minecraft.client.resources.model.BakedModel handleCameraTransforms(
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.resources.model.BakedModel model,
            net.minecraft.world.item.ItemDisplayContext context, boolean leftHand) {
        net.minecraft.client.renderer.block.model.ItemTransform transform =
                model.getTransforms().getTransform(context);
        if (transform != net.minecraft.client.renderer.block.model.ItemTransform.NO_TRANSFORM) {
            transform.apply(leftHand, poseStack);
        }
        return model;
    }

    private ClientHooks() {
    }
}
