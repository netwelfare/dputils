package com.dputils.sqltools;

public class Join extends SqlElement
{
	private String joinType;
	private Table srcTable;
	private Column srcCol;
	private Table destTable;
	private Column destCol;

	public Join(String joinType, Table srcTable, Column srcCol, Table destTable, Column destCol)
	{
		this.joinType = joinType;
		this.literal = "join on";
		this.srcTable = srcTable;
		this.srcCol = srcCol;
		this.destTable = destTable;
		this.destCol = destCol;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (srcTable != null)
		{
			sb.append(srcTable.getName());
		}
		sb.append(" ");
		sb.append(joinType);
		sb.append(" ");
		sb.append(literal.split(" ")[0]);
		sb.append(" ");
		sb.append(destTable.getName());
		sb.append(" ");
		sb.append(literal.split(" ")[1]);
		sb.append(" ");
		sb.append(srcCol.toString());
		sb.append("=");
		sb.append(destCol.toString());
		return sb.toString();
	}
}
