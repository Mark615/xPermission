package de.mark615.xpermission.command;

import java.util.List;

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
		super("xrank", "xperm.rank");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<String> subcommands)
	{
		subcommands.add("info");
		subcommands.add("up");
		subcommands.add("down");
		subcommands.add("set");
	}
	

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		p.sendMessage(ChatColor.GREEN + "/xrank info <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.info.description"));
		if(matchPermission(p, "xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank up <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.up.description"));
		if(matchPermission(p, "xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank down <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.down.description"));
		if(matchPermission(p, "xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank set <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.set.description"));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean run(CommandSender sender, Command command, String s, String[] args)
	{
		if(args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help") || args[0].equals("?"))
			{
				showHelp(sender);
			}
			
			if (!this.containsSubCommand(args[0]))
			{
				XUtil.sendCommandUsage(sender, "use: /xrank <help/?> " + ChatColor.YELLOW + "- for help");
				return true;
			}
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use: /xrank " + args[0] + " <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);

			if (target == null)
			{
				XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
				return true;
			}
			
			try
			{
				if (args[0].equalsIgnoreCase("info"))
				{
					XUtil.sendCommandInfo(sender, "Playerrank info: " + target.getName());
					XUtil.sendCommandInfo(sender, "- autoranking: [" + target.hasPermission("xperm.rank.auto") + "]");
					XUtil.sendCommandInfo(sender, 
							"- playerrank: [" + this.plugin.getManager().getXPlayerSubject(target.getUniqueId()).getGroup().getRank() +"] - " +
							"group [" + this.plugin.getManager().getXPlayerSubject(target.getUniqueId()).getGroup().getName() + "]");
					return true;
				}
			
				if (args[0].equalsIgnoreCase("up"))
				{
					if (!matchPermission(sender, "xperm.rank.set"))
					{
						this.hasNoPermission();
						return false;
					}
					plugin.getRankManager().rankPlayerUp(target);
					XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xrank.up.success").replace("%target%", target.getName()));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("down"))
				{
					if (!matchPermission(sender, "xperm.rank.set"))
					{
						this.hasNoPermission();
						return false;
					}
					plugin.getRankManager().rankPlayerDown(target);
					XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xrank.down.success").replace("%target%", target.getName()));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("set"))
				{
					if (!matchPermission(sender, "xperm.rank.set"))
					{
						this.hasNoPermission();
						return false;
					}
					if (args.length < 3)
					{
						XUtil.sendCommandUsage(sender, "use: /xrank " + args[0] + " <player> <rank>");
						return true;
					}
					
					try
					{
						plugin.getRankManager().setPlayerrank(target, Integer.parseInt(args[2]));
						XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xrank.set.success").replace("%target%", target.getName()));
						return true;
					}
					catch(NumberFormatException e)
					{
						XUtil.sendCommandUsage(sender, "use: /xrank " + args[0] + " <player> <rank>");
						XUtil.sendCommandHelp(sender, "example: /xrank " + args[0] + " Notch 4");
						return true;
					}
				}
			}
			catch (RankNotFoundException e)
			{
				XUtil.sendCommandError(sender, "Select rank doesn't exist!");
			}
		}
		else
		{
			showHelp(sender);
		}
		return true;
	}
}
