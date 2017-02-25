package com.dputils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dputils.file.handlers.LineHandler;

public class BigDataTool
{
	private static FileInputStream fis = null;
	private static InputStreamReader isr = null;
	private static BufferedReader br = null;

	private static FileOutputStream fos = null;
	private static OutputStreamWriter osr = null;
	private static BufferedWriter bw = null;

	public static BufferedReader getBufferedReader(String file)
	{
		try
		{
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "utf-8");
		}
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		br = new BufferedReader(isr);
		return br;
	}

	public static Map getMap(BufferedReader br, Map map, LineHandler lineHandler)
	{
		String s = null;
		try
		{
			while ((s = br.readLine()) != null)
			{
				String key = lineHandler.handleKey(s);
				String value = lineHandler.handleValue(s);
				map.put(key, value);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	public static BufferedWriter BufferedWriter(String file)
	{
		try
		{
			fos = new FileOutputStream(file);
			osr = new OutputStreamWriter(fos, "utf-8");
		}
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		bw = new BufferedWriter(osr);

		return bw;
	}

	public static void closeReader()
	{
		if (fis != null)
		{
			try
			{
				fis.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (isr != null)
		{
			try
			{
				isr.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (br != null)
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void closeWriter()
	{
		if (fos != null)
		{
			try
			{
				fos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (osr != null)
		{
			try
			{
				osr.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (bw != null)
		{
			try
			{
				bw.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void mapTraverse(Map<?, ?> map)
	{
		for (Entry<?, ?> entry : map.entrySet())
		{
			Object t1 = entry.getKey();
			System.out.print(t1);
			System.out.print("\t");
		}
		System.out.println();
		for (Entry<?, ?> entry : map.entrySet())
		{
			Object t2 = entry.getValue();
			System.out.print(t2);
			System.out.print("\t");
		}
	}

	public static void main(String args[])
	{
		HashMap<String, String> person = new HashMap<String, String>();
		person.put("name", "wxf");
		person.put("age", "30");
		mapTraverse(person);
	}

}
