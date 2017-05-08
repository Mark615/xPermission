package de.mark615.xpermission.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class XCommand
{
	private String command;
	private String permission;
	private boolean hasPermission;
	private List<String> subcommands;
	
	public XCommand(String command, String permission)
	{
		this.command = command;
		this.subcommands = new ArrayList<>();
		this.permission = permission;
		this.hasPermission = true;
		
		subcommands.add("?");
		subcommands.add("help");
		fillSubCommands(subcommands);
	}
	
	public abstract void fillSubCommands(List<String> subcommands);
	
	public boolean containsSubCommand(String cmd)
	{
		return subcommands.contains(cmd);
	}

	public abstract boolean run(CommandSender sender, Command command, String s, String[] args);
	
	protected abstract void showHelp(CommandSender sender);

	protected boolean matchPermission(CommandSender sender, String permission)
	{
		if (sender instanceof Player)
		{
			return ((Player) sender).hasPermission(permission);
		}
		return true;
	}
	
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
