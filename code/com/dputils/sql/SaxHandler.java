package com.dputils.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler
{
	private HashMap<String, String> map = null;
	private List<HashMap<String, String>> list = null;
	/** 
	 * ���ڽ�����Ԫ�صı�ǩ 
	 */
	private String currentTag = null;
	/** 
	 * ���ڽ�����Ԫ�ص�ֵ 
	 */
	private String currentValue = null;
	private String nodeName = null;

	public List<HashMap<String, String>> getList()
	{
		return list;
	}

	public SaxHandler(String nodeName)
	{
		this.nodeName = nodeName;
	}

	@Override
	public void startDocument() throws SAXException
	{
		//������һ����ʼ��ǩ��ʱ�򣬻ᴥ���������  
		list = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		//�������ĵ��Ŀ�ͷ��ʱ�򣬵����������  
		if (name.equals(nodeName))
		{
			map = new HashMap<String, String>();
		}
		if (attributes != null && map != null)
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				map.put(attributes.getQName(i), attributes.getValue(i));
			}
		}
		currentTag = name;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		//�����������������XML�ļ��ж���������  
		if (currentTag != null && map != null)
		{
			currentValue = new String(ch, start, length);
			if (currentValue != null && !currentValue.trim().equals("") && !currentValue.trim().equals("\n"))
			{
				map.put(currentTag, currentValue);
			}
		}
		currentTag = null;
		currentValue = null;
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		//������������ǩ��ʱ�򣬵����������  
		if (name.equals(nodeName))
		{
			list.add(map);
			map = null;
		}
		super.endElement(uri, localName, name);
	}

}