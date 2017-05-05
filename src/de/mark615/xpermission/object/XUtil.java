package de.mark615.xpermission.object;

import org.bukkit.Bukkit;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;

public class XUtil
{
	public static void info(String info)
	{
		Bukkit.getLogger().info(XPermission.PLUGIN_NAME + info);
	}
	
	public static void severe(String severe)
	{
		Bukkit.getLogger().severe(XPermission.PLUGIN_NAME + severe);
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
}
