package net.bappity.mixin;

import net.bappity.SleepManager;
import net.bappity.network.SyncIrregularityPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinPlayerJoin {

    @Inject(method = "onSpawn", at = @At("HEAD"))
    private void onPlayerJoin(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        boolean isIrregular = SleepManager.isPlayerIrregular(player);

        // Send the irregularity status to the client
        SyncIrregularityPacket.send(player, isIrregular);
    }
}