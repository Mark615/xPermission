package de.mark615.xpermission.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;

public class BasicEvent implements Listener{
	
	protected XPermission plugin;
	protected SettingManager settings; 

	public BasicEvent(XPermission instance)
	{
		this.plugin = instance;
		this.settings = SettingManager.getInstance();
	}
	
	protected boolean hasPlayer(Player p)
	{
		return p == null ? false : (this.plugin.getManager().getXPlayerSubject(p.getUniqueId()) == null ? false : true);
	}
}
