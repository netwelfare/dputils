package com.dputils.pool.my;

public class MyPooledObject<T>
{
	/*
	 * 将T封装成一个池对象的思路不错。
	 */
	private final T object;

	public MyPooledObject(T object)
	{
		this.object = object;
	}

	public T getObject()
	{
		return object;
	}
}
