package net.bappity;

import net.bappity.network.SyncIrregularityPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepManager {
    private static final Map<UUID, BlockPos> lastSuccessfulBed = new HashMap<>();

    public static void handleSuccessfulSleep(ServerPlayerEntity player, BlockPos bedPos) {
        UUID uuid = player.getUuid();
        BlockPos previousBed = lastSuccessfulBed.get(uuid);
        lastSuccessfulBed.put(uuid, bedPos);

        // Cast to our custom accessor
        BappityPlayerDataAccessor data = (BappityPlayerDataAccessor) player;
        if (previousBed != null) {
            if (!bedPos.equals(previousBed)) {
                if (!data.isBappityIrregular()) {
                    data.setBappityIrregular(true);
                    SyncIrregularityPacket.send(player, true);
                }
            } else {
                if (data.isBappityIrregular()) {
                    data.setBappityIrregular(false);
                    SyncIrregularityPacket.send(player, false);
                }
            }
        }
    }

    public static boolean isPlayerIrregular(PlayerEntity player) {
        return ((BappityPlayerDataAccessor) player).isBappityIrregular();
    }

    public static void addIrregularPlayer(ServerPlayerEntity player) {
        BappityPlayerDataAccessor data = (BappityPlayerDataAccessor) player;
        if (!data.isBappityIrregular()) {
            data.setBappityIrregular(true);
            SyncIrregularityPacket.send(player, true);
        }
    }

    public static void removeIrregularPlayer(ServerPlayerEntity player) {
        BappityPlayerDataAccessor data = (BappityPlayerDataAccessor) player;
        if (data.isBappityIrregular()) {
            data.setBappityIrregular(false);
            SyncIrregularityPacket.send(player, false);
        }
    }

    public static void resetSleepData(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            BappityPlayerDataAccessor data = (BappityPlayerDataAccessor) player;
            if (data.isBappityIrregular()) {
                data.setBappityIrregular(false);
                SyncIrregularityPacket.send(player, false);
            }
        });
    }
}