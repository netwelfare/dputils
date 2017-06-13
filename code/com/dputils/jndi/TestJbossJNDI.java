package com.dputils.jndi;

/*
 * Created on 2005-3-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
* @author shizy
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
public class TestJbossJNDI
{
	/**
	*
	*/
	public TestJbossJNDI()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		try
		{
			Properties env = new Properties();
			//����jboss��SPI��ز���,������ʼ�����Ĺ���������URL���ȵ�
			env.load(new FileInputStream("D:\\git\\dputils\\jbossJndi.properties"));
			env.list(System.out);
			//ͨ��JNDI api ��ʼ��������
			InitialContext ctx = new javax.naming.InitialContext(env);
			System.out.println("Got context");
			//create a subContext
			ctx.createSubcontext("/sylilzy");
			ctx.createSubcontext("sylilzy/sily");
			//rebind a object
			ctx.rebind("sylilzy/sily/a", "I am sily a!");
			ctx.rebind("sylilzy/sily/b", "I am sily b!");
			//lookup context
			Context ctx1 = (Context) ctx.lookup("sylilzy");
			Context ctx2 = (Context) ctx1.lookup("/sylilzy/sily");
			ctx2.bind("/sylilzy/g", "this is g");
			//lookup binded object
			Object o;
			o = ctx1.lookup("sily/a");
			System.out.println("get object from jndi:" + o);
			//rename the object
			ctx2.rename("/sylilzy/g", "g1");
			o = ctx2.lookup("g1");
			System.out.println("get object from jndi:" + o);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
