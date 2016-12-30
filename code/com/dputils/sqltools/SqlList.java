package com.dputils.sqltools;

import java.util.ArrayList;

public class SqlList<E> extends ArrayList<E>
{

	private static final long serialVersionUID = 1L;

	public String toSql()
	{
		StringBuilder sb = new StringBuilder();
		Object[] o = this.toArray();
		for (Object temp : o)
		{
			sb.append(temp.toString());
			sb.append(" ");
		}
		return sb.toString();
	}
}
