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
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("info", "i"));
		subcommands.add(new XSubCommand("up"));
		subcommands.add(new XSubCommand("down"));
		subcommands.add(new XSubCommand("set"));
	}
	

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		p.sendMessage(ChatColor.GREEN + "/xrank info <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.info.description"));
		if(matchPermission(p, "xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank up <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.up.description"));
		if(matchPermission(p, "xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank down <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.down.description"));
		if(matchPermission(p, "xperm.rank.set")) p.sendMessage(ChatColor.GREEN + "/xrank set <player> <rank>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xrank.set.description"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		XCommandReturnType type = null;
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xrank <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		if (args.length < 2)
		{
			XUtil.sendCommandUsage(sender, "use: /xrank " + args[0] + " <player>");
			return XCommandReturnType.NONE;
		}
		
		Player target = Bukkit.getPlayer(args[1]);

		if (target == null)
		{
			XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
			return XCommandReturnType.NOPLAYERMATCH;
		}
		
		try
		{
			if (matchesSubCommand("info", args[0]))
			{
				XUtil.sendCommandInfo(sender, "Playerrank info: " + target.getName());
				XUtil.sendCommandInfo(sender, "- autoranking: [" + target.hasPermission("xperm.rank.auto") + "]");
				XUtil.sendCommandInfo(sender, 
						"- playerrank: [" + this.plugin.getManager().getXPlayerSubject(target.getUniqueId()).getGroup().getRank() +"] - " +
						"group [" + this.plugin.getManager().getXPlayerSubject(target.getUniqueId()).getGroup().getName() + "]");
				return XCommandReturnType.SUCCESS;
			}
		
			if (matchesSubCommand("up", args[0]))
			{
				if (!matchPermission(sender, "xperm.rank.set"))
				{
					this.hasNoPermission();
					return XCommandReturnType.NOPERMISSION;
				}
				plugin.getRankManager().rankPlayerUp(target);
				XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xrank.up.success").replace("%target%", target.getName()));
				return XCommandReturnType.SUCCESS;
			}
			
			if (matchesSubCommand("down", args[0]))
			{
				if (!matchPermission(sender, "xperm.rank.set"))
				{
					this.hasNoPermission();
					return XCommandReturnType.NOPERMISSION;
				}
				plugin.getRankManager().rankPlayerDown(target);
				XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xrank.down.success").replace("%target%", target.getName()));
				return XCommandReturnType.SUCCESS;
			}
			
			if (matchesSubCommand("set", args[0]))
			{
				if (!matchPermission(sender, "xperm.rank.set"))
				{
					this.hasNoPermission();
					return XCommandReturnType.NOPERMISSION;
				}
				if (args.length < 3)
				{
					XUtil.sendCommandUsage(sender, "use: /xrank " + args[0] + " <player> <rank>");
					return XCommandReturnType.NONE;
				}
				
				try
				{
					plugin.getRankManager().setPlayerrank(target, Integer.parseInt(args[2]));
					XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xrank.set.success").replace("%target%", target.getName()));
					return XCommandReturnType.SUCCESS;
				}
				catch(NumberFormatException e)
				{
					XUtil.sendCommandUsage(sender, "use: /xrank " + args[0] + " <player> <rank>");
					XUtil.sendCommandHelp(sender, "example: /xrank " + args[0] + " Notch 4");
					return XCommandReturnType.NONE;
				}
			}
		}
		catch (RankNotFoundException e)
		{
			XUtil.sendCommandError(sender, "Select rank doesn't exist!");
		}
		return type;
	}
}
