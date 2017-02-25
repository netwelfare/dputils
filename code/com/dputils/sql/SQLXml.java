package com.dputils.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class SQLXml
{

	public static List<HashMap<String, String>> _readXml(InputStream input, String nodeName)
	{
		try
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			SaxHandler handler = new SaxHandler(nodeName);
			parser.parse(input, handler);
			input.close();
			return handler.getList();
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args)
	{
		try
		{
			FileInputStream input = new FileInputStream(
					new File("D:\\git\\dputils\\code\\com\\dputils\\sql\\itcast.xml"));
			List<HashMap<String, String>> list = _readXml(input, "person");
			for (HashMap<String, String> p : list)
			{
				System.out.println(p.toString());
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
