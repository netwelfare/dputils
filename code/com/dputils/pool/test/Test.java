package com.dputils.pool.test;

import com.dputils.pool.GenericKeyedObjectPool;

public class Test
{
	public static void main(String[] args) throws Exception
	{
		WaiterFactory<String> waiterFactory = new WaiterFactory<String>(0, 20, 0, 0, 0, 0, 50, 5, 0);
		GenericKeyedObjectPool<String, Waiter> waiterPool = new GenericKeyedObjectPool<String, Waiter>(waiterFactory);
		waiterPool.setMaxTotalPerKey(5);
		waiterPool.setMaxTotal(50);
		waiterPool.setLifo(false);
		for (int i = 0; i < 10; i++)
		{
			final String key = Integer.valueOf(i).toString();
			for (int j = 0; j < 5; j++)
			{
				System.out.println(key);
				waiterPool.addObject(key);
			}

			Thread.sleep(20);
		}
		Thread.sleep(50);
		Waiter waiter = waiterPool.borrowObject("10");
		Thread.sleep(200);
		waiterPool.returnObject("10", waiter);
		waiterPool.close();

	}

}
