package net.bappity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class InsomniaStatusEffect extends StatusEffect {
    public InsomniaStatusEffect() {
        // HARMFUL effect with an arbitrary color (you can adjust)
        super(StatusEffectCategory.HARMFUL, 0xA065A0);
    }

    // You can override methods to control tick behavior if needed.
}