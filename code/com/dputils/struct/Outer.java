package com.dputils.struct;

public class Outer
{

	private String name = "out";

	private class InnerClassA
	{
		String innerName = name;
		Outer out = Outer.this;
	}

	private static class InnerClassB
	{

	}

	public static void main(String[] args)
	{
		Outer outer = new Outer();
		Outer.InnerClassA a = outer.new InnerClassA();
	}
}
