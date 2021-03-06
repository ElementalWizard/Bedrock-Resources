package com.alexvr.bedres.command;

import com.alexvr.bedres.network.Networking;
import com.alexvr.bedres.network.packets.PacketOpenGui;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;

public class CommandHelp implements Command<CommandSource> {

    private static final CommandHelp CMD = new CommandHelp();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("commands")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        Networking.INSTANCE.sendTo(new PacketOpenGui(), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        return 0;
    }

}
