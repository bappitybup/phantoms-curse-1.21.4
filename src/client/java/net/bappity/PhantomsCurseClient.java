package net.bappity;

import net.bappity.network.ClientSyncIrregularityPacket;
import net.fabricmc.api.ClientModInitializer;

public class PhantomsCurseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSyncIrregularityPacket.register();
    }
}