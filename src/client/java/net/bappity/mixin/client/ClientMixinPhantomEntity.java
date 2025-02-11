package net.bappity.mixin.client;

import net.bappity.SleepManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomEntity.class)
public abstract class ClientMixinPhantomEntity {
    private int clientDisintegrationTimer = -1;
    private final int totalDisintegrationTime = 30;
    private Vec3d lastVelocity = Vec3d.ZERO;

    @Inject(method = "tick", at = @At("HEAD"))
    private void clientTick(CallbackInfo ci) {
        PhantomEntity phantom = (PhantomEntity) (Object) this;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        
        if (player == null) return;
        
        BlockPos pos = phantom.getBlockPos();
        boolean shouldDisintegrate = phantom.getWorld().isDay() && 
                                     phantom.getWorld().isSkyVisible(pos) && 
                                     SleepManager.isPlayerIrregular(player.getUuid());

        if (shouldDisintegrate) {
            if (clientDisintegrationTimer < 0) {
                clientDisintegrationTimer = totalDisintegrationTime;
            }
            
            double progress = 1.0 - ((double) clientDisintegrationTimer / totalDisintegrationTime);
            handleClientDisintegration(phantom, progress);
            
            clientDisintegrationTimer--;
        } else {
            clientDisintegrationTimer = -1;
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), cancellable = true)
    private void onPlaySound(CallbackInfo ci) {
        // Get the client player
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            // Check if the player is NOT in the irregular list
            if (!SleepManager.isPlayerIrregular(player.getUuid())) {
                // Cancel the ambient sound for phantoms
                ci.cancel();
            }
        }
    }

    private void handleClientDisintegration(PhantomEntity phantom, double progress) {
        if (progress < 0.33) {
            spawnDisintegrationParticles(phantom, false);
        } else if (progress < 0.40) {
            spawnDisintegrationParticles(phantom, false);
        } else {
            if (clientDisintegrationTimer == 2) {
                phantom.setInvisible(true);
                spawnDisintegrationParticles(phantom, true);
                phantom.playSound(SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE, 
                                    0.5f, 1.0f);
            }
            spawnDisintegrationParticles(phantom, false);
        }
    }

    private void spawnDisintegrationParticles(PhantomEntity phantom, boolean isFinalBurst) {
        int fromColor = packRgb(0.000f, 0.000f, 0.000f);
        int toColor = packRgb(0.290f, 1.000f, 0.976f);
        float scale = 1.0f;
        int particleCount = 30;
        double speed = 0.1;
        double radius = 1.0;

        if (isFinalBurst) {
            particleCount = 30;
            speed = 0.5;
            radius = 2.0;
            scale = 2.0f;
        }

        World world = phantom.getWorld();
        for (int i = 0; i < particleCount; i++) {
            if (isFinalBurst) {
                double angle = (2 * Math.PI * i) / particleCount;
                double dx = Math.cos(angle) * speed;
                double dy = (world.random.nextDouble() - 0.5) * speed;
                double dz = Math.sin(angle) * speed;
                
                world.addParticle(
                    new DustColorTransitionParticleEffect(fromColor, toColor, scale),
                    phantom.getX() + Math.cos(angle) * radius,
                    phantom.getY() + phantom.getHeight() / 2 + (world.random.nextDouble() - 0.5) * radius,
                    phantom.getZ() + Math.sin(angle) * radius,
                    dx, dy, dz
                );
            } else {
                world.addParticle(
                    new DustColorTransitionParticleEffect(fromColor, toColor, scale),
                    phantom.getX() + (world.random.nextDouble() - 0.5) * (phantom.getWidth()*2),
                    phantom.getY() + world.random.nextDouble() * phantom.getHeight(),
                    phantom.getZ() + (world.random.nextDouble() - 0.5) * (phantom.getWidth()*2),
                    0.0, speed, 0.0
                );
            }
        }
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
        )
    )
    private void removePhantomParticles(World world, ParticleEffect parameters, 
                                        double x, double y, double z,
                                        double velocityX, double velocityY, double velocityZ) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        PhantomEntity phantom = (PhantomEntity) (Object) this;
        if (parameters == ParticleTypes.MYCELIUM && player != null && 
            !SleepManager.isPlayerIrregular(player.getUuid())) {
                return;
        }
        world.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    private int packRgb(float r, float g, float b) {
        return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }
}