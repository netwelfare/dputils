package com.dputils.sqltools.condition;

import java.util.List;

import com.dputils.sqltools.SqlElement;
import com.dputils.sqltools.SqlElmentBuilder;

public class InCondition extends SqlElement
{
	private final SqlElement left;

	public InCondition(SqlElement left, List<Object> valueList)
	{
		this.left = left;
		this.literal = "in";
		this.valueList = valueList;
	}

	public String toString()
	{
		SqlElmentBuilder sb = new SqlElmentBuilder();
		sb.append(left.toString());
		sb.append("in");
		sb.append("(");

		if (valueList != null && valueList.size() > 0)
		{
			for (int i = 0; i < valueList.size(); i++)
			{
				Object value = valueList.get(i);
				sb.append(toValue(value));
				if (i != valueList.size() - 1)
				{
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

}
