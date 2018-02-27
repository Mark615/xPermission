package de.mark615.xpermission.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;
import net.milkbowl.vault.permission.Permission;

public class XVaultPermission extends Permission
{
	private XPermission plugin;
	private HashMap<String, XOfflinePlayerSubject> offline;
	private static XVaultPermission instance;
	
	public void cleanOfflinePlayer()
	{
		List<String> remove = new ArrayList<>();
		for (String key : offline.keySet())
		{
			if (System.currentTimeMillis() > (5 * 60 * 1000));
				remove.add(key);
		}
		
		for (String key : remove)
			offline.remove(key);
	}
	
	public static XVaultPermission getInstance() {
		return instance;
	}
	
	public XVaultPermission(XPermission plugin)
	{
		this.plugin = plugin;
		instance = this;
		this.offline = new HashMap<>();
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
	public boolean playerHas(Player player, String permission) {
		return this.playerHas(null, player, permission);
	}

	@Override
	public boolean playerHas(String world, OfflinePlayer player, String permission) {
		return playerHas(world, player.getName(), permission);
	}

	@Override
	public boolean playerHas(World world, String player, String permission) {
		if (world == null)
			return this.playerHas(Bukkit.getWorlds().get(0).getName(), player, permission);
		else
			return this.playerHas(world.getName(), player, permission);
	}

	@Override
	public boolean playerHas(String world, String player, String permission)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player))
			{
				return playerHas(world, p, permission);
			}
		}
		
		//TODO check world permission
		UUID uuid = null;
		XOfflinePlayerSubject subject = offline.get(player);
		if (subject == null) {
			uuid = SettingManager.getInstance().getPlayerUuidFromName(player);
			if (uuid == null)
				return false;

			subject = new XOfflinePlayerSubject(uuid, SettingManager.getInstance().getPlayerConfigurationsection(uuid));
		}
		else
			uuid = subject.getUUID();
		
		if (!offline.containsKey(player)) {
			Map<String, Boolean> perms = SettingManager.getInstance().getPlayerPermissionList(subject);
			for (String key : perms.keySet()) {
				subject.setPermission(UUID.randomUUID(), key, perms.get(key) ? 1 : -1);
			}
			offline.put(player, subject);
		}
		
		return subject.getPermissionTree().has(permission);
	}
	
	private boolean playerHas(String world, Player p, String permission)
	{
		//TODO checkworld permission
		if (p == null || Bukkit.getPlayer(p.getUniqueId()) == null)
			return false;
		
		return p.hasPermission(permission);
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
