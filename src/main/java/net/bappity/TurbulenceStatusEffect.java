package net.bappity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class TurbulenceStatusEffect extends StatusEffect {
    public TurbulenceStatusEffect() {
        // HARMFUL effect with an arbitrary color
        super(StatusEffectCategory.HARMFUL, 0x5A6DA0);
    }

    // This effect can be used (for example) on the client to scramble elytra controls.
}