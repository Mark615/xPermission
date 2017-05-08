package de.mark615.xpermission.object;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.mark615.xpermission.SettingManager;

public class XPlayerSubject
{
	private static long DEFAULTVALID_TIME = (60 * 1000);
	private String lastWorld = null;
	private long lastRequest = 0;
	
	private Map<UUID, Map<String, Integer>> permission = null;
	private UUID uuid = null;
	private XPermissible permissible = null;
	private XPermissionTree tree = null;
	private Group group = null;
	private long firstLogin = 0;
	private long lastLogin = 0;
	private long afkStartTime = 0;
	private long afkTime = 0;
	private boolean afk;
	
	public XPlayerSubject(UUID uuid, XPermissible permissible, ConfigurationSection section)
	{
		this.uuid = uuid;
		this.permissible = permissible;
		this.permission = new HashMap<>();
		this.lastLogin = System.currentTimeMillis();
		if (section != null)
		{
			this.firstLogin = section.getLong("firstLogin", 0);
			section.set("lastLogin", this.lastLogin);
		}
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
	
	public void removePermission(UUID uuid)
	{
		if (uuid == null)
			return;
		permission.remove(uuid);
	}
	
	public void clearPermission()
	{
		permission.clear();
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
	
	public XPermissionTree getPermissions(Player p)
	{
		boolean reload = true;
		if (lastWorld == null)
			lastWorld = p.getWorld().getName();
		else
		if (lastWorld.equalsIgnoreCase(p.getWorld().getName()))
			reload = false;
		
		if ((System.currentTimeMillis() - lastRequest) > DEFAULTVALID_TIME)
			reload = true;
		
		if (reload)
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
	
	public XPermissible getXPermissible()
	{
		return permissible;
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
	
	public void setAfkMode(boolean value)
	{
		if (this.afk == value)
			return;

		this.afk = value;
		if (value)
			this.afkStartTime = System.currentTimeMillis();
		else
			this.afkTime = this.afkTime + (System.currentTimeMillis() - this.afkStartTime);
	}
	
	public boolean isAfk()
	{
		return this.afk;
	}
	
	public long getAfkTime()
	{
		return this.afkTime;
	}
	
	public void setGroup(Group group)
	{
		this.group = group;
	}
	
	public Group getGroup()
	{
		return this.group;
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(uuid);
	}
	
	

}
