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
			/**�������⣺pool���澹Ȼ������MyPooledObject��������������Ļ������Ϊ���ַ���������MyPooledObject�Ĺ��캯����
			 * ������Ϊ�Ƿ��ͣ��޷����ɾ���Ķ��󣬼�ʱ����һ������Ķ���Ҳ����ת��Ϊ���͡����������ֻ�����ö��󹤳��ˡ��������˼��֮һ��
			**/
			allObjects.put(p, p);
		}
		return p.getObject();
	}

	public abstract MyPooledObject<T> create();

	public void returnObject(T obj)
	{
		MyPooledObject<T> p = allObjects.get(new MyPooledObject<T>(obj));
		/**û�����κ�У�飬���׳����⡣���뱣֤�ǽ�����Ķ���
		 * �û���֮ǰ��Ҫ�Ǽ������е�obj��Ȼ�󽫷��������ĵǼǵ�idleObjects����
		 */
		if (p != null)
		{
			idleObjects.addLast(p);
		}
	}
}
