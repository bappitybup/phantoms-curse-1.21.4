package net.bappity.mixin;

import net.bappity.SleepManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomEntity.class)
public abstract class ServerMixinPhantomEntity extends FlyingEntity {
    private int disintegrationTimer = -1;
    private final int totalDisintegrationTime = 30;

    protected ServerMixinPhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super((EntityType<? extends FlyingEntity>) entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void serverTick(CallbackInfo ci) {
        BlockPos pos = this.getBlockPos();

        if (this.getWorld().isDay() && this.getWorld().isSkyVisible(pos)) {
            if (disintegrationTimer < 0) {
                disintegrationTimer = totalDisintegrationTime;
            }
        }

        if (disintegrationTimer > 0) {
            disintegrationTimer--;
            if (disintegrationTimer == 0) {
                this.remove(RemovalReason.DISCARDED);
            }
        }

        // Keep server-only behavior
        handleTargetingAndSpecialMoves();
    }

    private void handleTargetingAndSpecialMoves() {
        LivingEntity target = this.getTarget();
        if (target instanceof ServerPlayerEntity player) {
            if (!SleepManager.isPlayerIrregular(player)) {
                this.setTarget(null);
            }
        }
    }

    @Override
    public boolean isAffectedByDaylight() {
        return false;
    }
}