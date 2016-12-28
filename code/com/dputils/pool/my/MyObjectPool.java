package com.dputils.pool.my;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class MyObjectPool<T>
{
	private final LinkedBlockingDeque<MyPooledObject<T>> idleObjects;
	private final Map<MyPooledObject<T>, MyPooledObject<T>> allObjects = new ConcurrentHashMap<MyPooledObject<T>, MyPooledObject<T>>();

	public MyObjectPool()
	{
		idleObjects = new LinkedBlockingDeque<MyPooledObject<T>>();
	}

	public T borrowObject()
	{
		MyPooledObject<T> p = null;
		p = idleObjects.pollFirst();
		if (p == null)
		{
			p = create();
			/**出现问题：pool里面竟然生成了MyPooledObject对象。如果不这样的话，则分为两种方法：利用MyPooledObject的构造函数，
			 * 但是因为是泛型，无法生成具体的对象，及时生成一个具体的对象，也难以转换为泛型。这种情况下只有利用对象工厂了。程序设计思想之一。
			**/
			allObjects.put(p, p);
		}
		return p.getObject();
	}

	public abstract MyPooledObject<T> create();

	public void returnObject(T obj)
	{
		MyPooledObject<T> p = allObjects.get(new MyPooledObject<T>(obj));
		/**没有做任何校验，容易出问题。必须保证是借出来的对象。
		 * 用户借之前，要登记下所有的obj，然后将符合条件的登记到idleObjects里面
		 */
		if (p != null)
		{
			idleObjects.addLast(p);
		}
	}
}
