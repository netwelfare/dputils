package com.dputils.clz;

import java.util.HashMap;
import java.util.Map;

public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static <T> void main(String[] args)
	{
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("name", "map1");
		map1.put("sex", "m");
		System.out.println(map1.toString());
		Map<String, String> map2 = map1;
		map2.put("name", "map2");
		System.out.println(map1.toString());

		Map<String, String> map3 = new HashMap<String, String>();

		PropertyCopier.copyBeanProperties(HashMap.class, map1, map3);
		map3.put("name", "map3");
		System.out.println(map1.toString());

		Map<String, String> map4 = SerializationUtils.clone(map1);
		map4.put("name", "map4");
		System.out.println(map1.toString());

	}

}
