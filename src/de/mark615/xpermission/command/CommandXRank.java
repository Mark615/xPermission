package de.mark615.xpermission.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xpermission.XPermission;
import de.mark615.xpermission.exception.RankNotFoundException;
import de.mark615.xpermission.object.XUtil;

public class CommandXRank extends XCommand
{
	private final XPermission plugin;

	public CommandXRank(XPermission plugin)
	{
		super("xrank", "xperm.xrank");
		this.plugin = plugin;
	}

	
	private void showHelp(Player p)
	{
		p.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(p.hasPermission("xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank up [player]" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.up.description"));
		if(p.hasPermission("xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank down [player]" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.down.description"));
		if(p.hasPermission("xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank set [player]" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.set.description"));
	}

	private void showHelp(CommandSender s)
	{
		s.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		s.sendMessage(ChatColor.GREEN + "/xrank up [player]" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.up.description"));
		s.sendMessage(ChatColor.GREEN + "/xrank down [player]" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.down.description"));
		s.sendMessage(ChatColor.GREEN + "/xrank set [player]" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.set.description"));
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
			
			if (args.length < 2)
			{
				commandSender.sendMessage(ChatColor.RED + "use: /xrank " + args[0] + " <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null)
			{
				commandSender.sendMessage(XUtil.getMessage("command.player-not-found"));
				return true;
			}
			
			try
			{
				if (args[0].equalsIgnoreCase("up"))
				{
					plugin.getRankManager().rankPlayerUp(target);
					commandSender.sendMessage(XUtil.getMessage("command.xrank.up.success").replace("%target%", target.getName()));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("down"))
				{
					plugin.getRankManager().rankPlayerDown(target);
					commandSender.sendMessage(XUtil.getMessage("command.xrank.down.success").replace("%target%", target.getName()));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("set"))
				{
					if (args.length < 3)
					{
						commandSender.sendMessage(ChatColor.RED + "use: /xrank " + args[0] + " <player> <rank>");
						return true;
					}
					
					try
					{
						plugin.getRankManager().setPlayerrank(target, Integer.parseInt(args[2]));
						commandSender.sendMessage(XUtil.getMessage("command.xrank.set.success").replace("%target%", target.getName()));
						return true;
					}
					catch(NumberFormatException e)
					{
						commandSender.sendMessage(ChatColor.RED + "use: /xrank " + args[0] + " <player> <rank>");
						commandSender.sendMessage(ChatColor.YELLOW + "example: /xrank " + args[0] + " Mark615 4");
						return true;
					}
				}
			}
			catch (RankNotFoundException e)
			{
				commandSender.sendMessage(ChatColor.RED + "Select rank doesn't exist!");
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
			
			try
			{
				if (args[0].equalsIgnoreCase("up"))
				{
					if (!player.hasPermission("xperm.xrank.up"))
					{
						this.hasNoPermission();
						return false;
					}
					plugin.getRankManager().rankPlayerUp(target);
					player.sendMessage(XUtil.getMessage("command.xrank.up").replace("%target%", target.getName()));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("down"))
				{
					if (!player.hasPermission("xperm.xrank.down"))
					{
						this.hasNoPermission();
						return false;
					}
					plugin.getRankManager().rankPlayerDown(target);
					player.sendMessage(XUtil.getMessage("command.xrank.down").replace("%target%", target.getName()));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("set"))
				{
					if (!player.hasPermission("xperm.xrank.set"))
					{
						this.hasNoPermission();
						return false;
					}
					if (args.length < 3)
					{
						player.sendMessage(ChatColor.RED + "use: /xrank " + args[0] + " <player> <rank>");
						return true;
					}
					
					try
					{
						plugin.getRankManager().setPlayerrank(target, Integer.parseInt(args[2]));
						player.sendMessage(XUtil.getMessage("command.xrank.set").replace("%target%", target.getName()));
						return true;
					}
					catch(NumberFormatException e)
					{
						player.sendMessage(ChatColor.RED + "use: /xrank " + args[0] + " <player> <rank>");
						player.sendMessage(ChatColor.YELLOW + "example: /xrank " + args[0] + " " + player.getName() + " 4");
						return true;
					}
				}
			}
			catch (RankNotFoundException e)
			{
				player.sendMessage(ChatColor.RED + "Select rank doesn't exist!");
			}
		}
		else
		{
			showHelp(player);
		}
		return true;
	}
}
