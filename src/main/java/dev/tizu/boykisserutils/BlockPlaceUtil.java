package dev.tizu.boykisserutils;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockPlaceUtil implements Listener {
    // HACK: this requires a mod such as https://modrinth.com/plugin/f3nperm to work.
    // the server BKU is built for has this, so I cba to reimplement it.
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                event.getItem().getType() != Material.COMMAND_BLOCK ||
                event.getPlayer().getGameMode() != GameMode.CREATIVE ||
                !event.getPlayer().hasPermission("boykisser.commandblock.place") ||
                !event.getPlayer().hasPermission("minecraft.commandblock"))
            return;

        event.setCancelled(true);
        var targetPos = event.getClickedBlock().getRelative(event.getBlockFace());
        if (targetPos.getType() != Material.AIR)
            return;
        var targetFace = getFacingDirection(event.getPlayer()).getOppositeFace();

        targetPos.setType(Material.COMMAND_BLOCK);
        var directional = (Directional) targetPos.getBlockData();
        directional.setFacing(targetFace);
        targetPos.setBlockData(directional);
    }

    private static BlockFace getFacingDirection(Player player) {
        float pitch = player.getLocation().getPitch();
        if (pitch < -45)
            return BlockFace.UP;
        else if (pitch > 45)
            return BlockFace.DOWN;
        return player.getFacing();
    }

}
