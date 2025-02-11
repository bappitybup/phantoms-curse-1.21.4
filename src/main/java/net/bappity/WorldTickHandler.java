package net.bappity;

import net.minecraft.server.world.ServerWorld;

public class WorldTickHandler {

    public static void onWorldTick(ServerWorld world) {
        long timeOfDay = world.getTimeOfDay();
        // Check if it's dawn (e.g., timeOfDay % 24000 == 0)
        if (timeOfDay % 24000 == 0) {
            SleepManager.resetSleepData(world.getServer());
        }
    }
}