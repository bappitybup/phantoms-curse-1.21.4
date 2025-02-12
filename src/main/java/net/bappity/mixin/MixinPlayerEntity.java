package net.bappity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.bappity.SleepManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {
    @Inject(
        method = "wakeUp",
        at = @At("HEAD")
    )
    private void onWakeUp(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getWorld().isClient() && player.isSleeping()) {
            BlockPos bedPos = player.getSleepingPosition().orElse(null);
            if (bedPos != null) {
                SleepManager.handleSuccessfulSleep((ServerPlayerEntity) player, bedPos);
            }
        }
    }
}
