package com.dputils.lang;

public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		//		int temp = RandomUtils.nextInt(1, 100);
		//		System.out.println(temp);
		//		String result = RandomStringUtils.randomAlphabetic(10);
		//		System.out.println(result);
		//		char ch = 126;
		//		System.out.println(ch);

		StringBuilder sb = new StringBuilder();
		sb.append("1");
		sb.append("2");
		sb.append("3");
		sb.append("4");

		StringBuilder sb2 = (StringBuilder) CharSequenceUtils.subSequence(sb, 1);

	}

}
