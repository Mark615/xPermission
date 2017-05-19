package de.mark615.xpermission.command;

import java.util.List;

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
		super("xperm", "xperm.perm");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<String> subcommands)
	{
		subcommands.add("reload");
		subcommands.add("op");
	}
	

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + "- " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(matchPermission(p, "xperm.perm.reload")) p.sendMessage(ChatColor.GREEN + "/xperm reload" + ChatColor.YELLOW + " - "+ XUtil.getMessage("command.xperm.reload.description"));
		if(matchPermission(p, "xperm.perm.op")) p.sendMessage(ChatColor.GREEN + "/xperm op <player>" + ChatColor.YELLOW + " - "+ XUtil.getMessage("command.xperm.op.description"));
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
				return true;
			}
			
			if (!this.containsSubCommand(args[0]))
			{
				XUtil.sendCommandUsage(sender, "use: /xperm <help/?> " + ChatColor.YELLOW + "- for help");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if (!matchPermission(sender, "xperm.perm.reload"))
				{
					this.hasNoPermission();
					return false;
				}
				return reload(sender);
			}
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use: /xperm " + args[0] + " <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);

			if (target == null)
			{
				XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("op"))
			{
				boolean me = false;
				if ((sender instanceof Player) && (((Player) sender).getName().equals(target.getName())))
					me = true;
				if (me)
				{
					if (!matchPermission(sender, "xperm.perm.op.me"))
					{
						this.hasNoPermission();
						return false;
					}
				}
				else
				{
					if (!matchPermission(sender, "xperm.perm.op.other"))
					{
						this.hasNoPermission();
						return false;
					}
				}
				
				setPlayerOP(target);
				if (target.isOp())
				{
					if (!me)
						XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xperm.op.senderInfoAdd").replace("%target%", target.getName()));
					XUtil.sendCommandInfo(target, XUtil.getMessage("command.xperm.op.targetInfoAdd").replace("%target%", target.getName()));
				}
				else
				{
					if (!me)
						XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xperm.op.senderInfoRemove").replace("%target%", target.getName()));
					XUtil.sendCommandInfo(target, XUtil.getMessage("command.xperm.op.targetInfoRemove").replace("%target%", target.getName()));
				}
				return true;
			}
		}
		else
		{
			showHelp(sender);
		}
		return true;
	}
	
	private void setPlayerOP(Player target)
	{
		XPlayerSubject subject = plugin.getManager().getXPlayerSubject(target.getUniqueId());
		if (subject != null)
		{
			subject.getXPermissible().setOp(!subject.getXPermissible().isOp());
			this.plugin.getSettingManager().savePermission();
		}
	}
	
	private boolean reload(CommandSender sender)
	{
		try
		{
			SettingManager.getInstance().reloadConfig();
			SettingManager.getInstance().reloadMessage();
			SettingManager.getInstance().reloadPermission();
			this.plugin.loadPlugin();
			XUtil.sendCommandInfo(sender, XPermission.PLUGIN_NAME_SHORT + XUtil.getMessage("command.xperm.reload.success"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			XUtil.sendCommandError(sender, XPermission.PLUGIN_NAME_SHORT + XUtil.getMessage("command.xperm.reload.error"));
		}
		return true;
	}
}
