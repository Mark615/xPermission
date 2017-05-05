package de.mark615.xpermission.exception;

public class RankNotFoundException extends NoSuchFieldException
{
	private static final long serialVersionUID = -9186114546690271778L;

	public RankNotFoundException(int rank)
	{
		super("Can't found rank: " + rank);
	}
}
