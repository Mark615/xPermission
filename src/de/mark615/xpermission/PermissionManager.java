package de.mark615.xpermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.XPermissible;
import de.mark615.xpermission.object.XPlayerSubject;

public class PermissionManager
{
	private XPermission plugin;
	private Map<UUID, XPlayerSubject> permissionList; 
	SettingManager settings = SettingManager.getInstance();
	
	
	public PermissionManager (XPermission plugin)
	{
		this.plugin = plugin;
		this.permissionList = new HashMap<>();
		System.out.println("test");
	}
	
	public XPlayerSubject getXPlayerSubject(UUID uuid)
	{
		return permissionList.get(uuid);
	}
	
	public void registerPlayer(Player p, XPermissible permissible)
	{
		permissionList.put(p.getUniqueId(), new XPlayerSubject(p.getUniqueId(), permissible, settings.getPlayerConfigurationsection(p.getUniqueId())));
	}
	
	public void unregisterPlayer(Player p)
	{
		settings.saveXPlayerSubject(permissionList.get(p.getUniqueId()));
		permissionList.remove(p.getUniqueId());
		settings.savePermission();
	}
	
	
	public void reloadAllPlayerPermission()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			permissionList.get(p.getUniqueId()).getXPermissible().clearPermissions();
			loadPlayerPermission(p, permissionList.get(p.getUniqueId()).getXPermissible());
		}
	}
	
	public void loadPlayerPermission(Player p, XPermissible permissible)
	{	
		permissible.setOp(false);
		settings.isPlayerFirstJoin(p);
		
		p.setPlayerListName(settings.getPlayerDisplayName(p));
		p.setDisplayName(settings.getPlayerDisplayName(p));

		if (settings.isPlayerOp(p))
			permissible.setOp(true);
		
		Group group = plugin.getGroup(settings.getPlayerRank(p.getUniqueId()));
		if (group == null)
		{
			int nextRank = -1;
			for(Group g : plugin.getGroups())
			{
				if (g.getRank() > nextRank && g.getRank() < settings.getPlayerRank(p.getUniqueId()))
					nextRank = g.getRank();
			}
			
			if (nextRank != -1)
				group = plugin.getGroup(nextRank);
			else
				group = plugin.getDefaultGroup();
		}
		permissionList.get(p.getUniqueId()).setGroup(group);

		Map<String, Boolean> permissions = settings.getPlayerPermissionList(p.getUniqueId(), permissionList.get(p.getUniqueId()));
		for (String perm : permissions.keySet())
		{
			PermissionAttachment attachment = p.addAttachment(XPermission.getInstance(), perm, permissions.get(perm));
			attachment.setPermission(perm, permissions.get(perm));
		}
	}
	
	public void reloadPlayerPermission(Player p)
	{
		permissionList.get(p.getUniqueId()).clearPermission();
		loadPlayerPermission(p, permissionList.get(p.getUniqueId()).getXPermissible());
	}
}
