package de.mark615.xpermission.api;

import org.bukkit.entity.Player;

public interface ApiInterface
{
	public void setPlayerRankUp(Player sender, Player target);
	
	public void setPlayerRankDown(Player sender, Player target);
	
	public void setPlayerAutoRankDisabled(Player sender, Player target, boolean value);
}
