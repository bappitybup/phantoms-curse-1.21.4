package net.bappity;

import net.bappity.network.SyncIrregularityPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhantomsCurse implements ModInitializer {
    public static final String MOD_ID = "phantoms-curse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SleepIrregularityCommand.register(dispatcher);
        });

        // Register event listeners (e.g. for player sleep).
        ModEvents.registerEvents();

        // Register the world tick event listener
        ServerTickEvents.END_WORLD_TICK.register(WorldTickHandler::onWorldTick);

        // Register the payload codec
        SyncIrregularityPacket.register();
    }
}