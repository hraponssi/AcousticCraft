package acoustic.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	Main plugin;
    public Commands(Main plugin) {
        super();
        this.plugin = plugin;
    }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(command.equalsIgnoreCase("acoustic")) {
				player.sendMessage(ChatColor.GREEN + "This server has acoustic panels.");
				return true;
			}
		}else {
			sender.sendMessage(ChatColor.RED+"This command can not be run from the console.");
		}
		return false;
	}
	
}
