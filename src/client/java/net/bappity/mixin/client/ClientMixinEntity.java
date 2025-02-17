package net.bappity.mixin.client;

import net.bappity.SleepManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.Box;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ClientMixinEntity {
    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    private void onGetBoundingBox(CallbackInfoReturnable<Box> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof PhantomEntity && self.getWorld().isClient()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && !SleepManager.isPlayerIrregular(player)) {
                cir.setReturnValue(new Box(0, 0, 0, 0, 0, 0)); // Empty bounding box
            }
        }
    }
}