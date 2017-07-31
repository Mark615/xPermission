package de.mark615.xpermission;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.XPlayerSubject;
import net.milkbowl.vault.permission.Permission;

public class XVaultPermission extends Permission
{
	private XPermission plugin;
	
	public XVaultPermission(XPermission plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public String getName()
	{
		return this.plugin.getName();
	}

	@Override
	public boolean isEnabled()
	{
		return this.plugin.isEnabled();
	}

	@Override
	public boolean hasGroupSupport()
	{
		return true;
	}

	@Override
	public boolean hasSuperPermsCompat()
	{
		return true;
	}
	
	@Override
	public String[] getGroups()
	{
		String[] groups = new String[plugin.getGroups().size()];
		return plugin.getGroups().toArray(groups);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String[] getPlayerGroups(String world, String player)
	{
		OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(player);
		if (target != null)
		{
			List<String> list = SettingManager.getInstance().getXPlayerSubjectGroups(target.getUniqueId());
			if (list != null && !list.isEmpty())
			{
				String[] array = new String[list.size()];
				return list.toArray(array);
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getPrimaryGroup(String world, String player)
	{
		OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(player);
		if (target != null)
		{
			List<String> list = SettingManager.getInstance().getXPlayerSubjectGroups(target.getUniqueId());
			if (list != null && !list.isEmpty())
			{
				 return list.get(0);
			}
		}
		return null;
	}
	
	private Group hasGroup(String group)
	{
		for (Group g : plugin.getGroups())
		{
			if (g.getName().equals(group))
				return g;
		}
		return null;
	}

	@Override
	public boolean groupAdd(String world, String group, String permission)
	{
		Group gr = hasGroup(group);
		boolean value = (gr != null ? gr.addPermission(permission) : false);
		
		plugin.getManager().reloadAllPlayerPermission();
		return value;
	}

	@Override
	public boolean groupHas(String world, String group, String permission) 
	{
		Group gr = hasGroup(group);
		return gr != null ? gr.hasPermission(permission) : false;
	}

	@Override
	public boolean groupRemove(String world, String group, String permission)
	{
		Group gr = hasGroup(group);
		boolean value = (gr != null ? gr.removePermission(permission) : false);
		
		plugin.getManager().reloadAllPlayerPermission();
		return value;
	}

	@Override
	public boolean playerAdd(String world, String player, String permission)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player))
			{
				PermissionAttachment attachment = p.addAttachment(XPermission.getInstance(), permission, true);
				attachment.setPermission(permission, true);
				return p.hasPermission(permission);
			}
		}
		
		return false;
	}

	@Override
	public boolean playerRemove(String world, String player, String permission)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player))
			{
				PermissionAttachment attachment = p.addAttachment(XPermission.getInstance(), permission, false);
				attachment.setPermission(permission, false);
				return !p.hasPermission(permission);
			}
		}
		
		return false;
	}

	@Override
	public boolean playerHas(String world, String player, String permission)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player))
			{
				return p.hasPermission(permission);
			}
		}
		
		return false;
	}

	@Override
	public boolean playerAddGroup(String world, String player, String group)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerRemoveGroup(String world, String player, String group)
	{
		Group gr = hasGroup(group);
		XPlayerSubject subject = null;
		
		if (gr == null)
			return false;
		
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(p.getName().equals(player))
			{
				subject = plugin.getManager().getXPlayerSubject(p.getUniqueId());
			}
		}
		if (subject == null)
			return false;
		
		if (subject.getGroup().equals(gr.getName()))
		{
			try
			{
				plugin.getRankManager().rankPlayerDown(subject.getPlayer());
				plugin.getSettingManager().savePermission();
				plugin.getManager().reloadPlayerPermission(subject.getPlayer());
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean playerInGroup(String world, String player, String group)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player))
			{
				return plugin.getManager().getXPlayerSubject(p.getUniqueId()).inGroup(group);
			}
		}
		
		OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(player);
		if (target != null)
		{
			List<String> list = SettingManager.getInstance().getXPlayerSubjectGroups(target.getUniqueId());
			return list != null ? list.contains(group) : false;
		}
		return false;
	}

}
