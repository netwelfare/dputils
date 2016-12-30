package com.dputils.sqltools;

public class VirtualColumn extends SqlElement
{

	public VirtualColumn(String literal)
	{
		this.literal = literal;
	}

	public String toString()
	{
		return literal;
	}
}
