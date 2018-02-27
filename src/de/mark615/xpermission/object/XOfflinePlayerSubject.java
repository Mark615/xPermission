package de.mark615.xpermission.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import de.mark615.xpermission.SettingManager;

public class XOfflinePlayerSubject
{
	protected static long DEFAULTVALID_TIME = (60 * 1000);
	protected long lastRequest = 0;
	
	protected Map<UUID, Map<String, Integer>> permission = null;
	protected UUID uuid = null;
	protected XPermissionTree tree = null;
	protected Group group = null;
	protected List<String> groups = null;
	protected long firstLogin = 0;
	protected long lastLogin = 0;
	protected long afkStartTime = 0;
	protected long afkTime = 0;
	protected boolean afk = false;
	protected String prefix = null;
	protected String suffix = null;
	
	public XOfflinePlayerSubject(UUID uuid, ConfigurationSection section)
	{
		this.uuid = uuid;
		this.permission = new HashMap<>();
		this.groups = new ArrayList<>();
		this.lastLogin = System.currentTimeMillis();
		if (section != null)
		{
			this.firstLogin = section.getLong("firstLogin", 0);

			this.prefix = SettingManager.getInstance().getPlayerPrefix(uuid);
			this.suffix = SettingManager.getInstance().getPlayerSuffix(uuid);
			this.groups = SettingManager.getInstance().getXPlayerSubjectGroups(uuid);
		}
	}
	
	public void reload()
	{
		this.permission = new HashMap<>();
		this.groups = new ArrayList<>();

		this.prefix = SettingManager.getInstance().getPlayerPrefix(uuid);
		this.suffix = SettingManager.getInstance().getPlayerSuffix(uuid);
		this.groups = SettingManager.getInstance().getXPlayerSubjectGroups(uuid);
	}
	
	public void setPermission(UUID uuid, String perm, int value)
	{
		if (perm == null)
			return;
		
		if (permission.get(uuid) != null)
		{
			permission.get(uuid).put(perm, Integer.valueOf(value));
		}
		else
		{
			Map<String, Integer> map = new HashMap<>();
			map.put(perm, Integer.valueOf(value));
			permission.put(uuid, map);
		}
	}
	
	public XPermissionTree getPermissionTree()
	{
		boolean reload = true;		
		if ((System.currentTimeMillis() - lastRequest) > DEFAULTVALID_TIME)
			reload = true;
		
		if (reload || (tree == null))
		{
			tree = new XPermissionTree();
			for (UUID uuid : permission.keySet())
			{
				for (String key : permission.get(uuid).keySet())
				{
					tree.addPermission(key, permission.get(uuid).get(key));
				}
			}
			lastRequest = System.currentTimeMillis();
		}
		
		return tree;
	}
	
	public Map<String, Boolean> getPermissions()
	{
		Map<String, Boolean> perms = new HashMap<>();
		for (UUID uuid : permission.keySet())
		{
			for (String key : permission.get(uuid).keySet())
			{
				perms.put(key, permission.get(uuid).get(key) == 1 ? true : false);
			}
		}
		
		return perms;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}

	public long getLastLogin()
	{
		return lastLogin;
	}

	public long getFirstLogin()
	{
		return firstLogin;
	}

	public long getTotalGameTime()
	{
		long time = System.currentTimeMillis() - firstLogin;
		if (!SettingManager.getInstance().hasRankCountAFKTime())
			time = time - afkStartTime;
		
		return time;
	}

	public long getGameTime()
	{
		long time = System.currentTimeMillis() - lastLogin;
		if (!SettingManager.getInstance().hasRankCountAFKTime())
			time = time - afkStartTime;
		
		return time;
	}
	
	public boolean isAfk()
	{
		return this.afk;
	}
	
	public long getAfkTime()
	{
		return this.afkTime;
	}
	
	public void setAfkMode()
	{
		this.afkTime = System.currentTimeMillis();
	}
	
	public Group getGroup()
	{
		return this.group;
	}
	
	public String getPrefix()
	{
		return prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public boolean inGroup(String group)
	{
		return groups.contains(group);
	}
	
	public List<String> getGroupList()
	{
		return groups;
	}
	
	

}
