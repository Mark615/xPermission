package de.mark615.xpermission.events;

import static de.mark615.xpermission.object.CraftBukkitInterface.getCBClassName;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permissible;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.object.XInjector;
import de.mark615.xpermission.object.XPermissible;
import de.mark615.xpermission.object.XUtil;
import net.ess3.api.events.AfkStatusChangeEvent;

public class PlayerEvents implements Listener
{
	private static final XInjector[] INJECTORS = new XInjector[] {
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

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		final Player p = e.getPlayer();
		
		
		try {
            XPermissible permissible = new XPermissible(p, plugin);
            plugin.getManager().registerPlayer(p, permissible);
            permissible.register();

            boolean success = false, found = false;
            for (XInjector injector : INJECTORS) {
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
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		plugin.getManager().unregisterPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerAFKStateChangedEvent(AfkStatusChangeEvent e)
	{
		this.plugin.getManager().getXPlayerSubject(e.getAffected().getBase().getUniqueId()).setAfkMode(e.getValue());
	}
	
	
}
