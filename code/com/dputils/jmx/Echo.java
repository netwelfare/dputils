package com.dputils.jmx;

public class Echo
{
	public static Message msg = new Message();
	public static boolean running = true;
	public static boolean pause = false;

	public static void main(String[] args)
	{
		// ����JMX Agent���������ҪJMX,ֻ�ǵ������г��������ε��������д��롣
		new MessageEngineAgent().start();

		while (running)
		{
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			if (!pause)
				msg.echo();
		}
	}
}