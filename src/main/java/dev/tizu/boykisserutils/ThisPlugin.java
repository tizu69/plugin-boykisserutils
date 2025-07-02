package dev.tizu.boykisserutils;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tizu.boykisserutils.tweaks.BlockPlaceTweak;
import dev.tizu.boykisserutils.tweaks.CarTweak;
import dev.tizu.boykisserutils.tweaks.PingTweak;
import dev.tizu.boykisserutils.tweaks.ResizeTweak;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class ThisPlugin extends JavaPlugin implements Listener {
    public static ThisPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new BlockPlaceTweak(), this);
        Bukkit.getPluginManager().registerEvents(new CarTweak(), this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(ResizeTweak.COMMAND.build());
            commands.registrar().register(PingTweak.COMMAND.build());
            commands.registrar().register(CarTweak.COMMAND.build());
        });
    }
}