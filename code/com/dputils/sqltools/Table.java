package com.dputils.sqltools;

public class Table extends SqlElement
{
	public Table()
	{
	}

	public Table(String name, String alias)
	{
		this.name = name;
		this.alias = alias;
	}

	public String getName()
	{
		return alias == null ? name : alias;
	}

	public String toString()
	{
		return name + " " + alias;
	}
}
