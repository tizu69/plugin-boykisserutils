package dev.tizu.boykisserutils;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tizu.boykisserutils.tweaks.CommandBlockTweak;
import dev.tizu.boykisserutils.tweaks.CarTweak;
import dev.tizu.boykisserutils.tweaks.PingTweak;
import dev.tizu.boykisserutils.tweaks.ResizeTweak;
import dev.tizu.boykisserutils.tweaks.ShiftPlaceTweak;
import dev.tizu.boykisserutils.tweaks.TimerTweak;
import dev.tizu.boykisserutils.tweaks.TrowelTweak;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class ThisPlugin extends JavaPlugin implements Listener {
	public static ThisPlugin instance;

	@Override
	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new CarTweak(), this);
		Bukkit.getPluginManager().registerEvents(new CommandBlockTweak(), this);
		Bukkit.getPluginManager().registerEvents(new ShiftPlaceTweak(), this);
		Bukkit.getPluginManager().registerEvents(new TrowelTweak(), this);
		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			commands.registrar().register(CarTweak.COMMAND.build());
			commands.registrar().register(PingTweak.COMMAND.build());
			commands.registrar().register(ResizeTweak.COMMAND.build());
			commands.registrar().register(TimerTweak.COMMAND.build());
		});
	}
}
