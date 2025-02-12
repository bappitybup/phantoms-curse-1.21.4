package net.bappity.mixin;

import net.bappity.ModStatusEffects;
import net.bappity.SleepManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (((Object)this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
            if (source.getAttacker() instanceof PhantomEntity) {
                if (!SleepManager.isPlayerIrregular(player) || world.isDay()) {
                    cir.cancel();
                }

                // Unwrap the Optional to get the RegistryEntry for the insomnia effect.
                RegistryEntry<StatusEffect> insomniaEffect = Registries.STATUS_EFFECT
                        .getEntry(Registries.STATUS_EFFECT.getRawId(ModStatusEffects.INSOMNIA))
                        .orElseThrow(() -> new IllegalStateException("Insomnia status effect not registered!"));
                
                // Apply Insomnia for 60 seconds (1200 ticks)
                if (!player.hasStatusEffect(insomniaEffect)) {
                    player.addStatusEffect(new StatusEffectInstance(
                        insomniaEffect, 
                        1200, 
                        0, 
                        false, 
                        true, 
                        true
                    ));
                }                
            }
        }
    }
}