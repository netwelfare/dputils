package com.dputils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	private ServerSocket serverSocket;
	private int port = 8000;

	public Server() throws IOException
	{
		serverSocket = new ServerSocket(port);
		System.out.println("·þÎñÆ÷Æô¶¯>>>>>");
	}

	public void service()
	{
		//while (true)
		{
			Socket socket = null;
			try
			{
				socket = serverSocket.accept();

				BufferedReader br = NetHelper.getReader(socket);
				PrintWriter pw = NetHelper.getWriter(socket);
				String msg = null;
				while ((msg = br.readLine()) != null)
				{
					System.out.println(msg);
					//pw.println("echo: " + msg);
					if (msg.equals("bye"))
					{
						break;
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (socket != null)
				{
					try
					{
						socket.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		new Server().service();

	}

}
