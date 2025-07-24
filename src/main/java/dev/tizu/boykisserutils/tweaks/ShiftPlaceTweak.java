package dev.tizu.boykisserutils.tweaks;

import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ShiftPlaceTweak implements Listener {
	@EventHandler
	public static void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().isSneaking())
			return;
		var bs = event.getBlockPlaced().getBlockData();
		if (bs instanceof Directional dir)
			dir.setFacing(dir.getFacing().getOppositeFace());
		else if (bs instanceof Rotatable rot)
			rot.setRotation(rot.getRotation().getOppositeFace());
		event.getBlockPlaced().setBlockData(bs);
	}
}
