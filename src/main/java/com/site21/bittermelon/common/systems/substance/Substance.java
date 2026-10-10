package com.site21.bittermelon.common.systems.substance;

import com.mojang.serialization.Codec;
import com.site21.bittermelon.common.systems.medical.legacy.drug.Drug;
import com.site21.bittermelon.common.systems.medical.legacy.drug.DrugHelper;
import com.site21.bittermelon.common.systems.medical.legacy.drug.DrugInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

import static com.site21.bittermelon.init.neoforge.BitterRegistries.SUBSTANCE_REGISTRY;
import static com.site21.bittermelon.init.neoforge.BitterRegistries.SUBSTANCE_REGISTRY_KEY;

public class Substance {
    public static final Codec<Holder<Substance>> CODEC = SUBSTANCE_REGISTRY.holderByNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Substance>> STREAM_CODEC =
            ByteBufCodecs.holderRegistry(SUBSTANCE_REGISTRY_KEY);
    public static final int DEFAULT_COLOR = 0;

    private final String name;
    private final Substance.Properties properties;
    private final DataComponentMap components;

    public Substance(String name, @NotNull Properties properties) {
        this.name = name;
        this.properties = properties;
        components = properties.components.build();
    }

    public void onContact(SubstanceStack stack, LivingEntity entity) {
        properties.onContactTick.accept(entity);
        for (MobEffectInstance effect : properties.contactEffects) {
            entity.addEffect(new MobEffectInstance(effect));
        }
        if (properties.applyDrugOnContact && properties.drug != null) {
            DrugHelper.ingestDrug(entity, new DrugInstance(properties.drug, stack.getVolume()));
        }
    }

    public void onConsume(SubstanceStack stack, LivingEntity entity) {
        if (properties.drug != null) {
            DrugHelper.ingestDrug(entity, new DrugInstance(properties.drug, stack.getVolume()));
        }
    }

    /**
     * Creates a SubstanceStack with 1 mole and temperature of 273.15K.
     * @return A new SubstanceStack instance.
     */
    public SubstanceStack toStack() {
        return new SubstanceStack(this, 1, 273.15f);
    }

    public String getName() {
        return name;
    }

    public DataComponentMap components() {
        return components;
    }

    public EnumMap<Nature, Float> getNature() {
        return properties.nature;
    }

    /**
     * Calculates the molar volume of the substance in cm³/mol.
     * @return Molar volume in cm³/mol.
     */
    public float getMolarVolume() {
        return properties.molarMass / properties.density;
    }

    /**
     * Gets the slipperiness of the substance.
     * @return Slipperiness value (0.0 - 1.0).
     */
    public float getSlipperiness() {
        return properties.slipperiness;
    }

    public int getViscosity() {
        return properties.viscosity;
    }

    public String getFlavor() {
        return properties.flavor;
    }

    public String getSmell() {
        return properties.smell;
    }

    public Integer getColor() {
        return properties.color;
    }

    public @NotNull Holder<Substance> builtInRegistryHolder() {
        return SUBSTANCE_REGISTRY.get(SUBSTANCE_REGISTRY.getId(this)).orElseThrow();
    }

    public static class Properties {
        EnumMap<Nature, Float> nature = new EnumMap<>(Nature.class);
        float molarMass = 18.02f; // g/mol
        float density = 1; // g/cm^3
        float slipperiness = 0.1f;
        int viscosity = 1000; //
        int color = DEFAULT_COLOR;
        String flavor = "";
        String smell = "";
        Holder<Drug> drug;
        Consumer<LivingEntity> onContactTick = entity -> {};
        List<MobEffectInstance> contactEffects = new ArrayList<>();
        private boolean applyDrugOnContact = false;
        private final DataComponentMap.Builder components = DataComponentMap.builder();

        public Properties nature(Nature nature, float intensity) {
            this.nature.put(nature, intensity);
            return this;
        }

        public Properties molarMass(float molarMass) {
            this.molarMass = molarMass;
            return this;
        }

        public Properties density(float density) {
            this.density = density;
            return this;
        }

        public Properties slipperiness(float slipperiness) {
            this.slipperiness = slipperiness;
            return this;
        }

        public Properties viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public Properties color(int color) {
            this.color = color;
            return this;
        }

        public Properties flavor(String flavor) {
            this.flavor = flavor;
            return this;
        }

        public Properties smell(String smell) {
            this.smell = smell;
            return this;
        }

        public Properties drug(Holder<Drug> drug) {
            this.drug = drug;
            return this;
        }

        public Properties onContactTick(Consumer<LivingEntity> effect) {
            this.onContactTick = effect;
            return this;
        }

        public Properties contactEffects(MobEffectInstance... effects) {
            contactEffects = List.of(effects);
            return this;
        }

        public Properties contactEffect(MobEffectInstance effect) {
            this.contactEffects.add(effect);
            return this;
        }

        public Properties applyDrugOnContact() {
            this.applyDrugOnContact = true;
            return this;
        }

        public <T> Properties component(DataComponentType<T> component, T value) {
            CommonHooks.validateComponent(component);
            components.set(component, value);
            return this;
        }
    }
}
