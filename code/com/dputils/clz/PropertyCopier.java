package com.dputils.clz;

import java.lang.reflect.Field;

public class PropertyCopier
{

	public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean)
	{
		Class<?> parent = type;
		while (parent != null)
		{
			final Field[] fields = parent.getDeclaredFields();
			for (Field field : fields)
			{
				try
				{
					field.setAccessible(true);
					field.set(destinationBean, field.get(sourceBean));
				}
				catch (Exception e)
				{

				}
			}
			parent = parent.getSuperclass();
		}
	}

}
