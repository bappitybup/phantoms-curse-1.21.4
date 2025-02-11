package net.bappity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
    public static StatusEffect INSOMNIA;
    public static StatusEffect TURBULENCE;

    public static void registerStatusEffects() {
         INSOMNIA = new InsomniaStatusEffect();
         TURBULENCE = new TurbulenceStatusEffect();

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
    }
}