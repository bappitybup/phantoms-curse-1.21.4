package net.bappity;

import net.bappity.network.ServerSyncIrregularityPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEvents {
    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            boolean isIrregular = SleepManager.isPlayerIrregular(player);
            ServerSyncIrregularityPacket.send(player, isIrregular);
        });
    }
}