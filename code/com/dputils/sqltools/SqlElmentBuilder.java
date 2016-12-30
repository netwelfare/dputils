package com.dputils.sqltools;

public class SqlElmentBuilder
{

	private final StringBuilder sb;

	public SqlElmentBuilder()
	{
		sb = new StringBuilder();
	}

	public void append(Object o)
	{
		sb.append(o);
		sb.append(" ");
	}

	public String toString()
	{
		return sb.toString();
	}
}
