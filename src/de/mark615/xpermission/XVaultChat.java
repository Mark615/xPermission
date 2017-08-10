package de.mark615.xpermission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.mark615.xpermission.object.Group;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class XVaultChat extends Chat
{
	private XPermission plugin;

	public XVaultChat(XPermission plugin, Permission perms)
	{
		super(perms);
		this.plugin = plugin;
	}

	@Override
	public boolean isEnabled()
	{
		return plugin.isEnabled();
	}

	@Override
	public String getName()
	{
		return plugin.getName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getPlayerPrefix(String world, String player)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player) && plugin.getManager().getXPlayerSubject(p.getUniqueId()) != null)
			{
				return plugin.getManager().getXPlayerSubject(p.getUniqueId()).getPrefix();
			}
		}
		
		OfflinePlayer offlineplayer = Bukkit.getServer().getOfflinePlayer(player);
		if (offlineplayer != null && offlineplayer.getUniqueId() != null)
		{
			return SettingManager.getInstance().getPlayerPrefix(offlineplayer.getUniqueId());
		}
		
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getPlayerSuffix(String world, String player)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getName().equals(player) && plugin.getManager().getXPlayerSubject(p.getUniqueId()) != null)
			{
				return plugin.getManager().getXPlayerSubject(p.getUniqueId()).getSuffix();
			}
		}
		
		OfflinePlayer offlineplayer = Bukkit.getServer().getOfflinePlayer(player);
		if (offlineplayer != null && offlineplayer.getUniqueId() != null)
		{
			return SettingManager.getInstance().getPlayerSuffix(offlineplayer.getUniqueId());
		}
		
		return null;
	}

	@Override
	public String getGroupPrefix(String world, String group)
	{
		for (Group g : plugin.getGroups())
		{
			if (g.getName().equals(group))
			{
				SettingManager.getInstance().getGroupPrefix(g.getName());
			}
		}
		return null;
	}

	@Override
	public String getGroupSuffix(String world, String group)
	{
		for (Group g : plugin.getGroups())
		{
			if (g.getName().equals(group))
			{
				SettingManager.getInstance().getGroupSuffix(g.getName());
			}
		}
		return null;
	}

	@Override
	public boolean getGroupInfoBoolean(String arg0, String arg1, String arg2, boolean arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getGroupInfoDouble(String arg0, String arg1, String arg2, double arg3)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGroupInfoInteger(String arg0, String arg1, String arg2, int arg3)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getGroupInfoString(String arg0, String arg1, String arg2, String arg3)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getPlayerInfoBoolean(String arg0, String arg1, String arg2, boolean arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getPlayerInfoDouble(String arg0, String arg1, String arg2, double arg3)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPlayerInfoInteger(String arg0, String arg1, String arg2, int arg3)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getPlayerInfoString(String arg0, String arg1, String arg2, String arg3)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public void setPlayerPrefix(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setPlayerSuffix(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setGroupPrefix(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setGroupSuffix(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setGroupInfoBoolean(String arg0, String arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroupInfoDouble(String arg0, String arg1, String arg2, double arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroupInfoInteger(String arg0, String arg1, String arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroupInfoString(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerInfoBoolean(String arg0, String arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerInfoDouble(String arg0, String arg1, String arg2, double arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerInfoInteger(String arg0, String arg1, String arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerInfoString(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

}
