package net;

import java.io.BufferedReader;
import java.io.IOException;


public class InputListener extends Thread
{
	Connection myConnection;
	BufferedReader myBuffReader;
	boolean connected = false;
	
	public InputListener(Connection _myConnection, BufferedReader _myBuffReader)
	{
		setName("InputListener");
		myConnection = _myConnection;
		myBuffReader = _myBuffReader;
	}
	
	public void run()
	{
		connected = true;
		String message = "";
		try
		{
			while (connected)
			{
				message = myBuffReader.readLine();
				System.out.println("InputListener read a line (from " + myConnection.IP + ":" + myConnection.port + "): " + message);
				if (message != null)
				{
					if (connected)
					{
						myConnection.messageReceived(message);
						message = "";
					}
				}
				else
				{
					connected = false;
				}
			}
			myConnection.cutConnection();
		}
		catch (IOException e)
		{
			System.out.println("InputListener exception (from " + myConnection.IP + ":" + myConnection.port + "): ");
			e.printStackTrace();
			cutConnection();
		}
	}

	public void cutConnection()
	{
		connected = false;
		myConnection.cutConnection();
	}
}
