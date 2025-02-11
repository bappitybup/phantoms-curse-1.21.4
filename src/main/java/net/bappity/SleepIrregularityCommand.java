package net.bappity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.bappity.network.SyncIrregularityPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class SleepIrregularityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setSleepIrregularity")
            .requires(source -> source.hasPermissionLevel(2)) // Require OP level
            .then(CommandManager.argument("target", net.minecraft.command.argument.EntityArgumentType.player())
                .then(CommandManager.argument("irregular", BoolArgumentType.bool())
                    .executes(context -> {
                        ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(context, "target");
                        boolean irregular = BoolArgumentType.getBool(context, "irregular");
                        
                        UUID uuid = player.getUuid();
                        if (irregular) {
                            SleepManager.irregularPlayers.add(uuid);
                        } else {
                            SleepManager.irregularPlayers.remove(uuid);
                        }
                        
                        // Sync the change to the client
                        SyncIrregularityPacket.send(player, irregular);
                        
                        // Optional feedback
                        context.getSource().sendFeedback(() -> 
                            net.minecraft.text.Text.literal("Updated sleep irregularity for " + player.getName().getString() + " to " + irregular),
                            true
                        );
                        
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
        );
    }
}