package dev.tizu.boykisserutils.tweaks;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.tizu.boykisserutils.ThisPlugin;
import dev.tizu.boykisserutils.util.Units;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CarTweak implements Listener {
    public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("car")
            .then(Commands.argument("speed", StringArgumentType.string()).executes(context -> {
                var bps = context.getArgument("speed", String.class);
                return spawnCar(context.getSource().getExecutor(), bps, 1, true);
            }).then(Commands.literal("minutely").executes(context -> {
                var bpm = context.getArgument("speed", String.class);
                return spawnCar(context.getSource().getExecutor(), bpm, 60, true);
            })).then(Commands.literal("hourly").executes(context -> {
                var bph = context.getArgument("speed", String.class);
                return spawnCar(context.getSource().getExecutor(), bph, 3600, true);
            }))).executes(context -> {
                return spawnCar(context.getSource().getExecutor(), 14, true); // 50km/h
            });

    private static int spawnCar(Entity executor, float bps, boolean playerMapped) {
        if (bps <= 1) {
            executor.sendMessage(Component.text("Cars move forwards.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        } else if (bps > 50) {
            executor.sendMessage(Component.text("Cars don't go that fast!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        if (!(executor instanceof Player player))
            return Command.SINGLE_SUCCESS;

        player.getWorld().spawnEntity(player.getLocation(),
                EntityType.HORSE, SpawnReason.COMMAND, (e) -> {
                    var horse = (Horse) e;

                    customizeHorse(horse, playerMapped ? player.getUniqueId() : UUID.randomUUID());
                    horse.customName(player.displayName().append(
                            Component.text("'s vroom vroom")));

                    var saddle = ItemStack.of(Material.SADDLE);
                    horse.getInventory().setSaddle(saddle);

                    horse.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).setBaseValue(0);
                    horse.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(2.2); // ~20b
                    horse.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(2);
                    horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(bps / 42.16f);
                    horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1024);
                    horse.setHealth(1024);

                    horse.addScoreboardTag("bku__car");
                    horse.addScoreboardTag("bku_kill_on_dismount");
                    horse.setTamed(true);
                    horse.setInvulnerable(true);
                    horse.addPassenger(player);
                });

        return Command.SINGLE_SUCCESS;
    }

    private static int spawnCar(Entity executor, String str, float mul, boolean playerMapped) {
        try {
            var parsed = Units.parse(str, "m", 0.01f);
            return spawnCar(executor, parsed / mul, playerMapped);
        } catch (IllegalStateException e) {
            executor.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
    }

    /** set color and style based on the uuid. */
    private static void customizeHorse(Horse horse, UUID ref) {
        var colorIndex = Math.floorMod(ref.hashCode(), Color.values().length);
        var styleIndex = Math.floorMod(ref.hashCode(), Style.values().length);
        horse.setColor(Color.values()[colorIndex]);
        horse.setStyle(Style.values()[styleIndex]);
    }

    @EventHandler
    public static void onDismount(EntityDismountEvent event) {
        var entity = event.getDismounted();
        // 1 here, as the count seems to only update after the dismount
        if (entity.getPassengers().size() > 1
                || !entity.getScoreboardTags().contains("bku_kill_on_dismount"))
            return;

        entity.remove();
        ThisPlugin.instance.getLogger().info(entity.getName() + " ("
                + entity.getUniqueId().toString() + ") has been killed: had bku_kill_on_dismount "
                + "tag equipped");
    }
}
