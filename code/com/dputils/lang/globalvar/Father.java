package com.dputils.lang.globalvar;

public class Father
{
	// ��̬����Ҳ����ȫ�ֱ������������ж����ô�죿�����������������
	public Father()
	{

	}

	public static void main(String[] args)
	{
		Sun.foods.put("apple", "ƻ��");
		Sun.foods.put("bananer", "�㽶");
		new MyThread().run();
	}
}
