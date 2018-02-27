package de.mark615.xpermission.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import de.mark615.xpermission.XPermission;
import net.ess3.api.events.AfkStatusChangeEvent;

public class EssentialEvents extends BasicEvent
{
	public EssentialEvents(XPermission main) {
		super(main);
	}
	
	@EventHandler
	public void onPlayerAFKStateChangedEvent(AfkStatusChangeEvent e)
	{
		final Player p = e.getAffected().getBase();
		
		if (hasPlayer(p))
		{
			this.plugin.getManager().getXPlayerSubject(p.getUniqueId()).setAfkMode(e.getValue());
		}
	}
}
