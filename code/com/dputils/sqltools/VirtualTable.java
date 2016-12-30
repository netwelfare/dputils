package com.dputils.sqltools;

public class VirtualTable extends Table
{

	public VirtualTable(String literal)
	{
		this.literal = literal;
	}

	public String getName()
	{
		return literal;
	}

}
