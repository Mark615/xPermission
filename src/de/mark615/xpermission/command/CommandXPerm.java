package de.mark615.xpermission.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xpermission.SettingManager;
import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.object.XPlayerSubject;
import de.mark615.xpermission.object.XUtil;

public class CommandXPerm extends XCommand
{
	private final XPermission plugin;

	public CommandXPerm(XPermission plugin)
	{
		super("xperm", "xperm");
		this.plugin = plugin;
	}
	

	private void showHelp(Player p)
	{
		p.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + "- " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(p.hasPermission("xperm.reload")) p.sendMessage(ChatColor.GREEN + "/xperm reload" + ChatColor.YELLOW + " - "+ XUtil.getMessage("command.xperm.reload.description"));
		if(p.hasPermission("xperm.reload")) p.sendMessage(ChatColor.GREEN + "/xperm op" + ChatColor.YELLOW + " <player> - "+ XUtil.getMessage("command.xperm.op.description"));
	}

	private void showHelp(CommandSender s)
	{
		s.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + "- " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		s.sendMessage(ChatColor.GREEN + "/xperm reload" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xperm.reload.description"));
		s.sendMessage(ChatColor.GREEN + "/xperm op" + ChatColor.YELLOW + " <player> - " + XUtil.getMessage("command.xperm.op.description"));
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean run(CommandSender commandSender, Command command, String s, String[] args)
	{
		if(args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help") || args[0].equals("?"))
			{
				showHelp(commandSender);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				return reload(commandSender);
			}
			
			if (args.length < 2)
			{
				commandSender.sendMessage(ChatColor.RED + "use: /xperm " + args[0] + " <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null)
			{
				commandSender.sendMessage(XUtil.getMessage("command.player-not-found"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("op"))
			{
				setPlayerOP(target);
				commandSender.sendMessage(XUtil.getMessage("command.xperm.op.senderInfo").replace("%target%", target.getName()));
				target.sendMessage(XUtil.getMessage("command.xperm.op.targetInfo").replace("%target%", target.getName()));
			}
		}
		else
		{
			showHelp(commandSender);
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean run(Player player, Command command, String s, String[] args)
	{
		if(args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help") || args[0].equals("?"))
			{
				showHelp(player);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if (!player.hasPermission("xperm.reload"))
				{
					this.hasNoPermission();
					return false;
				}
				return reload(player);
			}
			
			Player target = null;
			if (args.length < 2)
			{
				target = player;
			}
			else
			{
				target = Bukkit.getPlayer(args[1]);
			}

			if (target == null)
			{
				player.sendMessage(XUtil.getMessage("command.player-not-found"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("op"))
			{
				if (player.getName().equals(target.getName()))
				{
					if (!player.hasPermission("xperm.op.me"))
					{
						this.hasNoPermission();
						return false;
					}
				}
				else
				{
					if (!player.hasPermission("xperm.op.other"))
					{
						this.hasNoPermission();
						return false;
					}
				}
				
				setPlayerOP(target);
				if (!player.getName().equals(target.getName()))
					player.sendMessage(XUtil.getMessage("command.op.senderInfo").replace("%target%", target.getName()));
				target.sendMessage(XUtil.getMessage("command.op.targetInfo").replace("%target%", target.getName()));
				return true;
			}
		}
		else
		{
			showHelp(player);
		}
		return true;
	}
	
	private void setPlayerOP(Player target)
	{
		XPlayerSubject subject = plugin.getManager().getXPlayerSubject(target.getUniqueId());
		if (subject != null)
		{
			subject.getXPermissible().setOp(!subject.getXPermissible().isOp());
			
		}
	}
	
	private boolean reload(CommandSender sender)
	{
		try
		{
			SettingManager.getInstance().reloadConfig();
			SettingManager.getInstance().reloadMessage();
			SettingManager.getInstance().reloadPermission();
			plugin.getManager().reloadAllPlayerPermission();
			sender.sendMessage(XPermission.PLUGIN_NAME_SHORT + XUtil.getMessage("command.xperm.reload.success"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(XPermission.PLUGIN_NAME_SHORT + XUtil.getMessage("command.xperm.reload.error"));
		}
		return true;
	}
}
