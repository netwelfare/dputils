package com.dputils.lang;

public class Person
{
	//����Ҫ�Ƿ����ķ�װ������������Ե����Լ��ķ��������ǲ�Ҫ����ѭ�����á���Ȼ����д�Ĺ����У�����໹δ��ɣ����Ƿ����ĵ��ã��������û�����û�й�ϵ��
	//���ǵĹ�ע�����ڷ������棬ֻҪ��������ɵģ���ô����Ȼ������ˡ��ڵ��õĹ�����Ҫע��ѭ���ĵ��õ������������������˼��ɡ�

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
