package net.bappity.network;

import java.util.UUID;

import net.bappity.BappityPlayerDataAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ClientSyncIrregularityPacket implements ClientPlayNetworking.PlayPayloadHandler<ServerSyncIrregularityPacket.Payload> {
    
    public static void register() {
        PayloadTypeRegistry.playS2C().register(ServerSyncIrregularityPacket.ID, ServerSyncIrregularityPacket.Payload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ServerSyncIrregularityPacket.ID, new ClientSyncIrregularityPacket());
    }

    @Override
    public void receive(ServerSyncIrregularityPacket.Payload payload, ClientPlayNetworking.Context context) {
        boolean isIrregular = payload.isIrregular();
        UUID playerUuid = payload.playerUuid();
        context.client().execute(() -> {
            ClientPlayerEntity player = context.client().player;
            if (player != null && player.getUuid().equals(playerUuid)) {
                ((BappityPlayerDataAccessor) player).setBappityIrregular(isIrregular);
                
                if (isIrregular) {
                    player.playSound(SoundEvents.ENTITY_PHANTOM_FLAP, 0.5F, 0.1F);
                } else {
                    SoundEvent basaltDeltasMood = SoundEvents.AMBIENT_BASALT_DELTAS_MOOD.value();
                    player.playSound(basaltDeltasMood, 0.1F, 1.0F);
                }
            }
        });
    }
}