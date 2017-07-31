package de.mark615.xpermission.object;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionRemovedExecutor;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableSet;

import de.mark615.xpermission.PermissionManager;
import de.mark615.xpermission.XPermission;

public class XPermissible extends PermissibleBase
{

    private final Player player;
    private final XPermission plugin;
    private XPlayerSubject subject;
    private Permissible previousPermissible;
    private final Set<XPermissionAttachment> attachments = new HashSet<>();

    public XPermissible(Player player, XPermission plugin) throws ExecutionException, InterruptedException
    {
        super(player);
        this.player = player;
        this.plugin = plugin;
    }
    
    public void register()
    {
        this.subject = plugin.getManager().getXPlayerSubject(player.getUniqueId());
    }

    public PermissionManager getManager()
    {
        return this.plugin.getManager();
    }
    

    @Override
    public boolean isOp()
    {
    	return super.isOp();
    }

    @Override
    public void setOp(boolean value)
    {
    	super.setOp(value);
    	ConfigurationSection section = this.plugin.getSettingManager().getPlayerConfigurationsection(subject.getUUID());
    	if (section != null)
    	{
    		section.set("op", value);
    	}
    }

    @Override
    public boolean isPermissionSet(String name)
    {
    	if (name == null)
    		return false;
    	name = name.toLowerCase();

//    	System.out.println("isPermission: " + name + "; " + getPermissionValue(name, "get"));
    	return getPermissionValue(name, "get");
    }

    @Override
    public boolean isPermissionSet(Permission perm)
    {
        return perm != null ? isPermissionSet(perm.getName()) : false;
    }

    private boolean getPermissionValue(String permission, String type)
    {
    	if (permission == null)
    		return false;
    	
    	if (permission.trim().isEmpty())
    		return true;
    	
    	XPermissionTree nodeTree = subject.getPermissions(player);
    	if (type.equalsIgnoreCase("get"))
    	{
    		return nodeTree.get(permission);
    	}
    	else
    	if (type.equalsIgnoreCase("has"))
    	{
    		return nodeTree.has(permission);
    	}
    	return false;
    }

    @Override
    public boolean hasPermission(String inName)
    {
    	if (inName == null)
    		return false;
    	inName = inName.toLowerCase();
    	
    	if (isOp())
    		return true;
    	
//    	System.out.println("hasPermission: " + inName + "; " + getPermissionValue(inName, "has"));
    	return getPermissionValue(inName, "has");
    }

    @Override
    public boolean hasPermission(Permission perm)
    {
    	return perm != null ? hasPermission(perm.getName()) : false; 
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
    {
    	final XPermissionAttachment attach = new XPermissionAttachment(plugin, player, this);
    	attach.setPermission(name, value);
    	this.attachments.add(attach);
    	subject.setPermission(attach.getIdentifier(), name.toLowerCase(), value ? 1 : -1);
    	return attach;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin)
    {
        final XPermissionAttachment attach = new XPermissionAttachment(plugin, player, this);
        this.attachments.add(attach);
        return attach;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
    {
        return super.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks)
    {
        return addAttachment(plugin); // TODO: Implement timed permissions
    }

    public boolean removeAttachmentInternal(final XPermissionAttachment attach)
    {
        PermissionRemovedExecutor exec = attach.getRemovalCallback();
        if (exec != null) {
            exec.attachmentRemoved(attach);
        }
        return true;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment)
    {
        if (!(attachment instanceof XPermissionAttachment)) {
            throw new IllegalArgumentException("Provided attachment was not a PEX attachment!");
        }
        removeAttachmentInternal(((XPermissionAttachment) attachment));
        this.attachments.remove(attachment);
        subject.removePermission(((XPermissionAttachment) attachment).getIdentifier());
    }

    void removeAllAttachments()
    {
    	for (XPermissionAttachment attach : this.attachments)
    	{
    		removeAttachmentInternal(attach);
    	}
    	this.attachments.clear();
    	subject.clearPermission();
    }

    @Override
    public void recalculatePermissions()
    {
    }

    @Override
    public synchronized void clearPermissions()
    {
    	removeAllAttachments();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        ImmutableSet.Builder<PermissionAttachmentInfo> ret = ImmutableSet.builder();
        Map<String, Boolean> perms = subject.getPermissions();
        for (String key : perms.keySet())
        {
        	ret.add(new PermissionAttachmentInfo(player, key, null, perms.get(key)));
        }
        return ret.build();
    }

    public void setPreviousPermissible(Permissible previousPermissible)
    {
        this.previousPermissible = previousPermissible;
    }

    public Permissible getPreviousPermissible()
    {
        return previousPermissible;
    }
}
