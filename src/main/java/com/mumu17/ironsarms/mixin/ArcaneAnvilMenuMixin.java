package com.mumu17.ironsarms.mixin;

import com.mumu17.ironsarms.utils.GunTags;
import com.tacz.guns.api.item.IGun;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ArcaneAnvilMenu.class})
public abstract class ArcaneAnvilMenuMixin extends ItemCombinerMenu {
    public ArcaneAnvilMenuMixin(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @Inject(
            method = {"createResult"},
            at = {@At("TAIL")}
    )
    private void onCreateResult(CallbackInfo ci) {
        ItemStack baseStack = this.inputSlots.getItem(0);
        ItemStack modifierStack = this.inputSlots.getItem(1);
        if (this.resultSlots.getItem(0).isEmpty()) {
            if (baseStack.getItem() instanceof IGun && ISpellContainer.isSpellContainer(modifierStack)) {
                ISpellContainer spellContainer = ISpellContainer.get(modifierStack);
                if (!spellContainer.isEmpty()) {
                    SpellData spellData = spellContainer.getSpellAtIndex(0);
                    ItemStack result = baseStack.copy();
                    CompoundTag spellTag = new CompoundTag();
                    spellTag.putString("SpellID", spellData.getSpell().getSpellId());
                    spellTag.putInt("Level", spellData.getLevel());
                    CustomData.update(DataComponents.CUSTOM_DATA, result,
                            tag -> tag.put(GunTags.INSCRIBED_SPELL_KEY, spellTag));
                    this.resultSlots.setItem(0, result);
                }
            }
        }
    }
}
