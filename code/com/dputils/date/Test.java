package com.dputils.date;

import java.util.Date;
import java.util.TimeZone;

public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{

		FastDateFormat f = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS", TimeZone.getTimeZone("GMT+2"));
		System.out.println(f.format(new Date(0)));

	}

}
