package acoustic.main;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventHandlers implements Listener {
	Main plugin;

	public EventHandlers(Main plugin) {
		super();
		this.plugin = plugin;
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		ItemStack item = event.getItemInHand();
		Player player  = event.getPlayer();
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasLore()) return;
		if(item.getItemMeta().getLore().contains("An acoustic panel")) {
			plugin.panellocs.add(block.getLocation());
			player.sendMessage(ChatColor.GRAY + "Placed a panel");
		}
	}

	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player  = event.getPlayer();
		if(plugin.panellocs.contains(block.getLocation())) {
			plugin.panellocs.remove(block.getLocation());
			event.setDropItems(false);
			ItemStack panel = new ItemStack(Material.WHITE_WOOL, 1);
	        ItemMeta panelMeta = panel.getItemMeta();
	        
	        ArrayList<String> lore = new ArrayList<String>();
	        lore.add("An acoustic panel");
	        lore.add("Made for Christmas 2021");
	        panelMeta.setLore(lore);
	        panelMeta.setDisplayName(ChatColor.GRAY + "Acoustic Panel");
	        panel.setItemMeta(panelMeta);
			block.getWorld().dropItemNaturally(block.getLocation(), panel);
			player.sendMessage(ChatColor.GRAY + "Destroyed a panel");
		}
	}
}
