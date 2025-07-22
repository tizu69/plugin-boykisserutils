package dev.tizu.boykisserutils.tweaks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import dev.tizu.boykisserutils.ThisPlugin;
import dev.tizu.boykisserutils.util.TimeUnits;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TimerTweak {
	private static final Map<UUID, List<Timer>> timers = new HashMap<>();

	public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("timer")
			.then(Commands.argument("duration", StringArgumentType.string())
					.executes(c -> TimerTweak.createNew(c, false))
					.then(Commands.argument("name", StringArgumentType.greedyString())
							.executes(c -> TimerTweak.createNew(c, true))))
			.then(Commands.literal("list").executes(c -> TimerTweak.listAll(c)))
			.then(Commands.literal("cancel").executes(c -> TimerTweak.cancelTimer(c, false))
					.then(Commands.argument("id", IntegerArgumentType.integer(1))
							.executes(c -> TimerTweak.cancelTimer(c, true))));

	static int createNew(CommandContext<CommandSourceStack> context, boolean withName) {
		var sender = context.getSource().getSender();
		var durastr = context.getArgument("duration", String.class);
		var namestr = !withName ? "no name" : context.getArgument("name", String.class);

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

		var tasks = timers.getOrDefault(executor.getUniqueId(), new ArrayList<>());
		var task = Bukkit.getScheduler().runTaskLater(ThisPlugin.instance, () -> {
			executor.sendMessage(Component.text("Your " + durastr +
					" timer is up: " + namestr, NamedTextColor.RED));
			for (var i = 0; i < 10; i++)
				Bukkit.getScheduler().runTaskLater(ThisPlugin.instance, () -> {
					player.playSound(Sound.sound(Key.key("block.note_block.bell"),
							Source.MASTER, 1, 0.6f));
				}, i * 2l);
			deleteExpired();
		}, dura);
		tasks.add(new Timer(task, System.currentTimeMillis(), dura, namestr));
		timers.put(executor.getUniqueId(), tasks);

		executor.sendMessage(Component.text("Timer set for " + durastr));
		return Command.SINGLE_SUCCESS;
	}

	static int listAll(CommandContext<CommandSourceStack> context) {
		var sender = context.getSource().getSender();
		var executor = context.getSource().getExecutor();
		if (!(executor instanceof Player)) {
			sender.sendMessage(Component.text(
					"Only players can use this command!", NamedTextColor.RED));
			return 0;
		}
		var player = (Player) executor;

		var tasks = timers.getOrDefault(executor.getUniqueId(), new ArrayList<>());
		if (tasks.isEmpty()) {
			player.sendMessage(Component.text("You have no timers!", NamedTextColor.RED));
			return Command.SINGLE_SUCCESS;
		}
		for (var i = 0; i < tasks.size(); i++)
			player.sendMessage(Component.text("Timer " + (i + 1) + ": "
					+ tasks.get(i).unparse() + " left (" + tasks.get(i).name + ")"));
		return Command.SINGLE_SUCCESS;
	}

	static int cancelTimer(CommandContext<CommandSourceStack> context, boolean withId) {
		var sender = context.getSource().getSender();
		var id = withId ? context.getArgument("id", Integer.class) : null;

		var executor = context.getSource().getExecutor();
		if (!(executor instanceof Player)) {
			sender.sendMessage(Component.text(
					"Only players can use this command!", NamedTextColor.RED));
			return 0;
		}
		var player = (Player) executor;

		var tasks = timers.getOrDefault(executor.getUniqueId(), new ArrayList<>());
		if (id == null) {
			for (var task : tasks)
				task.cancel();
			player.sendMessage(Component.text(tasks.size() + " timer" + (tasks.size() == 1 ? ""
					: "s") + " cancelled"));
			timers.remove(executor.getUniqueId());
		} else {
			if (id < 1 || id > tasks.size()) {
				player.sendMessage(Component.text("Timer " + id + " does not exist!", NamedTextColor.RED));
				return Command.SINGLE_SUCCESS;
			}
			var name = tasks.get(id - 1).name;
			tasks.get(id - 1).cancel();
			tasks.remove(id - 1);
			player.sendMessage(Component.text("Timer " + id + " cancelled: " + name));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static void deleteExpired() {
		for (var entry : timers.entrySet()) {
			var tasks = entry.getValue();
			for (var i = tasks.size() - 1; i >= 0; i--)
				if (tasks.get(i).isExpired()) {
					tasks.get(i).cancel();
					tasks.remove(i);
				}
		}
	}

	public record Timer(BukkitTask task, long start, long duration, String name) {
		public void cancel() {
			task.cancel();
		}

		public String unparse() {
			return TimeUnits.unparse((int) (duration - (System.currentTimeMillis() - start) / 50));
		}

		public boolean isExpired() {
			return Bukkit.getScheduler().isQueued(task.getTaskId());
		}
	}
}
