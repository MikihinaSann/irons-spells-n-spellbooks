package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.armor.ArchevokerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.BootsOfSpeedArmorModel;
import io.redspace.ironsspellbooks.entity.armor.CryomancerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.CultistArmorModel;
import io.redspace.ironsspellbooks.entity.armor.DyeableArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.ElectromancerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.GenericArmorModel;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.GoldCrownModel;
import io.redspace.ironsspellbooks.entity.armor.InfernalSorcererArmorModel;
import io.redspace.ironsspellbooks.entity.armor.PaladinArmorModel;
import io.redspace.ironsspellbooks.entity.armor.PlaguedArmorModel;
import io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorModel;
import io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.pumpkin.PumpkinArmorModel;
import io.redspace.ironsspellbooks.entity.armor.pumpkin.PumpkinArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.PyromancerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.ShadowwalkerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.TarnishedCrownModel;
import io.redspace.ironsspellbooks.entity.armor.WanderingMagicianModel;
import io.redspace.ironsspellbooks.entity.armor.netherite.NetheriteMageArmorModel;
import io.redspace.ironsspellbooks.item.armor.WizardArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * Client-only construction sites for geckolib armor renderers. {@code GeoArmorRenderer}
 * extends {@code HumanoidModel} (a client-only class the Fabric loader refuses to load
 * on a dedicated server), so the {@code new} expressions must live here — the item
 * classes delegate via {@code Object}-typed factory calls that never place renderer
 * types into their own stack maps.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientArmorRenderers {
    private ClientArmorRenderers() {
    }

    private static Object trace(String name, java.util.function.Supplier<Object> s) {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.SERVER) {
            new Throwable("CLIENT RENDERER ON SERVER: " + name).printStackTrace();
            Thread.dumpStack();
        }
        return s.get();
    }

    public static Object wanderingMagician() {
        return trace("wanderingMagician", () -> new GenericCustomArmorRenderer<>(new WanderingMagicianModel()));
    }

    public static Object pumpkin() {
        return new PumpkinArmorRenderer(new PumpkinArmorModel());
    }

    public static Object priest() {
        return new PriestArmorRenderer(new PriestArmorModel());
    }

    public static Object pyromancer() {
        return new GenericCustomArmorRenderer<>(new PyromancerArmorModel());
    }

    public static Object cultist() {
        return new GenericCustomArmorRenderer<>(new CultistArmorModel());
    }

    public static Object cryomancer() {
        return new GenericCustomArmorRenderer<>(new CryomancerArmorModel());
    }

    public static Object electromancer() {
        return new GenericCustomArmorRenderer<>(new ElectromancerArmorModel());
    }

    public static Object infernalSorcerer() {
        return new GenericCustomArmorRenderer<>(new InfernalSorcererArmorModel());
    }

    public static Object netheriteMage() {
        return new DyeableArmorRenderer<>(new NetheriteMageArmorModel());
    }

    public static Object paladin() {
        return new GenericCustomArmorRenderer<>(new PaladinArmorModel());
    }

    public static Object plagued() {
        return trace("plagued", () -> new GenericCustomArmorRenderer<>(new PlaguedArmorModel()));
    }

    public static Object shadowwalker() {
        return new GenericCustomArmorRenderer<>(new ShadowwalkerArmorModel());
    }

    public static Object tarnishedCrown() {
        return new GenericCustomArmorRenderer<>(new TarnishedCrownModel());
    }

    public static Object goldCrown() {
        return new GenericCustomArmorRenderer<>(new GoldCrownModel());
    }

    public static Object archevoker() {
        return new GenericCustomArmorRenderer<>(new ArchevokerArmorModel());
    }

    public static Object bootsOfSpeed() {
        return new GenericCustomArmorRenderer<>(new BootsOfSpeedArmorModel());
    }

    public static Object wizard() {
        return new DyeableArmorRenderer<>(new GenericArmorModel<WizardArmorItem>("wizard")
                .variants(Map.of("hat", IronsSpellbooks.id("geo/wizard_armor_hat.geo.json"))));
    }
}
