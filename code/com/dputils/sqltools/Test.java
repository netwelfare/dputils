package com.dputils.sqltools;

public class Test
{

	public static void main(String[] args)
	{
		SqlList sqlList = new SqlList();
		sqlList.add(new Select());
		sqlList.add(new From());
		sqlList.add(new Where());

		Table scrTable = new Table("srctable", "t1");
		Column srcCol = new Column(scrTable, "name", "");

		Table destTable = new Table("desttable", "t2");
		Column destCol = new Column(destTable, "name", "");

		Join join = new Join("left", scrTable, srcCol, destTable, destCol);
		sqlList.add(join);
		System.out.println(sqlList.toSql());

	}

}
