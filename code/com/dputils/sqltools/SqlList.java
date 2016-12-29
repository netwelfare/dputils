package com.dputils.sqltools;

import java.util.ArrayList;

public class SqlList<E> extends ArrayList<E>
{

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
