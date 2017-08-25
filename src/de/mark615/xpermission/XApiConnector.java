package de.mark615.xpermission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.mark615.xapi.interfaces.XPermissionApi;
import de.mark615.xpermission.exception.RankNotFoundException;

public class XApiConnector extends XPermissionApi
{
	private XPermission plugin;
	
	public XApiConnector(de.mark615.xapi.XApi xapi, XPermission plugin)
	{
		super(xapi);
		this.plugin = plugin;
	}

	@Override
	public void setPlayerRankDown(Player sender, Player target)
	{
		try
		{
			plugin.getRankManager().rankPlayerDown(target);
		}
		catch(RankNotFoundException e)
		{
			sender.sendMessage(ChatColor.RED + "Selected rank doesn't exist!");
		}
	}

	@Override
	public void setPlayerRankUp(Player sender, Player target)
	{
		try
		{
			plugin.getRankManager().rankPlayerUp(target);
		}
		catch(RankNotFoundException e)
		{
			sender.sendMessage(ChatColor.RED + "Selected rank doesn't exist!");
		}
	}

	@Override
	public void setPlayerAutoRankDisabled(Player sender, Player target, boolean value)
	{
		plugin.getRankManager().disableAutoRanking(target, value);
	}
	
}
