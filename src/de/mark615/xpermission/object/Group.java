package de.mark615.xpermission.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class Group {

	private String name = null;
	private boolean defaultGroup = false;
	private String prefix = null;
	private String suffix = null;
	private String inheriance = null;
	private int rank = 0;
	private long upgrade = 0;
	private List<String> permissions;
	
	public Group(ConfigurationSection section)
	{
		permissions = new ArrayList<>();
		name = section.getName();
		prefix = section.getString("prefix", "§f");
		suffix = section.getString("suffix", "§f");
		defaultGroup = section.getBoolean("default", false);
		inheriance = section.getString("inheriance", "");
		rank = section.getInt("rank", 1);
		upgrade = section.getInt("upgrade", 0);
		permissions = section.getStringList("permission");
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
	
	public List<String> getPermissions()
	{
		return permissions;
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
