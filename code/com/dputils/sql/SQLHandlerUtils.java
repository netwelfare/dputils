package com.dputils.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class SQLHandlerUtils
{

	public SQLHandlerUtils()
	{

	}

	public static void _readXml(InputStream input, String nodeName)
	{
		try
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			SQLHandler handler = new SQLHandler(nodeName);
			parser.parse(input, handler);
			input.close();
			return;
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			e.printStackTrace();
		}

		return;
	}

	public static void main(String[] args)
	{
		try
		{
			FileInputStream input = new FileInputStream(new File("D:\\git\\dputils\\code\\com\\dputils\\sql\\sql.xml"));
			_readXml(input, "");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
