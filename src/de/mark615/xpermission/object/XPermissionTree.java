package de.mark615.xpermission.object;

import java.util.ArrayList;
import java.util.List;

public class XPermissionTree
{
	private List<XPermissionNode> nodes;
	
	public XPermissionTree()
	{
		nodes = new ArrayList<>();
	}
	
	public void addPermission(String permission, int value)
	{
		if (permission == null)
			return;
		String[] perm = permission.split("\\.");
		
		XPermissionNode curNode = null;
		for (XPermissionNode node : nodes)
		{
			if (node.getRoot().equals(perm[0]))
			{
				curNode = node;
			}
		}
		
		if (curNode == null)
		{
			XPermissionNode node = new XPermissionNode(perm[0], value);
			nodes.add(node);
			curNode = node;
		}

		for (int i = 1; i < perm.length; i++)
		{
			curNode = addPermission0(curNode, perm[i], value);
		}
	}
	
	private XPermissionNode addPermission0(XPermissionNode tempNode, String perm, int value)
	{
		XPermissionNode curNode = null;
		
		if (tempNode.containsLeaf(perm))
		{
			curNode = tempNode.getLeaf(perm);
		}
		else
		{
			XPermissionNode node = new XPermissionNode(perm, value);
			tempNode.addLeaf(node);
			curNode = node;
		}
		
		return curNode;
	}
	
	public boolean get(String name)
	{
		if (name == null)
			return false;
		String[] perm  = name.split("\\.");
		
		for (XPermissionNode node : nodes)
		{
			if (node.getRoot().equals(perm[0]))
			{
				XPermissionNode childNode = node;
				if (perm.length > 1)
				{
					for (int i = 1; i < perm.length; i++)
					{
						childNode = get0(childNode, perm[i]);
						if (childNode == null)
							return false;
						if (perm.length == (i + 1))
							return true;
					}
				}
			}
		}
		return false;
	}
	
	private XPermissionNode get0(XPermissionNode node, String name)
	{
		return node.getLeaf(name);
	}
	
	public boolean has(String name)
	{
		if (name == null)
			return false;
		String[] perm  = name.split("\\.");
		
		int hasop = 0;
		int hasPermission = 0;
		for (XPermissionNode node : nodes)
		{
			if (node.getRoot().equals(perm[0]))
			{
				XPermissionNode childOpNode = node;
				XPermissionNode childNode = node;
				if (perm.length > 1)
				{
					for (int i = 1; i < perm.length; i++)
					{
						childOpNode = has0(childNode, "*");
						childNode = has0(childNode, perm[i]);
						if (childNode == null)
							break;
						
						if (childNode.isOp())
						{
							hasop = childNode.isActive();
						}
						else
						if (childOpNode != null && childOpNode.isOp())
						{
							hasop = childOpNode.isActive(); 
						}
						
						if (perm.length == (i + 1))
						{
							hasPermission = childNode.isActive();
							break;
						}
						
					}
				}
				else
				{
					hasPermission = childNode.isActive();
					break;
				}
			}
		}
		
		if (hasPermission == 0)
		{
			for (XPermissionNode node : nodes)
			{
				if (node.getRoot().equals("*") && node.isActive() == 1)
					return true;
			}
		}
		
		if (hasPermission != 0)
			return hasPermission == 1 ? true : false;
		
		return hasop == 1 ? true : false;
	}
	
	private XPermissionNode has0(XPermissionNode node, String name)
	{
		XPermissionNode nextNode = node.getLeaf(name);
		if (nextNode == null)
			nextNode = node.getLeaf("*");
		return nextNode;
	}

}
