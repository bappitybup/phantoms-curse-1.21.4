package net.bappity;

import net.bappity.network.SyncIrregularityClientHandler;
import net.fabricmc.api.ClientModInitializer;

public class PhantomsCurseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SyncIrregularityClientHandler.register();
    }
}