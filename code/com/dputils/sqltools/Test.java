package com.dputils.sqltools;

public class Test
{

	public static void main(String[] args)
	{
		//		SqlList<SqlElement> sqlList = new SqlList<SqlElement>();
		//		sqlList.add(new Select());
		//		sqlList.add(new From());
		//		sqlList.add(new Where());
		//
		//		Table scrTable = new Table("srctable", "t1");
		//		Column srcCol = new Column(scrTable, "name", "");
		//
		//		Table destTable = new Table("desttable", "t2");
		//		Column destCol = new Column(destTable, "name", "");
		//
		//		Join join = new Join("left", scrTable, srcCol, destTable, destCol);
		//		sqlList.add(join);
		//		System.out.println(sqlList.toSql());

		//		Column col1 = new Column("name", "");
		//		Column col2 = new Column("name", "");
		//		MatchCondition condition = new MatchCondition(col1, MatchCondition.LIKE, col2);
		//		System.out.println(condition.toString());

		SqlList<SqlElement> sql = new SqlList<SqlElement>();
		//		sql.add(new Select());
		//		VirtualTable tb3 = new VirtualTable("tb3");
		//		Column c1 = new Column(tb3, "report_date");
		//		sql.add(c1);
		//		VirtualColumn c2 = new VirtualColumn("sum");
		//		Function f = new Function("sum", c2);
		//		sql.add(f);
		//		sql.add(new From());
		//		sql.add(new Bracket("left"));
		//		sql.add(new Bracket("right"));
		//		sql.add(tb3);

		Table t1 = new Table("ODS_NEWACCOUNT_KYLIN", "t1");
		Column c1 = new Column(t1, "report_date");
		Column c2 = new Column(t1, "hour_value");
		Column c3 = new Column(t1, "firm_id");
		Column c4 = new Column(t1, "account_id");
		sql.add(new Select());
		sql.add(c1);
		sql.add(c2);
		sql.add(c3);

		Function f = new Function("count", "distict", c4);
		sql.add(f);
		sql.add(new From());
		sql.add(t1);
		VirtualTable t2 = new VirtualTable("t2");
		Column c5 = new Column(t2, "account_id");
		Join join = new Join("", t1, c4, t2, c5);
		sql.add(join);
		System.out.println(sql.toSql());
	}

}
