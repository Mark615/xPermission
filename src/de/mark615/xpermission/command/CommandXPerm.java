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
import de.mark615.xpermission.object.XVaultPermission;

public class CommandXPerm extends XCommand
{
	private final XPermission plugin;

	public CommandXPerm(XPermission plugin)
	{
		super("xperm", "xperm.perm");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("reload"));
		subcommands.add(new XSubCommand("op"));
	}
	

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XPermission.PLUGIN_NAME + ChatColor.GRAY + "- " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(matchPermission(p, "xperm.perm.reload")) p.sendMessage(ChatColor.GREEN + "/xperm reload" + ChatColor.YELLOW + " - "+ XUtil.getMessage("command.xperm.reload.description"));
		if(matchPermission(p, "xperm.perm.op")) p.sendMessage(ChatColor.GREEN + "/xperm op <player>" + ChatColor.YELLOW + " - "+ XUtil.getMessage("command.xperm.op.description"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		XCommandReturnType type = null;
		
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xperm <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NOCOMMAND;
		}
		
		if (matchesSubCommand("reload", args[0]))
		{
			if (!matchPermission(sender, "xperm.perm.reload"))
			{
				this.hasNoPermission();
				return XCommandReturnType.NOPERMISSION;
			}
			reload(sender);
			return XCommandReturnType.NONE;
		}
		
		if (args.length < 2)
		{
			XUtil.sendCommandUsage(sender, "use: /xperm " + args[0] + " <player>");
			return XCommandReturnType.NONE;
		}
		
		Player target = Bukkit.getPlayer(args[1]);

		if (target == null)
		{
			XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
			return XCommandReturnType.NOPLAYERMATCH;
		}
		
		if (matchesSubCommand("op", args[0]))
		{
			boolean me = false;
			if ((sender instanceof Player) && (((Player) sender).getName().equals(target.getName())))
				me = true;
			if (me)
			{
				if (!matchPermission(sender, "xperm.perm.op.me"))
				{
					this.hasNoPermission();
					return XCommandReturnType.NOCOMMAND;
				}
			}
			else
			{
				if (!matchPermission(sender, "xperm.perm.op.other"))
				{
					this.hasNoPermission();
					return XCommandReturnType.NOPERMISSION;
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
			return XCommandReturnType.SUCCESS;
		}
		return type;
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
			plugin.getManager().reloadAllPlayer();
			if (XVaultPermission.getInstance() != null) {
				XVaultPermission.getInstance().cleanOfflinePlayer();
				XUtil.info("reset xVaultPermission Cache.");
			}
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xperm.reload.success"));
		}
		catch (Exception e)
		{
			XUtil.severe("unable to reload permission", e);
			XUtil.sendCommandError(sender, XUtil.getMessage("command.xperm.reload.error"));
		}
		return true;
	}
}
