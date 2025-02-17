package net.bappity.network;

import java.util.UUID;

import net.bappity.ClientSleepManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

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
            ClientPlayerEntity player = context.client().player;
            if (player != null) {
                if (isIrregular) {
                    player.playSound(SoundEvents.ENTITY_PHANTOM_FLAP, 0.5F, 0.1F);
                    ClientSleepManager.addIrregularPlayer(playerUuid);
                } else {
                    // Dereference the SoundEvent from the Reference<SoundEvent>
                    SoundEvent basaltDeltasMood = SoundEvents.AMBIENT_BASALT_DELTAS_MOOD.value();
                    player.playSound(basaltDeltasMood, 0.1F, 1.0F);
                    ClientSleepManager.removeIrregularPlayer(playerUuid);
                }
            }
        });
    }
}