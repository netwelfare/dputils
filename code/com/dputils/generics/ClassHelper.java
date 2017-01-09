package com.dputils.generics;

public class ClassHelper
{

	public ClassHelper()
	{
		// TODO Auto-generated constructor stub
	}

	public <T> void helper(String str, Class<T> type)
	{
		Class clz = str.getClass();
		Class clz2 = String.class;
		Class clz3 = type;
		clz3 = type.getClass();
	}

}
