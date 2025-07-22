package net;

import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

/*
 * METHODEN ZUR VERWENDUNG:
 * - activateConnector()
 * - deactivateConnector()
 * - removeConnection(String)
 * - sendMessage(String, String)
 * 
 * METHODEN ZUM ÜBERSCHREIBEN:
 * - connectionAdded(String)
 * - connectionRemoved(String)
 * - messageReceived(String, String)
 */

public abstract class Server implements ConnectionControl
{
	Vector<Connection> myConnectionCollection;
	Connector myConnector;
	int port;

	public Server(int _port)
	{
		myConnectionCollection = new Vector<Connection>();
		port = _port;
		myConnector = new Connector(this);
		activateConnector();
	}

	public Server(int _port, boolean _activateConnector)
	{
		myConnectionCollection = new Vector<Connection>();
		port = _port;
		myConnector = new Connector(this);
		if (_activateConnector)
		{
			activateConnector();
		}
	}

	/**
	 * Aktiviert die Verbindungsannahme
	 */
	public void activateConnector()
	{
		System.out.println("Erwarte Verbindungen");
		myConnector.start();
	}

	/**
	 * Deaktiviert die Verbindungsannahme
	 */
	public void deactivateConnector()
	{
		System.out.println("Verbindungsannahme deaktiviert");
		myConnector.deaktivateConnector();
	}

	// Wird vom Connector angesteuert
	public void addNewConnection(Socket _mySocket)
	{
		String IP = _mySocket.getInetAddress().toString();
		IP = IP.substring(1, IP.length());

		myConnectionCollection.add(new Connection(this, _mySocket));
		connectionAdded(IP, _mySocket.getPort());
	}

	/**
	 * Trennt die Verbindung mit der angegebenen IP.
	 * 
	 * @param _IP
	 */
	public void removeConnection(String _IP, int _clientPort)
	{
		for (int i = 0; i < myConnectionCollection.size(); i++)
		{
			if (myConnectionCollection.get(i).getIP().equals(_IP) && myConnectionCollection.get(i).getPort() == _clientPort)
			{
				Connection myConnection = myConnectionCollection.get(i);
				if (myConnection.myInputListener != null)
				{
					myConnection.myInputListener.cutConnection();
				} else
				{
					myConnection.cutConnection();
				}
			}
		}
	}

	// NICHT von extern aufrufen!!! Wird ausschließlich von der Connection
	// verwendet
	public void removeConnectionByConnection(String _IP, int _clientPort)
	{
		for (int i = 0; i < myConnectionCollection.size(); i++)
		{
			if (myConnectionCollection.get(i).getIP().equals(_IP) && myConnectionCollection.get(i).getPort() == _clientPort)
			{
				myConnectionCollection.remove(i);
				connectionRemoved(_IP, _clientPort);
			}
		}
	}

	/**
	 * Sendet die _message an _empfaengerIP.
	 * 
	 * @param _empfaengerIP
	 * @param _message
	 */
	public void sendMessage(String _empfaengerIP, int _empfaengerPort, String _message)
	{
		for (int i = 0; i < myConnectionCollection.size(); i++)
		{
			if (myConnectionCollection.get(i).getIP().equals(_empfaengerIP) && myConnectionCollection.get(i).getPort() == _empfaengerPort)
			{
				System.out.println("Send (to " + _empfaengerIP + ":" + _empfaengerPort + "): " + _message);
				myConnectionCollection.get(i).sendMessage(_message);
				return;
			}
		}
		System.out.println("Emfänger nicht gefunden");
	}

	public void sendToAll(String _message)
	{
		for (int i = 0; i < myConnectionCollection.size(); i++)
		{
			sendMessage(myConnectionCollection.get(i).getIP(), myConnectionCollection.get(i).getPort(), _message);
		}
	}

	public void close()
	{
		deactivateConnector();
		for (int i = 0; i < myConnectionCollection.size(); i++)
		{
			Connection myConnection = myConnectionCollection.get(i);
			if (myConnection.myInputListener != null)
			{
				myConnection.myInputListener.cutConnection();
			} else
			{
				myConnection.cutConnection();
			}
		}
	}

	/**
	 * Wird aufgerufen wenn eine Verbindung hergestellt wurde.
	 * 
	 * @param _IP
	 */
	public abstract void connectionAdded(String _IP, int _empfaengerPort);

	/**
	 * Wird aufgerufe, wenn eine Verbindung getrennt wurde.
	 * 
	 * @param _IP
	 */
	public abstract void connectionRemoved(String _IP, int _empfaengerPort);

	/**
	 * Wird aufgerufen, wenn eine Nachricht erhalten wurde.
	 */
	public abstract void messageReceived(String _absenderIP, int _empfaengerPort, String _message);

}
