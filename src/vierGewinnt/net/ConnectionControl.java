package vierGewinnt.net;

public interface ConnectionControl
{
	public void messageReceived(String _absenderIP, int _absenderPort, String _message);
	public void removeConnectionByConnection(String _IP, int _empfaengerPort);
}
