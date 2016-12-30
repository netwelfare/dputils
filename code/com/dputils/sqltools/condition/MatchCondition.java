package com.dputils.sqltools.condition;

import com.dputils.sqltools.SqlElement;
import com.dputils.sqltools.SqlElmentBuilder;

public class MatchCondition extends SqlElement
{

	public static final String EQUALS = "=";
	public static final String GREATER = ">";
	public static final String GREATEREQUAL = ">=";
	public static final String LESS = "<";
	public static final String LESSEQUAL = "<=";
	public static final String LIKE = "LIKE";
	public static final String NOTEQUAL = "<>";

	private final SqlElement left;
	private final String operator;
	private final SqlElement right;

	public MatchCondition(SqlElement left, String operator, SqlElement right)
	{
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public String toString()
	{
		SqlElmentBuilder sb = new SqlElmentBuilder();
		if (left.getValue() == null)
		{
			sb.append(left.toString());
		}
		else
		{
			sb.append(left.toValue());
		}
		sb.append(operator);
		if (right.getValue() == null)
		{
			sb.append(right.toString());
		}
		else
		{
			sb.append(right.toValue());
		}
		return sb.toString();
	}

}
