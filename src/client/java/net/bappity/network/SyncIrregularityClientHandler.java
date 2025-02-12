package net.bappity.network;

import java.util.UUID;

import net.bappity.ClientSleepManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
            if (isIrregular) {
                context.player().sendMessage(Text.translatable("message.phantoms-curse.irregular_added").formatted(Formatting.RED), true);
                ClientSleepManager.addIrregularPlayer(playerUuid);
            } else {
                context.player().sendMessage(Text.translatable("message.phantoms-curse.irregular_removed").formatted(Formatting.RED), true);
                ClientSleepManager.removeIrregularPlayer(playerUuid);
            }
        });
    }
}