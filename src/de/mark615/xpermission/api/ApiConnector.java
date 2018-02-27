package de.mark615.xpermission.api;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.mark615.xcore.interfaces.XPermissionApi;
import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.exception.RankNotFoundException;

public class ApiConnector implements ApiInterface
{
	private XPermission plugin;
	private XPermissionApi api;
	
	public ApiConnector(XPermission plugin, XPermissionApi api)
	{
		this.plugin = plugin;
		this.api = api;
		this.api.setApi(this);
	}
	
	public XPermissionApi getApi() {
		return api;
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
