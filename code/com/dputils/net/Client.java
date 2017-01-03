package com.dputils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
	private String host = "localhost";
	private int port = 8000;
	private Socket socket;

	public Client() throws UnknownHostException, IOException
	{
		socket = new Socket(host, port);
		System.out.println("¿Í»§¶ËÆô¶¯>>>>>");
	}

	public void talk()
	{
		//while (true)
		{
			try
			{
				PrintWriter pw = NetHelper.getWriter(socket);
				BufferedReader br = NetHelper.getReader(socket);
				BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
				String msg = null;
				while ((msg = localReader.readLine()) != null)
				{
					pw.println(msg);
					//System.out.println(br.readLine());
					if (msg.equals("bye"))
					{
						break;
					}
					msg = null;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
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

	public static void main(String[] args) throws UnknownHostException, IOException
	{
		new Client().talk();

	}

}
