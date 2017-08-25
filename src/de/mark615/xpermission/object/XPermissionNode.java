package de.mark615.xpermission.object;

import java.util.ArrayList;
import java.util.List;

public class XPermissionNode
{
	private String root;
	private boolean op;
	private int value;
	private List<XPermissionNode> leafs;
	
	public XPermissionNode(String root, int value)
	{
		this.op = false;
		this.value = value;
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
		if (node != null && !op)
			this.leafs.add(node);
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public boolean isOp()
	{
		return op;
	}
	
}
