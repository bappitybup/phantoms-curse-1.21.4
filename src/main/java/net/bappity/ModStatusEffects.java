package net.bappity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
    public static final StatusEffect INSOMNIA = new InsomniaStatusEffect();
    public static final StatusEffect TURBULENCE = new TurbulenceStatusEffect();

    // RegistryEntry for each status effect
    public static RegistryEntry<StatusEffect> INSOMNIA_ENTRY;
    public static RegistryEntry<StatusEffect> TURBULENCE_ENTRY;

    public static void registerStatusEffects() {
        // Register the status effects
        Registry.register(
            Registries.STATUS_EFFECT, 
            Identifier.of(PhantomsCurse.MOD_ID, "insomnia"), 
            INSOMNIA
        );
        Registry.register(
            Registries.STATUS_EFFECT, 
            Identifier.of(PhantomsCurse.MOD_ID, "turbulence"), 
            TURBULENCE
        );

        // Get the RegistryEntry for each status effect
        INSOMNIA_ENTRY = Registries.STATUS_EFFECT.getEntry(Registries.STATUS_EFFECT.getRawId(INSOMNIA))
            .orElseThrow(() -> new IllegalStateException("Insomnia status effect not registered!"));
        TURBULENCE_ENTRY = Registries.STATUS_EFFECT.getEntry(Registries.STATUS_EFFECT.getRawId(TURBULENCE))
            .orElseThrow(() -> new IllegalStateException("Turbulence status effect not registered!"));
    }
}