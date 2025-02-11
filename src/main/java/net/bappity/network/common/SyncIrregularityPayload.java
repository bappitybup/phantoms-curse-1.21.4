package net.bappity.network.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record SyncIrregularityPayload(boolean isIrregular, UUID playerUuid) implements CustomPayload {
    public static final CustomPayload.Id<SyncIrregularityPayload> ID = new CustomPayload.Id<>(Identifier.of("bappity", "sync_irregularity"));

    public static final PacketCodec<PacketByteBuf, SyncIrregularityPayload> CODEC = PacketCodec.of(
        (payload, buf) -> {
            buf.writeBoolean(payload.isIrregular());
            buf.writeUuid(payload.playerUuid());
        },
        buf -> new SyncIrregularityPayload(buf.readBoolean(), buf.readUuid())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}