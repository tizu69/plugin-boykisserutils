package dev.tizu.boykisserutils;

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
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;

public class CarUtil implements Listener {
    public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("car")
            .then(Commands.argument("bps", IntegerArgumentType.integer(1, 50)).executes(context -> {
                var bps = context.getArgument("bps", Integer.class);
                return spawnCar(context.getSource().getExecutor(), bps, true);
            })
                    .then(Commands.argument("randomStyle", BoolArgumentType.bool()).executes(context -> {
                        var bps = context.getArgument("bps", Integer.class);
                        var randomMapped = context.getArgument("randomStyle", Boolean.class);
                        return spawnCar(context.getSource().getExecutor(), bps, !randomMapped);
                    })))
            .executes(context -> {
                return spawnCar(context.getSource().getExecutor(), 20, true);
            });

    private static int spawnCar(Entity executor, float bps, boolean playerMapped) {
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
