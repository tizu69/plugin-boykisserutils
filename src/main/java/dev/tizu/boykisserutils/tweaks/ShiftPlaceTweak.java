package dev.tizu.boykisserutils.tweaks;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInputEvent;

public class ShiftPlaceTweak implements Listener {
	private static Set<UUID> controlling = new HashSet<>();

	@EventHandler
	public static void onBlockPlace(BlockPlaceEvent event) {
		if (!controlling.contains(event.getPlayer().getUniqueId()))
			return;
		var bs = event.getBlockPlaced().getBlockData();
		if (bs instanceof Directional dir)
			dir.setFacing(dir.getFacing().getOppositeFace());
		else if (bs instanceof Rotatable rot)
			rot.setRotation(rot.getRotation().getOppositeFace());
		event.getBlockPlaced().setBlockData(bs);
	}

	@EventHandler
	public static void onPlayerInput(PlayerInputEvent event) {
		if (event.getInput().isSprint())
			controlling.add(event.getPlayer().getUniqueId());
		else
			controlling.remove(event.getPlayer().getUniqueId());
	}
}
