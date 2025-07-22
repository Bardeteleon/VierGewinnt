package net;

import java.io.IOException;
import java.net.Socket;


public abstract class Client implements ConnectionControl
{
	
	public Connection myConnection;
	
	/**
	 * Stellt eine Verbindung zu dem angegebenen Server her.
	 * 
	 * @param _IP
	 * @param _port
	 */
	public void newConnection(String _IP, int _port) //Stellt eine Verbindung zu dem angegebenen Server her
	{
		try
		{
			Socket _mySocket = new Socket(_IP, _port);
			
			if (myConnection != null)
				close();
			
			myConnection = new Connection(this, _mySocket);
			connectionAdded(_IP);
		}
		catch (IOException e)
		{
			System.err.println("Fehler beim Verbindungsversuch");
		}
	}
	
	/**
	 * Trennt die Verbindung
	 */
	public void close()
	{
		if (myConnection != null)
		{
			if (myConnection.myInputListener != null)
			{
				myConnection.myInputListener.cutConnection();
			}
			else
			{
				myConnection.cutConnection();
			}
		}
	}
	
	/**
	 * Sendet die angegebene Nachricht an den angegebenen Empfaenger.
	 * @param _message
	 */
	public void sendMessage(String _message)
	{
		if (myConnection != null && myConnection.getConnected())
		{
			System.out.println("Send: "+_message);
			myConnection.sendMessage(_message);
		}
	}
	
	/**
	 * Wird aufgerufen, wenn eine Verbindung hergestellt wurde.
	 * @param _IP
	 */
	public abstract void connectionAdded(String _IP);
	
	/**
	 * Wird aufgerufen, wenn die Verbindung getrennt wurde.
	 */
	public abstract void connectionRemoved();
	
	/**
	 * Wird aufgerufen, wenn eine Message empfangen wurde.
	 */
	public abstract void messageReceived(String _absaenderIP, int _absenderPort, String _message);

	
	public void removeConnectionByConnection(String useless, int notNecessary) //NICHT von extern aufrufen!!! Wird ausschliesslich von der Connection verwendet 
	{
		myConnection = null;
		connectionRemoved();
	}
}
