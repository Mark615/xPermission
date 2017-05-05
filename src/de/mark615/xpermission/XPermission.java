package de.mark615.xpermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import de.mark615.xapi.XApi;
import de.mark615.xpermission.command.CommandXPerm;
import de.mark615.xpermission.command.CommandXRank;
import de.mark615.xpermission.command.XCommand;
import de.mark615.xpermission.events.PlayerEvents;
import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.XUtil;
import net.milkbowl.vault.permission.Permission;

public class XPermission extends JavaPlugin
{
	public static final String PLUGIN_NAME = "[xPermission] ";
	public static final String PLUGIN_NAME_SHORT = "[xPerm] ";

	private XApiConnector xapiconn;
	private Essentials ess;

	private SettingManager settings = null;
	private static XPermission instance = null;
	private PermissionManager manager;
	private RankManager rankmanager;
	private List<Group> groups = null;
	private Map<String, XCommand> commands = null;

	public void onEnable()
	{
		instance = this;
		this.groups = new ArrayList<>();
		this.commands = new HashMap<>();

		settings = SettingManager.getInstance();
		settings.setup(this);
		this.manager = new PermissionManager(this);
		this.rankmanager = new RankManager(this);

		setupPermissionGroups();
		settings.checkPermissionFile();

		registerEvents();
		registerCommands();
		
		setupXApi();
		if (xapiconn != null)
		{
			XUtil.info("connected with xApi");
		}
		hookEssentials();
		if (ess != null)
		{
			XUtil.info("connected to Essentials2");
		}
		
		hookVaultPermissions();
		XUtil.info("startet");
	}

	@Override
	public void onDisable()
	{
		this.groups = null;
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.manager.unregisterPlayer(p); 
		}
		this.settings.savePermission();
		XUtil.info("shutdown");
	}
	
	

	private void registerEvents()
	{
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
	}
	
	private void registerCommands()
	{
		commands.put("xperm", new CommandXPerm(this));
		commands.put("xrank", new CommandXRank(this));
	}

	private boolean setupXApi()
	{
		XApi xapi = (XApi)getServer().getPluginManager().getPlugin("xApi");
    	if(xapi == null)
    		return false;
    	
    	xapiconn = new XApiConnector(xapi, this);
    	xapi.registerXPermission(xapiconn);
    	return xapiconn != null;
	}
	
	private boolean hookVaultPermissions()
	{
		if (getServer().getPluginManager().isPluginEnabled("Vault"))
		{
            final XVault vault = new XVault(this);
            getServer().getServicesManager().register(Permission.class, vault, this, ServicePriority.High); // Hook into vault
            XUtil.info("Hooked into Vault for Permission interfaces");
            return true;
		}
		return false;
	}
	
	private boolean hookEssentials()
	{
		Essentials ess = (Essentials)getServer().getPluginManager().getPlugin("Essentials");
		if (ess == null)
			return false;
		
		this.ess = ess;
		return true;
	}

	private void setupPermissionGroups()
	{
		groups = settings.loadGroups();
		if (groups == null)
		{
			XUtil.severe("permission.yml doesn't contain groups");
			return;
		}

		int countDefaultgroups = 0;
		for (Group group : groups)
		{
			if (group.isDefault() && !group.getName().equalsIgnoreCase("default"))
				countDefaultgroups++;
		}
		
		if (countDefaultgroups == 0)
		{
			XUtil.severe("No defaultgroup has been set. Select group[default] as defaultgroup!");
			getDefaultGroup().setDefault(true);
		}
		else
		if (countDefaultgroups == 1)
		{
			XUtil.info("Default group has to group: " + getDefaultGroup().getName());
		}
		else
		{
			XUtil.severe("More than one defaultgroup has been found. Select group[default] as defaultgroup!");
			for (Group group : groups)
			{
				if (group.isDefault() && !group.getName().equalsIgnoreCase("default"))
					group.setDefault(false);
			}
			getDefaultGroup().setDefault(true);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
	{
		XCommand xCommand = commands.get(command.getLabel());
		
		if (xCommand == null)
			return false;
		
		if (commandSender instanceof Player)
		{
			if (!((Player) commandSender).hasPermission(xCommand.getPermission()))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
				return true;
			}
			return xCommand.run((Player) commandSender, command, s, args);
		}
		else
		{
			if (!xCommand.run(commandSender, command, s, args))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
			}
		}
		return true;
	}
	
	
	

	public Group getDefaultGroup()
	{
		for (Group g : groups)
		{
			if (g.isDefault())
				return g;
		}

		for (Group g : groups)
		{
			if (g.getName().equals("default"))
				return g;
		}

		return null;
	}

	public List<Group> getGroups()
	{
		return this.groups;
	}
	
	public Group getGroup(int rank)
	{
		for (Group g : this.groups)
		{
			if (g.getRank() == rank)
				return g;
		}
		return null;
	}

	public SettingManager getSettingManager()
	{
		return this.settings;
	}

	public static XPermission getInstance()
	{
		return instance;
	}

	public PermissionManager getManager()
	{
		return this.manager;
	}
	
	public RankManager getRankManager()
	{
		return this.rankmanager;
	}
	
	public XApiConnector getAPI()
	{
		return this.xapiconn;
	}
	
	public boolean hasAPI()
	{
		return this.xapiconn != null;
	}
	
	public boolean hasEssentials()
	{
		return this.ess != null;
	}

}