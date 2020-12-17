package acoustic.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	Main plugin;
	
    public ConfigManager(Main plugin) {
        super();
        this.plugin = plugin;
    }
    
	public FileConfiguration AcousticDatacfg;
	public File AcousticDatafile;

	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		AcousticDatafile = new File(plugin.getDataFolder(), "AcousticData.yml");

		if (!AcousticDatafile.exists()) {
			try {
				AcousticDatafile.createNewFile();
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The AcousticData.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the AcousticData.yml file");
			}
		}

		AcousticDatacfg = YamlConfiguration.loadConfiguration(AcousticDatafile);
	}

	public FileConfiguration getAcousticData() {
		return AcousticDatacfg;
	}

	public void saveAcousticData() {
		try {
			AcousticDatacfg.save(AcousticDatafile);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The AcousticData.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the AcousticData.yml file");
		}
	}

	public void reloadAcousticData() {
		AcousticDatacfg = YamlConfiguration.loadConfiguration(AcousticDatafile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The AcousticData.yml file has been reload");

	}
}
