package de.mark615.xpermission;

import de.mark615.xpermission.object.Group;
import net.milkbowl.vault.permission.Permission;

public class XVault extends Permission
{
	private XPermission plugin;
	
	public XVault(XPermission plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public String getName()
	{
		return this.plugin.getName();
	}

	@Override
	public boolean isEnabled()
	{
		return this.plugin.isEnabled();
	}

	@Override
	public boolean hasGroupSupport()
	{
		return true;
	}
	
	@Override
	public String[] getGroups()
	{
		String[] groups = new String[this.plugin.getGroups().size()];
		int i = 0;
		for (Group g : this.plugin.getGroups())
		{
			groups[i] = g.getName();
			i++;
		}
		return groups;
	}

	@Override
	public String[] getPlayerGroups(String arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrimaryGroup(String arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean groupAdd(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean groupHas(String arg0, String arg1, String arg2) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean groupRemove(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSuperPermsCompat()
	{
		return true;
	}

	@Override
	public boolean playerAdd(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerAddGroup(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerHas(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerInGroup(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerRemove(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerRemoveGroup(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
