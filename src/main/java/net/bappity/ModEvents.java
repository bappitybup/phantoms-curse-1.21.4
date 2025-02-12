package net.bappity;

import net.bappity.network.SyncIrregularityPacket;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEvents {
    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            boolean isIrregular = SleepManager.isPlayerIrregular(player.getUuid());
            SyncIrregularityPacket.send(player, isIrregular);
        });
    }
}