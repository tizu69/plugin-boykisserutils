package dev.tizu.boykisserutils.tweaks;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.tizu.boykisserutils.util.Units;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ResizeTweak {
    private static final float MAX_HEIGHT = 8.0f;

    public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("resize")
            .then(Commands.argument("size", StringArgumentType.greedyString()).executes(context -> {
                var sender = context.getSource().getSender();
                var sizestr = context.getArgument("size", String.class);

                var executor = context.getSource().getExecutor();
                if (!(executor instanceof Player player)) {
                    sender.sendMessage(Component.text(
                            "You must change scale of a player only", NamedTextColor.RED));
                    return 0;
                }

                float height, convheight;
                try {
                    height = Units.parse(sizestr, "steves", 1.8f / 100.0f);
                    // minecraft has a hard limit on client for this
                    if (height <= 2.0f / 16.0f)
                        throw new IllegalStateException("You must be at least somewhat tall");
                    if (height > MAX_HEIGHT)
                        throw new IllegalStateException(
                                "You must be at most " + MAX_HEIGHT + "m tall, not " + height + "m");
                    convheight = roundTo2(height / 1.8f);
                } catch (IllegalStateException e) {
                    sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                    return 0;
                }

                player.getAttribute(Attribute.SCALE).setBaseValue(convheight);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(convheight * 4.5f);
                player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(convheight * 3.0f);
                player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(convheight * 0.42f);
                player.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(convheight * 0.6f);
                player.getAttribute(Attribute.GRAVITY).setBaseValue(convheight * 0.08f);

                if (sender != executor)
                    sender.sendActionBar(Component.text("Changed height to " + height + "m, aka " + convheight + "st"));
                executor.sendActionBar(
                        Component.text("Your height has been changed to " + height + "m, aka " + convheight + "st"));

                return Command.SINGLE_SUCCESS;
            }));

    private static float roundTo2(float f) {
        return (float) Math.round(f * 100) / 100;
    }
}
