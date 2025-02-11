package net.bappity;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

public class ClientSleepManager {
    private static Set<UUID> irregularPlayers = new HashSet<>();

    public static Set<UUID> getIrregularPlayers() {
        return irregularPlayers;
    }

    public static void setIrregularPlayers(Set<UUID> newSet) {
        irregularPlayers = newSet;
    }
}