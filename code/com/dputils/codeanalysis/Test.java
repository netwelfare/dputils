package com.dputils.codeanalysis;

import java.io.File;
import java.util.ArrayList;

public class Test
{

	public static void main(String[] args)
	{
		String fileName = "D:/git/saiku-master";
		File file = new File(fileName);
		ArrayList<String> fileList = new ArrayList<String>();
		FileAnalysis.fileList(file, fileList);
		for (String f : fileList)
		{
			if (f.endsWith(".java"))
			{
				if (!f.contains("test"))
				{
					if (f.contains("export"))
					{
						System.out.println(f);
					}
				}
			}
		}

	}

}
