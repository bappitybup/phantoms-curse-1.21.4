package net.bappity;

import net.bappity.network.SyncIrregularityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SleepManager {
    private static final Map<UUID, BlockPos> familiarBeds = new HashMap<>();
    public static final Set<UUID> irregularPlayers = new HashSet<>();

    /**
     * Call this when a player sleeps.
     * If it’s the player’s first sleep, record the bed as “familiar.”
     * If not, mark the player as irregular if they slept in a different bed.
     */
    public static void onPlayerSleep(ServerPlayerEntity player, BlockPos bedPos) {
        UUID uuid = player.getUuid();
        if (!familiarBeds.containsKey(uuid)) {
            familiarBeds.put(uuid, bedPos);
        } else {
            BlockPos familiar = familiarBeds.get(uuid);
            if (!familiar.equals(bedPos)) {
                irregularPlayers.add(uuid);
                // Send packet to the player's client
                SyncIrregularityPacket.send(player, true);
            }
        }
    }

    public static boolean isPlayerIrregular(ServerPlayerEntity player) {
        return irregularPlayers.contains(player.getUuid());
    }

    public static boolean isPlayerIrregular(UUID playerUuid) {
        return irregularPlayers.contains(playerUuid);
    }

    /**
     * Call this at dawn (or when appropriate) to reset the data.
     */
    public static void resetSleepData(MinecraftServer server) {
        // Send packets to all affected players before clearing
        for (UUID uuid : irregularPlayers) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                SyncIrregularityPacket.send(player, false);
            }
        }
        irregularPlayers.clear();
    }
}