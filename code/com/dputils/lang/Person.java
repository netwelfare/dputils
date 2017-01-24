package com.dputils.lang;

public class Person
{
	//类主要是方法的封装，在类里面可以调用自己的方法，但是不要产生循环调用。虽然在书写的过程中，这个类还未完成，但是方法的调用，跟类完成没有完成没有关系。
	//我们的关注点在于方法上面，只要方法是完成的，那么它自然就完成了。在调用的过程中要注意循环的调用的情况。这是面向对象的思想吧。

	Person p;//= new Person();

	public Person()
	{
		//p = new Person();
	}

	public void say()
	{
		p = new Person();
		p.say();
	}

	public static void main(String[] args)
	{
		Person p = new Person();
		p.say();
	}

}
