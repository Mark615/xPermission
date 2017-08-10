package de.mark615.xpermission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.mark615.xpermission.exception.RankNotFoundException;
import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.XPlayerSubject;
import de.mark615.xpermission.object.XUtil;

public class RankManager
{
	private static final long DEFAULT_PAUSETIME = 60 * 1000;
	private XPermission plugin;
	private BukkitTask task;
	private long lastRun = 0;
	
	public RankManager(XPermission plugin)
	{
		this.plugin = plugin;
		this.lastRun = System.currentTimeMillis();
		this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run() 
			{
				if ((System.currentTimeMillis() - lastRun) > DEFAULT_PAUSETIME)
				{
					updatePlayerRanks();
					lastRun = System.currentTimeMillis();
				}
			}
		}, 60, 10 * 20);
	}
	
	private void updatePlayerRanks()
	{
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			XPlayerSubject subject = this.plugin.getManager().getXPlayerSubject(player.getUniqueId());
			if (subject != null)
			{
				long totalTime = subject.getTotalGameTime();
				for (Group group : this.plugin.getGroups())
				{
					if (group.getRank() != 0 && group.getUpgradeTime() != 0)
					{
						if ((group.getUpgradeTime() * 1000 * 60) < totalTime && group.getRank() > subject.getGroup().getRank())
						{
							if (player.hasPermission("xperm.rank.auto"))
							{
								try
								{
									changeRankTo(subject, group.getRank());
									this.plugin.getAPI().createPlayerRankChangedEvent(subject.getPlayer(), subject.getGroup().getRank(), group.getRank(), true);
								}
								catch (RankNotFoundException e)
								{}
							}
						}
					}
				}
			}
		}
	}
	
	public void rankPlayerUp(Player p) throws RankNotFoundException
	{
		XPlayerSubject subject = plugin.getManager().getXPlayerSubject(p.getUniqueId());
		if (subject != null)
			changeRankTo(subject, subject.getGroup().getRank() + 1);
		this.plugin.getAPI().createPlayerRankChangedEvent(p, subject.getGroup().getRank(), subject.getGroup().getRank() + 1, false);
	}
	
	public void rankPlayerDown(Player p) throws RankNotFoundException
	{
		XPlayerSubject subject = plugin.getManager().getXPlayerSubject(p.getUniqueId());
		if (subject != null && subject.getGroup().getRank() > 0)
			changeRankTo(subject, subject.getGroup().getRank() - 1);
		this.plugin.getAPI().createPlayerRankChangedEvent(p, subject.getGroup().getRank(), subject.getGroup().getRank() - 1, false);
	}
	
	public void setPlayerrank(Player p, int rank) throws RankNotFoundException
	{
		XPlayerSubject subject = plugin.getManager().getXPlayerSubject(p.getUniqueId());
		if (subject != null)
			changeRankTo(subject, rank);
		disableAutoRanking(p, true);
		this.plugin.getAPI().createPlayerRankChangedEvent(p, subject.getGroup().getRank(), rank, false);
	}
	
	private void changeRankTo(XPlayerSubject target, int rank) throws RankNotFoundException
	{
		Group targetGroup = plugin.getGroup(rank);
		if (targetGroup == null)
			throw new RankNotFoundException(rank);
		
		if (target == null)
			return;

		if (target.getGroup().getRank() < rank)
			XUtil.sendFileMessage(target.getPlayer(), "message.rank-up", ChatColor.GREEN);
		else
			XUtil.sendFileMessage(target.getPlayer(), "message.rank-down", ChatColor.YELLOW);
		
		target.setGroup(targetGroup);
		plugin.getSettingManager().saveXPlayerSubject(target);
		plugin.getManager().reloadPlayerPermission(target.getPlayer());
	}
	
	public void disableAutoRanking(Player player, boolean value)
	{
		XPlayerSubject subject = this.plugin.getManager().getXPlayerSubject(player.getUniqueId());
		if (subject == null)
			return;
		
		if (value)
		{
			this.plugin.getPermissionEditor().addPermissionToXSubject(subject, "- xperm.rank.auto");
		}
		else
		{
			this.plugin.getPermissionEditor().addPermissionToXSubject(subject, "- xperm.rank.auto");
		}
		this.plugin.getAPI().createPlayerAutoRankingChangedEvent(player, value);
	}
}
