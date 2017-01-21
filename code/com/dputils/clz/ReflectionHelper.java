package com.dputils.clz;

import java.lang.reflect.Constructor;
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

	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... argTypes)
	{
		try
		{
			Constructor<T> c = type.getConstructor(argTypes);
			return c;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static <T> T getObject(Constructor<T> c, Object[] args)
	{
		try
		{
			Object o = c.newInstance(args);
			return (T) o;
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
