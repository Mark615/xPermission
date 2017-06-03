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
import de.mark615.xapi.versioncheck.VersionCheck;
import de.mark615.xapi.versioncheck.VersionCheck.XType;
import de.mark615.xpermission.command.CommandXPerm;
import de.mark615.xpermission.command.CommandXRank;
import de.mark615.xpermission.command.XCommand;
import de.mark615.xpermission.events.PlayerEvents;
import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.Updater;
import de.mark615.xpermission.object.Updater.UpdateResult;
import de.mark615.xpermission.object.Updater.UpdateType;
import de.mark615.xpermission.object.XUtil;
import net.milkbowl.vault.permission.Permission;

public class XPermission extends JavaPlugin
{
	public static final int BUILD = 5;
	public static final String PLUGIN_NAME = "[xPermission] ";
	public static final String PLUGIN_NAME_SHORT = "[xPerm] ";
	
	private static XPermission instance = null;

	private XApiConnector xapiconn = null;
	private Essentials ess = null;

	private SettingManager settings = null;
	private PermissionManager manager = null;
	private PermissionEditor editor = null;
	private RankManager rankmanager = null;
	
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
		this.editor = new PermissionEditor(this);
		this.rankmanager = new RankManager(this);

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
		
		XUtil.onEnable();
		updateCheck();
		hookVaultPermissions();
		loadPlugin();
	}

	@Override
	public void onDisable()
	{
		XUtil.onDisable();
		this.groups = null;
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.manager.unregisterPlayer(p); 
		}
		this.settings.savePermission();
	}
	
	public void loadPlugin()
	{
		setupPermissionGroups();
		settings.checkPermissionFile();
		
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.getManager().unregisterPlayer(p);
			this.getManager().registerPlayer(p);
		}
	}
	
	private void updateCheck()
	{
		if (SettingManager.getInstance().hasCheckVersion())
		{
			try
			{
				Updater updater = new Updater(this, 266896, this.getFile(), UpdateType.NO_DOWNLOAD, true);
				if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				    XUtil.info("New version available! " + updater.getLatestName());
				}
			}
			catch(Exception e)
			{
				XUtil.severe("Can't generate checkUpdate webrequest");
			}
		}
	}

	public static XPermission getInstance()
	{
		return instance;
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
    	
    	try
    	{
	    	if (xapi.checkVersion(XType.xPermission, BUILD))
	    	{
	        	xapiconn = new XApiConnector(xapi, this);
	        	xapi.registerXPermission(xapiconn);
	    	}
	    	else
	    	{
	    		XUtil.severe("Can't hook to xApi!"); 
	    		if (VersionCheck.isXPluginHigherXApi(XType.xPermission, BUILD))
	    		{
		    		XUtil.warning("Please update your xApi!");
		    		XUtil.warning("Trying to hook to xApi. Have an eye into console for errors with xApi!");

		        	xapiconn = new XApiConnector(xapi, this);
		        	xapi.registerXPermission(xapiconn);
	    		}
	    		else
	    		{
		    		XUtil.severe("Please update your xPermission for hooking.");
	    		}
	    	}
    	}
    	catch (Exception e)
    	{
    		XUtil.severe("An error accurred during connection to xApi!");
    	}
    	
    	return xapiconn != null;
	}
	
	private boolean hookVaultPermissions()
	{
		if (getServer().getPluginManager().isPluginEnabled("Vault"))
		{
            final XVault vault = new XVault(this);
            getServer().getServicesManager().register(Permission.class, vault, this, ServicePriority.High); // Hook into vault
            XUtil.info("Activate Vault permissioninterfaces");
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
			if (group.isDefault())
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
			XUtil.info("Default group is: " + getDefaultGroup().getName());
		}
		else
		{
			countDefaultgroups = 0;
			for (Group group : groups)
			{
				if (group.isDefault() && !group.getName().equalsIgnoreCase("default"))
					countDefaultgroups++;
			}
			if (countDefaultgroups == 1)
			{
				for (Group group : groups)
				{
					if (group.isDefault() && group.getName().equalsIgnoreCase("default"))
						group.setDefault(false);
				}
			}
			if (countDefaultgroups > 1)
			{
				XUtil.severe("More than one defaultgroup has been found. Select group[default] as defaultgroup!");
				for (Group group : groups)
				{
					if (group.getName().equalsIgnoreCase("default"))
						group.setDefault(true);
					else
						group.setDefault(false);
				}
			}
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

	public PermissionManager getManager()
	{
		return this.manager;
	}
	
	public PermissionEditor getPermissionEditor()
	{
		return editor;
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