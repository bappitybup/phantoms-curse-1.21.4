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
public abstract class ServerMixinLivingEntity {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (((Object)this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
            if (source.getAttacker() instanceof PhantomEntity) {
                if (!SleepManager.isPlayerIrregular(player) || world.isDay()) {
                    cir.cancel();
                }
            }
        }
    }
}