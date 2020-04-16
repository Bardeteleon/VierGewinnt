package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Connector extends Thread
{
	Server myConnectionControl;
	ServerSocket myServerSocket;
	boolean allowConnection = false;

	public Connector(Server _myConnectionControl)
	{
		setName("Connector");
		myConnectionControl = _myConnectionControl;

	}

	public void run()
	{
		allowConnection = true;

		try
		{
			myServerSocket = new ServerSocket(myConnectionControl.port);
			while (allowConnection)
			{
				Socket mySocket = myServerSocket.accept();
				if (allowConnection)
				{
					myConnectionControl.addNewConnection(mySocket);
				}
			}
		} catch (SocketException e)
		{
			System.out.println("Connector beendet!");
		} catch (IOException e)
		{
			System.err.println("Fehler beim Erwarten einer Verbindung");
		}
	}

	public void deaktivateConnector()
	{
		allowConnection = false;
		try
		{
			myServerSocket.close(); // TODO close() added
			myServerSocket = null;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
