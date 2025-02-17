package net.bappity.mixin.client;

import net.bappity.SleepManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ClientMixinLivingEntity {
    @Inject(
        method = "playSound(Lnet/minecraft/sound/SoundEvent;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelPhantomSounds(SoundEvent sound, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (entity instanceof PhantomEntity) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && !SleepManager.isPlayerIrregular(player)) {
                ci.cancel(); // Cancel sound if player isn't irregular
            }
        }
    }
}
