package de.mark615.xpermission.object;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
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
		
		if (section == null)
		{
			SettingManager.getInstance().isPlayerFirstJoin(p);
			section = SettingManager.getInstance().getPlayerConfigurationsection(uuid);
		}
		
		section.set("lastLogin", this.lastLogin);
	}
	
	public void removePermission(UUID uuid)
	{
		if (uuid == null)
			return;
		permission.remove(uuid);
	}
	
	public void clearPermission()
	{
		tree = null;
		permission.clear();
	}
	
	public XPermissionTree getPermissions(World world)
	{
		boolean reload = true;
		if (lastWorld == null)
			lastWorld = world.getName();
		else
		if (lastWorld.equalsIgnoreCase(world.getName()))
			reload = false;
		
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
	
	public void reloadPermissionTree()
	{
		tree = null;
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
