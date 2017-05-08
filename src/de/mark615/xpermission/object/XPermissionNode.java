package de.mark615.xpermission.object;

import java.util.ArrayList;
import java.util.List;

public class XPermissionNode
{
	private String root;
	private boolean op;
	private int active;
	private List<XPermissionNode> leafs;
	
	public XPermissionNode(String root, int active)
	{
		this.op = false;
		this.active = active;
		this.root = root;
		this.leafs = new ArrayList<>();
		if (root.equals("*"))
		{
			this.op = true;
		}
	}
	
	public String getRoot()
	{
		return root;
	}
	
	public boolean isLeaf()
	{
		return leafs.size() == 0;
	}
	
	public boolean containsLeaf(String leaf)
	{
		for (XPermissionNode node : leafs)
		{
			if (node.getRoot().equals(leaf))
			{
				return true;
			}
		}
		return false;
	}
	
	public XPermissionNode getLeaf(String leaf)
	{
		for (XPermissionNode node : leafs)
		{
			if (node.getRoot().equals(leaf))
			{
				return node;
			}
		}
		return null;
	}
	
	public void addLeaf(XPermissionNode node)
	{
		if (node != null)
			this.leafs.add(node);
	}
	
	public int isActive()
	{
		if (leafs.size() > 0)
			return 0;
		return active;
	}
	
	public boolean isOp()
	{
		return op;
	}
	
}
