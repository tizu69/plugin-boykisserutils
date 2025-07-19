package dev.tizu.boykisserutils.tweaks;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.tizu.boykisserutils.ThisPlugin;
import dev.tizu.boykisserutils.util.DistanceUnits;
import dev.tizu.boykisserutils.util.TimeUnits;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TimerTweak {
	public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("timer")
			.then(Commands.argument("duration", StringArgumentType.greedyString()).executes(context -> {
				var sender = context.getSource().getSender();
				var durastr = context.getArgument("duration", String.class);

				var executor = context.getSource().getExecutor();
				if (!(executor instanceof Player)) {
					sender.sendMessage(Component.text(
							"Only players can use this command!", NamedTextColor.RED));
					return 0;
				}
				var player = (Player) executor;

				int dura;
				try {
					dura = TimeUnits.parse(durastr);
				} catch (IllegalStateException e) {
					sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
					return 0;
				}

				Bukkit.getScheduler().runTaskLater(ThisPlugin.instance, () -> {
					executor.sendMessage(Component.text("Your " + durastr + " timer is up!", NamedTextColor.RED));
					for (var i = 0; i < 10; i++)
						Bukkit.getScheduler().runTaskLater(ThisPlugin.instance, () -> {
							player.playSound(Sound.sound(Key.key("block.note_block.bell"),
									Source.MASTER, 1, 0.6f));
						}, i * 2l);
				}, dura);

				executor.sendMessage(Component.text("Timer set for " + durastr));
				return Command.SINGLE_SUCCESS;
			}));
}
