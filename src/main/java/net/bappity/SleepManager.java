package net.bappity;

import net.bappity.network.SyncIrregularityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SleepManager {
    private static final Map<UUID, BlockPos> lastSuccessfulBed = new HashMap<>();
    private static final Set<UUID> irregularPlayers = new HashSet<>();

    public static void handleSuccessfulSleep(ServerPlayerEntity player, BlockPos bedPos) {
        UUID uuid = player.getUuid();
    
        // Get the previous bed BEFORE updating
        BlockPos previousBed = lastSuccessfulBed.get(uuid);
        
        // Update the last successful bed to the current one
        lastSuccessfulBed.put(uuid, bedPos);

        // Now compare against the previous bed (before update)
        if (previousBed != null) {
            if (!bedPos.equals(previousBed)) {
                // Punish for different bed
                if (irregularPlayers.add(uuid)) { // Only send packet if newly added
                    SyncIrregularityPacket.send(player, true);
                }
            } else {
                // Reward for same bed twice consecutively
                if (irregularPlayers.remove(uuid)) { // Only send packet if removed
                    SyncIrregularityPacket.send(player, false);
                }
            }
        }
    }

    public static boolean isPlayerIrregular(ServerPlayerEntity player) {
        return irregularPlayers.contains(player.getUuid());
    }

    public static boolean isPlayerIrregular(UUID playerUuid) {
        return irregularPlayers.contains(playerUuid);
    }

    public static void addIrregularPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (irregularPlayers.add(uuid)) {
            // Only send packet if status actually changed
            SyncIrregularityPacket.send(player, true);
        }
    }

    public static void removeIrregularPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (irregularPlayers.remove(uuid)) {
            // Only send packet if status actually changed
            SyncIrregularityPacket.send(player, false);
        }
    }

    public static void resetSleepData(MinecraftServer server) {
        // Notify clients and clear data at dawn
        for (UUID uuid : irregularPlayers) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) SyncIrregularityPacket.send(player, false);
        }
        irregularPlayers.clear();
    }
}