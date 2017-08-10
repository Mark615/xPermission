package de.mark615.xpermission.object;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.mark615.xpermission.SettingManager;

public class XPlayerSubject extends XOfflinePlayerSubject
{
	private String lastWorld = null;
	private XPermissible permissible = null;
	
	public XPlayerSubject(Player p, XPermissible permissible, ConfigurationSection section)
	{
		super(p.getUniqueId(), section);
		this.permissible = permissible;
		
		if (section != null)
		{
			this.firstLogin = section.getLong("firstLogin", 0);
		}
		else
		{
			SettingManager.getInstance().isPlayerFirstJoin(p);
			section = SettingManager.getInstance().getPlayerConfigurationsection(uuid);
		}
		
		section.set("lastLogin", this.lastLogin);
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
	
	@Override
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
	
	public void setGroup(Group group)
	{
		this.group = group;
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(uuid);
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
	
	

}
