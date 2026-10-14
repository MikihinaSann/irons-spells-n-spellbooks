package net.neoforged.neoforge.common.extensions;

/**
 * Duck interface applied (via mixin) to vanilla enums that NeoForge patches
 * with extra constructors. When {@code EnumExtender} builds a constant whose
 * declared parameters do not fit a vanilla constructor signature, the
 * mismatched arguments are coerced to {@code null} for the constructor call
 * and then delivered here so the enum can store them (e.g. Rarity's
 * {@code UnaryOperator<Style>} style modifier).
 */
public interface IEnumExtensionCarrier {

    /**
     * Receives a constructor argument that could not be passed to the vanilla
     * constructor. {@code declaredType} is the formal parameter type of the
     * vanilla constructor at the slot the value was destined for — or
     * {@code null} when the extension's constructor had more parameters than
     * any vanilla constructor and the value simply had no slot (e.g.
     * {@code ArmPose}'s extra {@code IArmPoseTransformer} argument).
     */
    default void neoforge$setExtraCtorArg(Class<?> declaredType, Object value) {
    }
}
