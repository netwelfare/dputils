package com.dputils.pool.my;

public class MyPooledObject<T>
{
	/*
	 * ��T��װ��һ���ض����˼·����
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
