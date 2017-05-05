package de.mark615.xpermission.object;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import de.mark615.xpermission.XPermission;

public class XPermissionAttachment extends PermissionAttachment
{
    private final XPermissible perm;
    private XPlayerSubject subject;
    private UUID identifier;
    
    public XPermissionAttachment(Plugin plugin, Player parent, XPermissible perm)
    {
        super(plugin, parent);
        this.perm = perm;
        this.identifier = UUID.randomUUID();
        this.subject = XPermission.getInstance().getManager().getXPlayerSubject(parent.getUniqueId());
    }

    @Override
    public Map<String, Boolean> getPermissions()
    {
        return subject.getPermissions();
    }

    @Override
    public void setPermission(String name, boolean value)
    {
    	subject.setPermission(identifier, name, value ? 1 : -1);
    }

    @Override
    public void setPermission(Permission perm, boolean value)
    {
        setPermission(perm.getName(), value);
    }

    @Override
    public void unsetPermission(String name)
    {
    	subject.setPermission(identifier, name, 0);
    }

    @Override
    public void unsetPermission(Permission perm)
    {
        unsetPermission(perm.getName());
    }

    @Override
    public boolean remove()
    {
        return perm.removeAttachmentInternal(this);
    }
    
    public UUID getIdentifier()
    {
    	return identifier;
    }
}
