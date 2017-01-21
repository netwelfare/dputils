package com.dputils.log;

public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		Log log = LogFactory.getLog(Test.class);
		log.info("hello world!");
	}

}
