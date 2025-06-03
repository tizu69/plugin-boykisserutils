package dev.tizu.boykisserutils;

import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ResizeUtil {
    // default minecraft player height reference: 180cm, 1bl = 1m = 16px
    // Map<unit, 1m in unit>
    private static final Map<String, Float> HEIGHTMAP = Map.ofEntries(
            // in-game
            Map.entry("px", 1f / 16.0f),
            Map.entry("steve", 1.8f),
            Map.entry("%", 1.8f / 100.0f),
            // real-world
            Map.entry("cm", 0.01f),
            Map.entry("m", 1.0f),
            Map.entry("in", 0.0254f),
            Map.entry("inch", 0.0254f),
            Map.entry("ft", 0.3048f),
            Map.entry("foot", 0.3048f),
            Map.entry("yd", 0.9144f),
            Map.entry("yard", 0.9144f),
            // people
            Map.entry("nico", 1.65f),
            Map.entry("tizu", 1.87f),
            // joke
            Map.entry("cat", 0.24f),
            Map.entry("lns", 0.3f),
            Map.entry("catgirl", 1.62f));
    private static final Pattern HEIGHT_REGEX = Pattern.compile(
            "^(-?\\d*\\.?\\d+)?\s*([%a-zA-Z]+)?$");
    private static final float MAX_HEIGHT = 8.0f;

    public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND = Commands.literal("height")
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
                    height = roundTo2(getParsedHeight(sizestr));
                    // minecraft has a hard limit on client for this
                    if (height <= 0.4f)
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

    /**
     * Parses a height string into a float.
     * 
     * @returns The parsed height in meters.
     * @exception IllegalStateException If the height string is invalid.
     * @credit ElNico56 for function name.
     */
    static float getParsedHeight(String s) {
        var m = HEIGHT_REGEX.matcher(s);
        if (!m.matches())
            throw new IllegalStateException("Invalid height");

        // default of 1.0 (100% player) in case of no number
        var num = 1.0f;
        if (m.group(1) != null)
            num = Float.parseFloat(m.group(1));

        // default to steves if no unit
        var unit = m.group(2);
        if (unit == null)
            unit = "steve";
        // tell the users the accepted units if they fucked up big time
        if (HEIGHTMAP.get(unit) == null)
            throw new IllegalStateException("Invalid unit, expected one of: " +
                    String.join(", ", HEIGHTMAP.keySet()));

        return num * HEIGHTMAP.get(unit);
    }

    private static float roundTo2(float f) {
        return (float) Math.round(f * 100) / 100;
    }
}
