package com.site21.bittermelon.init.custom;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.substance.Nature;
import com.site21.bittermelon.common.systems.substance.Substance;
import com.site21.bittermelon.common.systems.substance.SubstanceState;
import com.site21.bittermelon.init.neoforge.BitterMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.site21.bittermelon.init.custom.Drugs.CYANIDE;
import static com.site21.bittermelon.init.neoforge.BitterRegistries.SUBSTANCE_REGISTRY_KEY;

public class Substances {
    public static final DeferredRegister<Substance> SUBSTANCES = DeferredRegister.create(SUBSTANCE_REGISTRY_KEY, Bittermelon.MOD_ID);

    public static final DeferredHolder<Substance, Substance> OXYGEN = SUBSTANCES.register("oxygen", () -> new Substance(
            "Oxygen",
            new Substance.Properties().nature(Nature.COMBUSTIBLE, 0.5f)
    ));

    public static final DeferredHolder<Substance, Substance> WATER = SUBSTANCES.register("water", () -> new Substance(
            "Water",
            new Substance.Properties()
                    .state(SubstanceState.LIQUID)
                    .color(0x50AAD5DB)
                    .nature(Nature.WATER_BASED, 1.0f)
    ));

    public static final DeferredHolder<Substance, Substance> ICE = SUBSTANCES.register("ice", () -> new Substance(
            "Ice",
            new Substance.Properties()
                    .state(SubstanceState.SOLID)
                    .color(0x50AAD5DB)
                    .density(0.917f)
                    .nature(Nature.WATER_BASED, 1.0f)
    ));

    public static final DeferredHolder<Substance, Substance> STEAM = SUBSTANCES.register("steam", () -> new Substance(
            "Steam",
            new Substance.Properties()
                    .state(SubstanceState.GAS)
                    .color(0x50AAD5DB)
                    .density(0.597f)
                    .nature(Nature.WATER_BASED, 1.0f)
    ));

    public static final DeferredHolder<Substance, Substance> HYDROGEN_PEROXIDE = SUBSTANCES.register("hydrogen_peroxide", () -> new Substance(
            "Hydrogen Peroxide",
            new Substance.Properties().nature(Nature.WEAK_ACID, 0.5f)
    ));

    public static final DeferredHolder<Substance, Substance> BLOOD = SUBSTANCES.register("blood", () -> new Substance(
            "Blood",
            new Substance.Properties()
                    .color(0xFFB52222)
                    .viscosity(2500)
                    .nature(Nature.WATER_BASED, 0.7f)
    ));

    public static final DeferredHolder<Substance, Substance> URINE = SUBSTANCES.register("urine", () -> new Substance(
            "Urine",
            new Substance.Properties()
                    .color(0xFFFFDC65)
                    .nature(Nature.STRONG_ACID, 0.8f)
                    .nature(Nature.WATER_BASED, 0.8f)
    ));

    public static final DeferredHolder<Substance, Substance> VOMIT = SUBSTANCES.register("vomit", () -> new Substance(
            "Vomit",
            new Substance.Properties()
                    .color(0xFFCFBD8B)
                    .nature(Nature.BASE, 0.5f)
                    .nature(Nature.WATER_BASED, 0.6f)
    ));

    public static final DeferredHolder<Substance, Substance> SODIUM_CYANIDE = SUBSTANCES.register("sodium_cyanide", () -> new Substance(
            "Sodium Cyanide",
            new Substance.Properties()
                    .color(0xFFFFFFFF)
                    .drug(CYANIDE)
                    .smell("almondy")
                    .flavor("almondy")
    ));

    public static final DeferredHolder<Substance, Substance> HYDROGEN_CYANIDE = SUBSTANCES.register("hydrogen_cyanide", () -> new Substance(
            "Hydrogen Cyanide",
            new Substance.Properties()
                    .color(0xFFFFFFFF)
                    .drug(CYANIDE)
                    .smell("almondy")
                    .flavor("bitter")
                    .nature(Nature.WEAK_ACID, 0.6f)
    ));

