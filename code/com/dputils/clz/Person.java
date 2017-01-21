package com.dputils.clz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Person
{
	String name;
	Integer age;

	public Person(String name, Integer age)
	{
		this.name = name;
		this.age = age;
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class clz = Person.class;
		Class[] clzs = new Class[2];
		clzs[0] = String.class;
		clzs[1] = Integer.class;
		Constructor c = ReflectionHelper.getConstructor(Person.class, clzs);
		Object[] arg = new Object[2];
		arg[0] = "wxf";
		arg[1] = 30;
		Person p = (Person) ReflectionHelper.getObject(c, arg);
	}
}
