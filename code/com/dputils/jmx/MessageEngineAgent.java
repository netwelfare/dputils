package com.dputils.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MessageEngineAgent
{
	public void start()
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try
		{
			ObjectName mxbeanName = new ObjectName("com.example:type=MessageEngine");
			MessageEngineMXBean mxbean = new MessageEngine();
			mbs.registerMBean(mxbean, mxbeanName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