    public static final DeferredHolder<Substance, Substance> MOTOR_OIL = SUBSTANCES.register("motor_oil", () -> new Substance(
            "Motor Oil",
            new Substance.Properties()
                    .density(0.93f)
                    .molarMass(880)
                    .color(0xFFE3B324)
                    .slipperiness(0.5f)
                    .flavor("oily")
    ));

    public static final DeferredHolder<Substance, Substance> SPACE_MIRAGE = SUBSTANCES.register("space_mirage", () -> new Substance(
            "Space Mirage",
            new Substance.Properties()
                    .density(0.93f)
                    .molarMass(880)
                    .color(0xFFB1A5C2)
                    .slipperiness(0.5f)
                    .drug(Drugs.SPACE_MIRAGE)
                    .flavor("oily")
    ));

    public static final DeferredHolder<Substance, Substance> KOOL_AID = SUBSTANCES.register("kool_aid", () -> new Substance(
            "Kool-Aid",
            new Substance.Properties()
                    .color(0xFFB52222)
                    .flavor("sweet")
                    .nature(Nature.WATER_BASED, 0.9f)
    ));

    public static final DeferredHolder<Substance, Substance> APPLE_JUICE = SUBSTANCES.register("apple_juice", () -> new Substance(
            "Apple Juice",
            new Substance.Properties()
                    .color(0xFFFFDC65)
                    .flavor("sweet")
                    .nature(Nature.WATER_BASED, 0.8f)
    ));

    public static final DeferredHolder<Substance, Substance> SULFURIC_ACID = SUBSTANCES.register("sulfuric_acid", () -> new Substance(
            "Sulfuric Acid",
            new Substance.Properties()
                    .color(0x50AAD5DB)
                    .nature(Nature.STRONG_ACID, 0.9f)
    ));

    public static final DeferredHolder<Substance, Substance> HYDROCHLORIC_ACID = SUBSTANCES.register("hydrochloric_acid", () -> new Substance(
            "Hydrochloric Acid",
            new Substance.Properties()
                    .color(0x50AAD5DB)
                    .nature(Nature.STRONG_ACID, 0.8f)
    ));

    public static final DeferredHolder<Substance, Substance> NITRIC_ACID = SUBSTANCES.register("nitric_acid", () -> new Substance(
            "Nitric Acid",
            new Substance.Properties()
                    .color(0x50AAD5DB)
                    .nature(Nature.STRONG_ACID, 0.7f)
    ));

    public static final DeferredHolder<Substance, Substance> RED_ICE = SUBSTANCES.register("red_ice", () -> new Substance(
            "Red Ice",
            new Substance.Properties()
                    .color(0xFFB52222)
    ));

    public static final DeferredHolder<Substance, Substance> BRINE_SHRIMP = SUBSTANCES.register("brine_shrimp", () -> new Substance(
            "Brine Shrimp",
            new Substance.Properties()
                    .color(0xFFFFDC65)
                    .nature(Nature.WATER_BASED, 0.9f)
    ));

    public static final DeferredHolder<Substance, Substance> FERTILE_LIQUID = SUBSTANCES.register("fertile_liquid", () -> new Substance(
            "Fertile Liquid",
            new Substance.Properties()
                    .color(0xFFB1A5C2)
                    .nature(Nature.WATER_BASED, 0.9f)
    ));

    public static final DeferredHolder<Substance, Substance> EYEBALL_FLUID = SUBSTANCES.register("eyeball_fluid", () -> new Substance(
            "Eyeball Fluid",
            new Substance.Properties()
                    .color(0x55FFFFFF)
                    .contactEffects(new MobEffectInstance(BitterMobEffects.EYEBALL_GROWTH, MobEffectInstance.INFINITE_DURATION, 0, true, false))
    ));
}

