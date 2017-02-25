package com.dputils.sql;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dputils.file.BigDataTool;
import com.dputils.file.handlers.DefaultLineHandler2;

public class SQLHandler extends DefaultHandler
{
	private String currentTag = null;
	private String currentValue = null;
	private String nodeName = null;
	private List<String> list = null;
	private HashMap<String, String> map;

	public SQLHandler(String nodeName)
	{
		this.nodeName = nodeName;
		BufferedReader br = BigDataTool.getBufferedReader("D:\\git\\dputils\\code\\com\\dputils\\sql\\sql.config");
		map = new HashMap<>();
		BigDataTool.getMap(br, map, new DefaultLineHandler2());
	}

	@Override
	public void startDocument() throws SAXException
	{
		list = new ArrayList<String>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		//System.out.println("开始解析");
		System.out.println(name);
		System.out.println(map.get(name));
		currentTag = name;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (currentTag != null)
		{
			currentValue = new String(ch, start, length);
			//System.out.println("解析的值");
			System.out.println(currentValue);
		}
		currentTag = null;
		currentValue = null;
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		//System.out.println("解析结束");
		//System.out.println(name);
		super.endElement(uri, localName, name);
	}
}
