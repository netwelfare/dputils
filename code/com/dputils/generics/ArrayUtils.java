package com.dputils.generics;

import java.util.Map;

public class ArrayUtils
{

	public ArrayUtils()
	{

	}

	public static <K, V> String[] getStringArray(Map<K, V> map)
	{
		return (String[]) map.keySet().toArray(new String[map.size()]);
	}
}
