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
	private Map<UUID, XPlayerSubject> player; 
	SettingManager settings = SettingManager.getInstance();
	
	
	public PermissionManager (XPermission plugin)
	{
		this.plugin = plugin;
		this.player = new HashMap<>();
	}
	
	public XPlayerSubject getXPlayerSubject(UUID uuid)
	{
		return player.get(uuid);
	}
	
	public void unregisterPlayer(Player p)
	{
		if (player.get(p.getUniqueId()) != null)
		{
			settings.saveXPlayerSubject(player.get(p.getUniqueId()));
			player.remove(p.getUniqueId());
		}
	}
	
	public void clearXPlayerSubjectList()
	{
		player.clear();
	}
	
	public void registerPlayerJoin(Player p)
	{
		if (player.get(p.getUniqueId()) != null)
			return;
		
		try {
	        XPermissible permissible = new XPermissible(p, plugin);
			player.put(p.getUniqueId(), new XPlayerSubject(p, permissible, settings.getPlayerConfigurationsection(p.getUniqueId())));
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
	        	XUtil.info("Unable to inject xPermission permissible for " + p.getName());
	        }	
	
	        permissible.recalculatePermissions();
			loadPlayerPermission(p, permissible);
		}
		catch (Exception e)
		{
			XUtil.severe("unable to register player", e);
		}
	}
	
	private void loadPlayerPermission(Player p, XPermissible permissible)
	{	
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
		player.get(p.getUniqueId()).setGroup(group);

		Map<String, Boolean> permissions = settings.getPlayerPermissionList(p.getUniqueId(), player.get(p.getUniqueId()));
		for (String perm : permissions.keySet())
		{
			PermissionAttachment attachment = p.addAttachment(XPermission.getInstance(), perm, permissions.get(perm));
			attachment.setPermission(perm, permissions.get(perm));
		}
		player.get(p.getUniqueId()).reloadPermissionTree();
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
		player.get(p.getUniqueId()).getXPermissible().clearPermissions();
		loadPlayerPermission(p, player.get(p.getUniqueId()).getXPermissible());
	}
	
	public void reloadAllPlayer()
	{
		for (UUID uuid : player.keySet())
		{
			player.get(uuid).reload();
			reloadPlayerPermission(player.get(uuid).getPlayer());
		}
	}
}
