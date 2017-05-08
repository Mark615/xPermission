package de.mark615.xpermission.object;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;

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
	
	public static void debug(Exception e)
	{
		e.printStackTrace();
	}
	
	public static String getMessage(String msg)
	{
		String raw = SettingManager.getInstance().getMessage().getString(msg);
		if (raw == null)
		{
			raw = msg + " (not found in messages.yml)";
		}
		raw = raw.replace("&", "§");
		return raw;
	}
	
	private static void sendMessage(CommandSender sender, String message)
	{
		message = message.replace("&", "§");
		sender.sendMessage(message);
	}
	
	public static void sendFileMessage(CommandSender s, String msg, ChatColor color)
	{
		String message = getMessage(msg);
		if (s instanceof Player)
			message = color + message;
		
		sendMessage(s, message);
	}

	public static void sendFileMessage(CommandSender s, String msg)
	{
		sendMessage(s, getMessage(msg));
	}
	
	public static void sendCommandUsage(CommandSender s, String usage)
	{
		if (s instanceof Player)
			usage = ChatColor.RED + usage;
		
		sendMessage(s, usage);
	}
	
	public static void sendCommandInfo(CommandSender s, String info)
	{
		if (s instanceof Player)
			info = ChatColor.GREEN + info;
		
		sendMessage(s, info);
	}
	
	public static void sendCommandHelp(CommandSender s, String help)
	{
		if (s instanceof Player)
			help = ChatColor.YELLOW + help;
		
		sendMessage(s, help);
	}
	
	public static void sendCommandError(CommandSender s, String error)
	{
		if (s instanceof Player)
			error = ChatColor.RED + error;
		
		sendMessage(s, error);
	}
}
