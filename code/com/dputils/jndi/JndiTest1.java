package com.dputils.jndi;

/*
 * Created on 2005-3-1
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

/**
* @author shizy
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
public class JndiTest1
{
	/**
	*
	*/
	public JndiTest1()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		try
		{
			Properties env = new Properties();
			env.load(new FileInputStream("D:\\git\\dputils\\fileSystemService.properties"));
			env.put(Context.PROVIDER_URL, "file:///c:/");
			Context ctx = new InitialContext(env);
			ctx.createSubcontext("sylilzy");

			NamingEnumeration list = ctx.list("/");
			while (list.hasMore())
			{
				NameClassPair nc = (NameClassPair) list.next();
				System.out.println(nc);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}