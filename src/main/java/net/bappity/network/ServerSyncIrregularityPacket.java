package net.bappity.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ServerSyncIrregularityPacket {
    public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(
            Identifier.of("bappity", "sync_irregularity"));

    public record Payload(boolean isIrregular, UUID playerUuid) implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, Payload> CODEC = PacketCodec.of(
                (payload, buf) -> {
                    buf.writeBoolean(payload.isIrregular());
                    buf.writeUuid(payload.playerUuid());
                },
                buf -> new Payload(buf.readBoolean(), buf.readUuid()));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void register() {
        // Register the payload type for S2C (Server-to-Client) packets
        PayloadTypeRegistry.playS2C().register(ID, Payload.CODEC);

        // No need to register a global receiver for S2C packets
    }

    public static void send(ServerPlayerEntity player, boolean isIrregular) {
        ServerPlayNetworking.send(player, new Payload(isIrregular, player.getUuid()));
    }
}