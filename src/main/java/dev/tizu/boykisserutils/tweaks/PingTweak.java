package dev.tizu.boykisserutils.tweaks;

import org.bukkit.Bukkit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PingTweak {
	public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("ping")
			.executes(context -> {
				var sender = context.getSource().getSender();
				var ping = Bukkit.getServer().getPlayer(sender.getName()).getPing();
				sender.sendMessage(Component.text("Ping! (" + ping + "ms)", NamedTextColor.GREEN));
				return Command.SINGLE_SUCCESS;
			});
}
