package de.mark615.xpermission.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.mysql.jdbc.interceptors.SessionAssociationInterceptor;

import de.mark615.xpermission.SettingManager;

public class Group {

	private String name = null;
	private String prefix = null;
	private String suffix = null;
	private boolean defaultGroup = false;
	private String inheriance = null;
	private int rank = 0;
	private long upgrade = 0;
	private Map<String, Boolean> permissions;
	
	public Group(ConfigurationSection section)
	{
		permissions = new HashMap<>();
		name = section.getName();
		prefix = section.getString("prefix", "§f");
		suffix = section.getString("suffix", "§f");
		defaultGroup = section.getBoolean("default", false);
		inheriance = section.getString("inheriance", "");
		rank = section.getInt("rank", 1);
		upgrade = section.getInt("upgrade", 0);
		loadPermission(section);
	}
	
	private void loadPermission(ConfigurationSection section)
	{
		List<String> perms = section.getStringList("permissions.groups." + name);
		if (perms != null)
		{
			for (String key : perms)
			{
				SettingManager.getInstance().calculatePermission(permissions, key);
			}
		}
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public String getSuffix()
	{
		return suffix;
	}
	
	public boolean isDefault()
	{
		return defaultGroup;
	}
	
	public String getInheriance()
	{
		return inheriance;
	}
	
	public int getRank()
	{
		return rank;
	}
	
	public boolean hasPermission(String permission)
	{
		return permissions.containsKey(permission) ? permissions.get(permission) : false;
	}
	
	public boolean removePermission(String permission)
	{
		if (!hasPermission(permission))
			return false;
		
		permissions.put(permission, false);
		return true;
	}
	
	public boolean addPermission(String permission)
	{
		if (hasPermission(permission))
			return false;
		
		permissions.put(permission, true);
		return true;
	}
	
	public void setDefault(boolean value)
	{
		this.defaultGroup = value;
	}
	
	public long getUpgradeTime()
	{
		return upgrade;
	}
}