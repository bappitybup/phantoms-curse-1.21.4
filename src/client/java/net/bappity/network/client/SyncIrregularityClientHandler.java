package net.bappity.network.client;

import java.util.UUID;

import net.bappity.SleepManager;
import net.bappity.network.SyncIrregularityPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class SyncIrregularityClientHandler implements ClientPlayNetworking.PlayPayloadHandler<SyncIrregularityPacket.Payload> {
    
    public static void register() {
        // Register only on client side
        PayloadTypeRegistry.playS2C().register(SyncIrregularityPacket.ID, SyncIrregularityPacket.Payload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SyncIrregularityPacket.ID, new SyncIrregularityClientHandler());
    }

    @Override
    public void receive(SyncIrregularityPacket.Payload payload, ClientPlayNetworking.Context context) {
        boolean isIrregular = payload.isIrregular();
        UUID playerUuid = payload.playerUuid();
        context.client().execute(() -> {
            if (context.client().player != null && 
                playerUuid.equals(context.client().player.getUuid())) {
                if (isIrregular) {
                    SleepManager.irregularPlayers.add(playerUuid);
                } else {
                    SleepManager.irregularPlayers.remove(playerUuid);
                }
            }
        });
    }
}