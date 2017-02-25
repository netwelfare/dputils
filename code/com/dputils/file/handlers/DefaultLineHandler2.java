package com.dputils.file.handlers;

public class DefaultLineHandler2 implements LineHandler
{

	@Override
	public String handleKey(String s)
	{
		String[] temp = s.split("=");
		return temp[0];
	}

	@Override
	public String handleValue(String s)
	{
		String[] temp = s.split("=");
		if (temp.length > 1)
		{
			return temp[1];
		}
		else
		{
			return null;
		}
	}
}
