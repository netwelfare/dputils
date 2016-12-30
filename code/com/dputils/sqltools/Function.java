package com.dputils.sqltools;

public class Function extends SqlElement
{
	private final SqlElement argument;
	private String distinct;

	public Function(String name, SqlElement argument)
	{
		this.name = name;
		this.argument = argument;
	}

	public Function(String name, String distinct, SqlElement argument)
	{
		this.name = name;
		this.distinct = distinct;
		this.argument = argument;
	}

	public String toString()
	{
		SqlElmentBuilder sb = new SqlElmentBuilder();
		sb.append(name);
		sb.append("(");
		if (distinct != null)
		{
			sb.append(distinct);
		}
		sb.append(argument.toString());
		sb.append(")");
		return sb.toString();
	}

}
