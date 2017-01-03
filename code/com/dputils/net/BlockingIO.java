package com.dputils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BlockingIO
{

	public BlockingIO()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String a = br.readLine();
		System.out.println("a: " + a);
		String b = br.readLine();
		System.out.println("b: " + b);
		String c = br.readLine();
		System.out.println("c: " + c);
		String d = br.readLine();
		System.out.println("d: " + d);
	}

}
