package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.extensions.IIngredientExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's patched Ingredient: carries an {@link ICustomIngredient} whose
 * test() overrides the vanilla value matching.
 */
@Mixin(Ingredient.class)
public abstract class IngredientCustomIngredientMixin implements IIngredientExtension {

    @Shadow
    private Ingredient.Value[] values;

    @Unique
    @Nullable
    private ICustomIngredient irons_spellbooks$customIngredient;

    @Override
    public boolean isCustom() {
        return this.irons_spellbooks$customIngredient != null;
    }

    @Override
    @Nullable
    public ICustomIngredient getCustomIngredient() {
        return this.irons_spellbooks$customIngredient;
    }

    @Override
    public void setCustomIngredient(@Nullable ICustomIngredient customIngredient) {
        this.irons_spellbooks$customIngredient = customIngredient;
    }

    @Override
    public Ingredient.Value[] getValues() {
        return this.values;
    }

    @Override
    public boolean isSimple() {
        return this.irons_spellbooks$customIngredient == null
                || this.irons_spellbooks$customIngredient.isSimple();
    }

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$customTest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.irons_spellbooks$customIngredient != null) {
            cir.setReturnValue(this.irons_spellbooks$customIngredient.test(stack));
        }
    }
}
