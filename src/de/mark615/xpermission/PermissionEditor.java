package de.mark615.xpermission;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.mark615.xpermission.object.XPlayerSubject;

public class PermissionEditor
{
	private XPermission plugin;
	
	public PermissionEditor(XPermission plugin)
	{
		this.plugin = plugin;
	}
	
	public void addPermissionToXSubject(XPlayerSubject subject, String permission)
	{
		List<String> perms = null;
		ConfigurationSection section = this.plugin.getSettingManager().getPlayerConfigurationsection(subject.getUUID());
		perms = section.getStringList("permission");
		if (perms.contains(permission))
			return;
		
		if (permission.startsWith("+ "))
		{
			//remove negative permission and neutral
		}
		else
		if (permission.startsWith("- "))
		{
			//remove positiv and neutral permission
		}
		
		perms.add(permission);
		section.set("permission", perms);
		this.plugin.getSettingManager().savePermission();
	}
	
	public void removePermissionFromXSubject(XPlayerSubject subject, String permission)
	{
		List<String> perms = null;
		ConfigurationSection section = this.plugin.getSettingManager().getPlayerConfigurationsection(subject.getUUID());
		perms = section.getStringList("permission");
		if (!perms.contains(permission))
			return;
		
		perms.remove(permission);
		section.set("permission", perms);
		this.plugin.getSettingManager().savePermission();
	}
}
