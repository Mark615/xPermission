package de.mark615.xpermission.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class XCommand
{
	private String command;
	private String permission;
	private boolean hasPermission;
	
	public XCommand(String command, String permission)
	{
		this.command = command;
		this.permission = permission;
		this.hasPermission = true;
	}

	public abstract boolean run(Player commandSender, Command command, String s, String[] args);
	
	public abstract boolean run(CommandSender commandSender, Command command, String s, String[] args);
	
	public String getCommand()
	{
		return this.command;
	}
	
	public String getPermission()
	{
		return this.permission;
	}
	
	public void hasNoPermission()
	{
		this.hasPermission = false;
	}
	
	public boolean hasCommandSenderPermission()
	{
		return this.hasPermission;
	}
}
