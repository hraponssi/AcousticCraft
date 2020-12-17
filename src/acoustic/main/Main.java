package acoustic.main;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	private ProtocolManager protocolManager;
	ArrayList<Location> panellocs = new ArrayList<Location>();
	ArrayList<Player> panelpeople = new ArrayList<Player>();
	public ConfigManager configManager;
	public EventHandlers eventhandlers;
	public Commands commands;

	public void onDisable() {
		saveAcousticData();
		PluginManager pm = getServer().getPluginManager();
		pm.removePermission(new Permissions().craft);
		pm.removePermission(new Permissions().panelcraft);
	}

	public void onEnable() {
		protocolManager = ProtocolLibrary.getProtocolManager();
		eventhandlers = new EventHandlers(this);
		commands = new Commands(this);
		getServer().getPluginManager().registerEvents(eventhandlers, this);
        configManager = new ConfigManager(this);
		PluginManager pm = getServer().getPluginManager();
		pm.addPermission(new Permissions().craft);
		pm.addPermission(new Permissions().panelcraft);
		configManager.setup();
		setConfig();
		loadConfig();
		loadAcousticData();
		getCommand("acoustic").setExecutor(commands);
		
		ItemStack panel = new ItemStack(Material.WHITE_WOOL, 1);
        ItemMeta panelMeta = panel.getItemMeta();
        
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("An acoustic panel");
        lore.add("Made for Christmas 2020");
        panelMeta.setLore(lore);
        panelMeta.setDisplayName(ChatColor.GRAY + "Acoustic Panel");
        panel.setItemMeta(panelMeta);
		 
		ShapedRecipe Panel = new ShapedRecipe(panel);
		 
		Panel.shape("ICI","IWI","ICI");
		 
		Panel.setIngredient('W', Material.WHITE_WOOL);
		Panel.setIngredient('C', Material.WHITE_CARPET);
		Panel.setIngredient('I', Material.STICK);
		 
		getServer().addRecipe(Panel);
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Location panel : panellocs) {
					if(panel.getBlock().getType() != Material.WHITE_WOOL) {
						panellocs.remove(panel);
					}
				}
			}
		}, 20L, 20L);
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					panelpeople.remove(player);
					Location ploc = player.getLocation();
					for(Location loc : panellocs) {
						if(ploc.getWorld() == loc.getWorld()) {
							if((loc.getBlockZ()-ploc.getBlockZ()) < 4 && (loc.getBlockZ()-ploc.getBlockZ()) > -4) {
								if((loc.getBlockY()-ploc.getBlockY()) < 4 && (loc.getBlockY()-ploc.getBlockY()) > -4) {
									if((loc.getBlockX()-ploc.getBlockX()) < 4 && (loc.getBlockX()-ploc.getBlockX()) > -4) {
						                panelpeople.add(player);
						                //if(player.hasPermission(new Permissions().craft)) player.sendMessage("your near a panel");
									}
								}
							}
						}
					}
				}
			}
		}, 20L, 20L);
	    // Disable all sound effects
	    protocolManager.addPacketListener(
	      new PacketAdapter(this, ListenerPriority.NORMAL,
	              PacketType.Play.Server.NAMED_SOUND_EFFECT) {
	        @Override
	        public void onPacketSending(PacketEvent event) {
	            // Item packets (id: 0x29)
	            if (event.getPacketType() ==
	                    PacketType.Play.Server.NAMED_SOUND_EFFECT) {
	            	//event.getPlayer().sendMessage("sound played");
	            	if(panelpeople.contains(event.getPlayer())) {
		                event.setCancelled(true);
	            	}
	            }
	        }
	    });
	}
	
	public void saveAcousticData() {
		//TODO: add a backup function?
		List<String> warns = configManager.getAcousticData().getStringList("panels");
		configManager.getAcousticData().set("panels", warns);
		warns.clear();
        for (Location loc : panellocs) {
            warns.add(loc.getBlockX() +  ":" +  loc.getBlockY() + ":" + loc.getBlockZ() + ":"+ loc.getWorld().getName());
        }
        configManager.saveAcousticData();
	}
	
	public void loadConfig() {
		reloadConfig();
		FileConfiguration config = this.getConfig();
		//offlinemode = config.getBoolean("offlinemode");
	}

	public void loadAcousticData() {
		List<String> locs = configManager.getAcousticData().getStringList("panels");
		for(String key: locs) {
				String[] splitted = key.split(":");
				panellocs.add(new Location(Bukkit.getWorld(splitted[3]), Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2])));
        }
	}
	
	public void setConfig() {
		File f = new File(this.getDataFolder() + File.separator + "config.yml");
		if (f.exists()) {
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		try (InputStream defConfigStream = this.getResource("config.yml");
				InputStreamReader reader = new InputStreamReader(defConfigStream,StandardCharsets.UTF_8)){
			FileConfiguration defconf = YamlConfiguration.loadConfiguration(reader);
			config.addDefaults(defconf);
			config.setDefaults(defconf);
			this.saveDefaultConfig();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
