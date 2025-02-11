package net.bappity.mixin;

import net.bappity.SleepManager;
import net.bappity.network.common.SyncIrregularityPayload;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        player.networkHandler.sendPacket(new GameMessageS2CPacket(Text.literal(
                isIrregular ? "You are marked as sleep irregular. Phantoms will target you." 
                            : "You are not sleep irregular. Phantoms will ignore you."), false));

        // Send the irregularity status to the client
        //ServerPlayNetworking.send(player, new SyncIrregularityPayload(isIrregular, player.getUuid()));
    }
}