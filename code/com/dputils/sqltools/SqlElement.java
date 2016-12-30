package com.dputils.sqltools;

import java.util.List;

public class SqlElement
{
	protected String literal;

	protected String name;
	protected String alias;

	protected Object value;

	protected List<Object> valueList;

	public String toString()
	{
		return literal;
	}

	public SqlElement(Object value)
	{
		this.value = value;
	}

	public SqlElement()
	{

	}

	protected String toValue(Object value)
	{
		if (value instanceof String)
		{
			return "'" + value + "'";
		}
		return null;
	}

	public String toValue()
	{
		return toValue(value);
	}

	public Object getValue()
	{
		return value;
	}

}
