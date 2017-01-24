package com.dputils.lang.globalvar;

public class Father
{
	// 静态变量也就是全局变量，但是类的卸载怎么办？这种情况是有隐患的
	public Father()
	{

	}

	public static void main(String[] args)
	{
		Sun.foods.put("apple", "苹果");
		Sun.foods.put("bananer", "香蕉");
		new MyThread().run();
	}
}
