package com.dputils.sqltools;

public class Column extends SqlElement
{
	private Table table;

	public Column(String name, String alias)
	{
		this.name = name;
		this.alias = alias;
	}

	public Column(Table table, String name)
	{
		this.table = table;
		this.name = name;
	}

	public Column(Table table, String name, String alias)
	{
		this(name, alias);
		this.table = table;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (table != null)
		{
			sb.append(table.getName());
			sb.append(".");
		}
		sb.append(name);
		//		sb.append(" ");
		//		sb.append(alias);
		return sb.toString();
	}
}
