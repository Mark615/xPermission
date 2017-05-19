package de.mark615.xpermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;

import de.mark615.xpermission.events.PlayerEvents;
import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.XInjector;
import de.mark615.xpermission.object.XPermissible;
import de.mark615.xpermission.object.XPlayerSubject;
import de.mark615.xpermission.object.XUtil;

public class PermissionManager
{
	private XPermission plugin;
	private Map<UUID, XPlayerSubject> permissionList; 
	SettingManager settings = SettingManager.getInstance();
	
	
	public PermissionManager (XPermission plugin)
	{
		this.plugin = plugin;
		this.permissionList = new HashMap<>();
	}
	
	public XPlayerSubject getXPlayerSubject(UUID uuid)
	{
		return permissionList.get(uuid);
	}
	
	public void unregisterPlayer(Player p)
	{
		if (permissionList.get(p.getUniqueId()) != null)
		{
			settings.saveXPlayerSubject(permissionList.get(p.getUniqueId()));
			permissionList.remove(p.getUniqueId());
		}
	}
	
	public void registerPlayer(Player p)
	{
		if (permissionList.get(p.getUniqueId()) != null)
			return;
		
		try {
	        XPermissible permissible = new XPermissible(p, plugin);
			permissionList.put(p.getUniqueId(), new XPlayerSubject(p.getUniqueId(), permissible, settings.getPlayerConfigurationsection(p.getUniqueId())));
	        permissible.register();
	
	        boolean success = false, found = false;
	        for (XInjector injector : PlayerEvents.INJECTORS) {
	            if (injector.isApplicable(p)) {
	                found = true;
	                Permissible oldPerm = injector.inject(p, permissible);
	                if (oldPerm != null) {
	                    permissible.setPreviousPermissible(oldPerm);
	                    success = true;
	                    break;
	                }
	            }
	        }
	
	        if (!found) {
	            XUtil.info("No Permissible injector found for your server implementation!");
	        } else if (!success) {
	        	XUtil.info("Unable to inject PEX's permissible for " + p.getName());
	        }	
	
	        permissible.recalculatePermissions();
			plugin.getManager().loadPlayerPermission(p, permissible);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void loadPlayerPermission(Player p, XPermissible permissible)
	{	
		settings.isPlayerFirstJoin(p);
		p.setPlayerListName(settings.getPlayerDisplayName(p));
		p.setDisplayName(settings.getPlayerDisplayName(p));

		permissible.setOp(settings.isPlayerOp(p));
		
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
	
	public void reloadAllPlayerPermission()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			reloadPlayerPermission(p);
		}
	}
	
	public void reloadPlayerPermission(Player p)
	{
		permissionList.get(p.getUniqueId()).clearPermission();
		loadPlayerPermission(p, permissionList.get(p.getUniqueId()).getXPermissible());
	}
}
