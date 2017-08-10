package de.mark615.xpermission.events;

import static de.mark615.xpermission.object.CraftBukkitInterface.getCBClassName;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.object.XInjector;
import net.ess3.api.events.AfkStatusChangeEvent;

public class PlayerEvents implements Listener
{
	public static final XInjector[] INJECTORS = new XInjector[] {
            //new XInjector.ClassPresencePermissibleInjector("net.glowstone.entity.GlowHumanEntity", "permissions", true),
            //new XInjector.ClassPresencePermissibleInjector("org.getspout.server.entity.SpoutHumanEntity", "permissions", true),
            //new XInjector.ClassNameRegexPermissibleInjector("org.getspout.spout.player.SpoutCraftPlayer", "perm", false, "org\\.getspout\\.spout\\.player\\.SpoutCraftPlayer"),
            new XInjector.ClassPresencePermissibleInjector(getCBClassName("entity.CraftHumanEntity"), "perm", true),
	};
	
	private XPermission plugin;

	public PlayerEvents(XPermission instance)
	{
		this.plugin = instance;
	}

	SettingManager settings = SettingManager.getInstance();
	
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
	
	@EventHandler
	public void onPlayerAFKStateChangedEvent(AfkStatusChangeEvent e)
	{
		if (e.getAffected().getBase() != null)
			this.plugin.getManager().getXPlayerSubject(e.getAffected().getBase().getUniqueId()).setAfkMode(e.getValue());
	}
	
	
}
