package net.bappity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientSleepManager {
    private static final Set<UUID> irregularPlayers = new HashSet<>();

    public static void addIrregularPlayer(UUID playerUuid) {
        irregularPlayers.add(playerUuid);
    }

    public static void removeIrregularPlayer(UUID playerUuid) {
        irregularPlayers.remove(playerUuid);
    }

    public static boolean isPlayerIrregular(UUID playerUuid) {
        return irregularPlayers.contains(playerUuid);
    }

    public static void clear() {
        irregularPlayers.clear();
    }
}