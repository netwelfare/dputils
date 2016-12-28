package com.dputils.codeanalysis;

import java.io.File;
import java.util.ArrayList;

public class FileAnalysis
{

	public FileAnalysis()
	{

	}

	public static void fileList(File file, ArrayList<String> fileList)
	{

		if (file.isFile())
		{
			fileList.add(file.getAbsolutePath());
		}
		else if (file.isDirectory())
		{
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++)
			{
				fileList(f[i], fileList);
			}
		}
	}

}
