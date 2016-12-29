package com.dputils.sqltools;

public class Table extends SqlElement
{
	public Table(String name, String alias)
	{
		this.name = name;
		this.alias = alias;
	}

	public String getName()
	{
		return name;
	}

	public String getFullName()
	{
		return alias == null ? name : name + " " + alias;
	}

	public String getAlias()
	{
		return alias == null ? name : alias;
	}
}
