package com.dputils.clz;

import java.lang.reflect.Method;

public class ReflectionHelper
{

	public static void main(String[] args)
	{
		Method m = getMethod(ClassUtil.class, "addClasspath", String.class);
		System.out.println(m.toString());
	}

	public ReflectionHelper()
	{

	}

	public static <T> Method getMethod(Class<T> type, String methodName, Class<?>... argTypes)
	{
		try
		{
			Method m = type.getMethod(methodName, argTypes);
			return m;
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
