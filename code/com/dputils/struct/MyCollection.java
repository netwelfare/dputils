package com.dputils.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyCollection
{

	public MyCollection()
	{

	}

	public static <T> List<T> getEmptyList()
	{
		final List<Thread> result = new ArrayList<Thread>();
		Collections.unmodifiableCollection(result);
		return Collections.emptyList();

	}

}
