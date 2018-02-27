package de.mark615.xpermission.events;

import static de.mark615.xpermission.object.CraftBukkitInterface.getCBClassName;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.object.XInjector;

public class PlayerEvents extends BasicEvent
{
	public static final XInjector[] INJECTORS = new XInjector[] {
            new XInjector.ClassPresencePermissibleInjector(getCBClassName("entity.CraftHumanEntity"), "perm", true),
	};
	
	public PlayerEvents(XPermission main) {
		super(main);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLoggin(PlayerLoginEvent e)
	{
		final Player p = e.getPlayer();
		this.plugin.getManager().registerPlayerJoin(p);
		
		/*
		for (String key : plugin.getManager().getXPlayerSubject(p.getUniqueId()).getPermissions().keySet())
		{
			XUtil.info("permission " + key + ", " + plugin.getManager().getXPlayerSubject(p.getUniqueId()).getPermissions().get(key));
		}
		*/
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		plugin.getManager().unregisterPlayer(e.getPlayer());
		plugin.getSettingManager().savePermission();
	}
}
