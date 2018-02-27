package de.mark615.xpermission.object;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.mark615.xcore.XCore;
import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.object.Updater.UpdateResult;
import de.mark615.xpermission.object.Updater.UpdateType;

public class XUtil
{
	public static void info(String info)
	{
		Bukkit.getLogger().info(XPermission.PLUGIN_NAME + info);
	}

	public static void warning(String severe)
	{
		Bukkit.getLogger().info(XPermission.PLUGIN_NAME + "[WARNING] " + severe);
	}
	
	public static void severe(String severe)
	{
		Bukkit.getLogger().severe(XPermission.PLUGIN_NAME + severe);
	}
	
	public static void severe(String severe, Exception e)
	{
		severe(severe);
		e.printStackTrace();
	}
	
	public static void debug(Exception e)
	{
		e.printStackTrace();
	}
	
	public static String replaceColorCodes(String message)
	{
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static String getMessage(String file)
	{
		String raw = SettingManager.getInstance().getMessage().getString(file);
		if (raw == null)
		{
			raw = file + " (not found in messages.yml)";
		}
		raw = raw.replace("&", "§");
		return raw;
	}
	
	private static void sendMessage(CommandSender sender, String message, boolean prefix)
	{
		message = message.replace("&", "§");
		
		for (String line : message.split("%ln%"))
		{
			if (!(sender instanceof Player)) {
				sender.sendMessage(XCore.PLUGIN_NAME + line);
				return;
			}
			if (!prefix)
				sender.sendMessage(line);
			else
				sender.sendMessage(XPermission.PLUGIN_NAME + line);
		}
	}
	
	private static void sendMessage(CommandSender sender, String message)
	{
		sendMessage(sender, message, false);
	}
	
	public static void sendFileMessage(CommandSender s, String file, ChatColor color)
	{
		String message = getMessage(file);
		if (s instanceof Player)
			message = color + message;
		
		sendMessage(s, message);
	}

	public static void sendFileMessage(CommandSender s, String msg)
	{
		sendMessage(s, getMessage(msg));
	}

	public static void sendFileMessage(CommandSender s, String msg, boolean prefix)
	{
		sendMessage(s, getMessage(msg), prefix);
	}
	
	public static void sendCommandUsage(CommandSender s, String usage)
	{
		usage = ChatColor.RED + usage;
		sendMessage(s, usage, true);
	}
	
	public static void sendCommandInfo(CommandSender s, String info)
	{
		info = ChatColor.GREEN + info;
		sendMessage(s, info, true);
	}
	
	public static void sendCommandHelp(CommandSender s, String help)
	{
		help = ChatColor.YELLOW + help;
		sendMessage(s, help, true);
	}
	
	public static void sendCommandError(CommandSender s, String error)
	{
		error = ChatColor.RED + error;
		sendMessage(s, error, true);
	}
	
	public static void sendMessage(Player p, String info)
	{
		sendMessage(p, info);
	}

	
	
	public static void updateCheck(final JavaPlugin plugin)
	{
		Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (SettingManager.getInstance().hasCheckVersion())
				{
					try
					{
						Updater updater = new Updater(plugin, 266896, plugin.getDataFolder(), UpdateType.NO_DOWNLOAD, true);
						if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
						    XUtil.info("New version available! " + updater.getLatestName());
						}
					}
					catch(Exception e)
					{
						XUtil.severe("Can't check version at Bukkit.com");
					}
				}
			}
		}, 20, 6 * 60 * 60 * 20);
	}
}
