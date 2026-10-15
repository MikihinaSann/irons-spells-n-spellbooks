package net.alshanex.alshanex_familiars.util;

import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.resources.ResourceLocation;

public class ASpellAnimations {
   public static ResourceLocation ANIMATION_RESOURCE = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "animation");
   public static final AnimationHolder TOUCH_GROUND_ANIMATION = new AnimationHolder("alshanex_familiars:touch_ground", false, true);
   public static final AnimationHolder FIST_START_ANIMATION = new AnimationHolder("alshanex_familiars:fist_start", false, true);
   public static final AnimationHolder FIST_RELEASE_ANIMATION = new AnimationHolder("alshanex_familiars:fist_release", true, true);
}
