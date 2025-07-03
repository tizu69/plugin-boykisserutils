package dev.tizu.boykisserutils.tweaks;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class TrowelTweak implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getItem() == null
                || event.getHand() != EquipmentSlot.HAND || !event.getItem().getType().isBlock()
                || player.getInventory().getItemInOffHand().getType() != Material.STONE_SHOVEL)
            return;

        var slots = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            var item = player.getInventory().getItem(i);
            if (item != null && item.getType().isBlock())
                slots.add(i);
        }
        if (slots.size() == 0)
            return;

        var slot = (int) (Math.random() * slots.size());
        player.getInventory().setHeldItemSlot(slots.get(slot));
    }
}
