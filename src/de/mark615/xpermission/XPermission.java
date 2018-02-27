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
import org.java.mcstats.Metrics;

import com.earth2me.essentials.Essentials;

import de.mark615.xcore.XCore;
import de.mark615.xcore.interfaces.XPermissionApi;
import de.mark615.xcore.versioncheck.VersionCheck;
import de.mark615.xcore.versioncheck.VersionCheck.XType;
import de.mark615.xpermission.api.ApiConnector;
import de.mark615.xpermission.command.CommandXPerm;
import de.mark615.xpermission.command.CommandXRank;
import de.mark615.xpermission.command.XCommand;
import de.mark615.xpermission.events.EssentialEvents;
import de.mark615.xpermission.events.PlayerEvents;
import de.mark615.xpermission.object.Group;
import de.mark615.xpermission.object.XUtil;
import de.mark615.xpermission.object.XVaultChat;
import de.mark615.xpermission.object.XVaultPermission;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class XPermission extends JavaPlugin
{
	public static final int BUILD = 9;
	public static final String PLUGIN_NAME = "[xPermission] ";
	
	private static XPermission instance = null;
	private Metrics metrics = null;

	private XCore xcore;
	private ApiConnector xapiconn = null;
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
		
		Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				setupXApi();
				hookEssentials();
				registerEvents();
				registerCommands();
			}
		}, 0);
		
		
		try{
			metrics = new Metrics(this);
			metrics.start();
			XUtil.info("hooked to [Metrics]");
		} catch (Exception e){
			XUtil.severe("Can't hook to [Metrics]", e);
		}
		
		XUtil.updateCheck(this);
		
		XVaultPermission permission = new XVaultPermission(this);
		hookVaultPermissions(permission);
		hookVaultChat(new XVaultChat(this, permission));
		
		loadPlugin();
		XUtil.info("Enabled Build " + BUILD);
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
	}
	
	private void loadPlugin()
	{
		setupPermissionGroups();
		settings.checkPermissionFile();
		
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.getManager().unregisterPlayer(p);
			this.getManager().clearXPlayerSubjectList();
			this.getManager().registerPlayerJoin(p);
		}
	}

	public static XPermission getInstance()
	{
		return instance;
	}
	
	

	private void registerEvents()
	{
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
		if (hasEssentials()) {
			Bukkit.getServer().getPluginManager().registerEvents(new EssentialEvents(this), this);
		}
	}
	
	private void registerCommands()
	{
		commands.put("xperm", new CommandXPerm(this));
		commands.put("xrank", new CommandXRank(this));
	}

	private boolean setupXApi() 
	{
		try {
			Class.forName("de.mark615.xcore.XCore");
		}
		catch (Exception e) {
			return false;
		}
		
		XCore xcore = (XCore)getServer().getPluginManager().getPlugin("xCore");
    	if(xcore == null)
    		return false;
    	
    	this.xcore = xcore;
    	try
    	{
	    	if (xcore.checkVersion(XType.xPermission, BUILD))
	    	{
	        	xapiconn = new ApiConnector(this, new XPermissionApi(xcore));
	        	xcore.registerXPermission(xapiconn.getApi());
	            XUtil.info("hooked to [xCore]");
	    	}
	    	else
	    	{
	    		XUtil.severe("Can't hook to xCore!"); 
	    		if (VersionCheck.isXPluginHigherXApi(XType.xPermission, BUILD))
	    		{
		    		XUtil.warning("Please update your xCore!");
		    		XUtil.warning("Trying to hook to xCore. Have an eye into console for errors with xCore!");

		    		xapiconn = new ApiConnector(this, new XPermissionApi(xcore));
		        	xcore.registerXPermission(xapiconn.getApi());
	    		}
	    		else
	    		{
		    		XUtil.severe("Please update your xPermission for hooking.");
	    		}
	    	}
    	}
    	catch (Exception e)
    	{
    		XUtil.severe("An error accurred during connection to xCore!");
    	}
    	
    	return xapiconn != null;
	}
	
	private boolean hookEssentials()
	{
		Essentials ess = (Essentials)getServer().getPluginManager().getPlugin("Essentials");
		if (ess == null)
			return false;

		this.ess = ess;
		XUtil.info("hooked to [Essentials2]");
		return true;
	}
	
	private boolean hookVaultPermissions(XVaultPermission vault)
	{
		if (getServer().getPluginManager().isPluginEnabled("Vault"))
		{
            getServer().getServicesManager().register(Permission.class, vault, this, ServicePriority.High); // Hook into vault
            XUtil.info("Activate [Vault][Permission]");
            return true;
		}
		return false;
	}
	
	private boolean hookVaultChat(XVaultChat vault)
	{
		if (getServer().getPluginManager().isPluginEnabled("Vault"))
		{
            getServer().getServicesManager().register(Chat.class, vault, this, ServicePriority.High); // Hook into vault
            XUtil.info("Activate [Vault][Chat]");
            return true;
		}
		return false;
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
			return xCommand.runCommand((Player) commandSender, command, s, args);
		}
		else
		{
			if (!xCommand.runCommand(commandSender, command, s, args))
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

	public boolean hasXCore()
	{
		return (xcore != null && xcore.getXPlugin(XType.xPermission) != null);
	}
	
	public boolean hasXApi(XType type)
	{
		return (hasXCore() && xcore.getXPlugin(type) != null);
	}
	
	public XCore getXCore()
	{
		return xcore;
	}
	
	public boolean hasApiConnector()
	{
		return xapiconn != null;
	}
	
	public ApiConnector getApiConnector()
	{
		return xapiconn;
	}
	
	public boolean hasEssentials()
	{
		return this.ess != null;
	}

}