package vierGewinnt.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class Connection
{
	ConnectionControl myConnectionControl;
	Socket mySocket;
	InputListener myInputListener;
	PrintStream myPrtStream;
	String IP;
	int port;
	boolean connected = false;
	
	public Connection(ConnectionControl _myConnectionControl, Socket _mySocket)
	{
		myConnectionControl = _myConnectionControl;
		mySocket = _mySocket;
		IP = mySocket.getInetAddress().getCanonicalHostName();
		port = mySocket.getPort();
		newConnection();
	}
	
	private void newConnection()
	{
		try
		{
			myInputListener = new InputListener(this,new BufferedReader(new InputStreamReader(mySocket.getInputStream())));
			myPrtStream = new PrintStream(mySocket.getOutputStream());
			String tempIP = mySocket.getInetAddress().toString();
			IP = tempIP.substring(1, tempIP.length());
			port = mySocket.getPort();
			myInputListener.start();
			connected = true;
			//System.out.println(IP + ":" + port + " hat eine Verbindung hergestellt");
		}
		catch (IOException e)
		{
			System.err.println("Fehler beim Aufbau der Verbindung!");
		}
	}
	
	public void messageReceived(String _message)
	{
		myConnectionControl.messageReceived(IP, port, _message);
	}
	
	public void sendMessage(String _message)
	{
		if (connected && _message != null)
		{
			myPrtStream.println(_message);
			//System.out.println("Server: " + _message);
		}
	}
	
	public void cutConnection()
	{
		if (connected)
		{
			//System.out.println("Verbindung zu " + IP + ":" + port + " wurde getrennt");
			try
			{
				connected = false;
				myPrtStream.close();
				myPrtStream = null;
				mySocket.close();
				myConnectionControl.removeConnectionByConnection(IP, port);
			}
			catch (IOException e)
			{
				System.err.println("Problem beim Trennen der Verbindung");
			}
		}
	}
	
	public String getIP()
	{
		return IP;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public boolean getConnected()
	{
		return connected;
	}
	
	
	public boolean equals(Object obj)
	{
		if(obj instanceof Connection)
		{
			Connection c  = (Connection) obj;
			if(c.getIP().equals(getIP()) && c.getPort() == getPort())
				return true;
		}
		return false;
	}
	
}
