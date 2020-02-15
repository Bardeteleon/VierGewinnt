package vierGewinnt.server;
import java.util.Vector;

import vierGewinnt.MessageGenerator;

public class UserControl
{
	
	ServerVierGewinnt myServer;
	Vector<User> myUsers = new Vector<User>();
	
	public UserControl(ServerVierGewinnt _myServer)
	{
		myServer = _myServer;
	}
	
	public User getUserByNick(String _nick)
	{
		for (int i = 0 ; i < myUsers.size() ; i++)
		{
			if (myUsers.get(i).getNick().equals(_nick))
			{
				return myUsers.get(i);
			}
		}
		return null;
	}
	
	public User getUser(User u)
	{
		for (int i = 0 ; i < myUsers.size() ; i++)
		{
			if (myUsers.get(i).equals(u))
			{
				return myUsers.get(i);
			}
		}
		return null;
	}
	
	public User getUser(String _IP, int _port)
	{
		User u = new User(_IP, _port);
		return getUser(u);
	}
	
	public boolean nickAvailable(String _nick)
	{
		User search = getUserByNick(_nick);
		if (search != null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void setNick(User u, String _nick)
	{
		if (!_nick.equals(""))
		{
			if (nickAvailable(_nick))
			{
				User aktUser = getUser(u);
				if (aktUser != null)
				{
					aktUser.setNick(_nick);
					String nachricht = MessageGenerator.serverSendUserTable(getUserString());
					myServer.sendToAll(nachricht);
				}
			}
			else
			{
				//Nick schon vorhanden
			}
		}
		else
		{
			//Keinen Nickname eingegeben
			
		}
	}
	
	public void newUser(String _IP, int _port)
	{
		myUsers.add(new User(_IP, _port));
	}
	
	public void deleteUser(User u)
	{
		for (int i = 0 ; i < myUsers.size() ; i++)
		{
			if (myUsers.get(i).equals(u))
			{
				myUsers.remove(i);
				String nachricht = MessageGenerator.serverSendUserTable(getUserString());
				myServer.sendToAll(nachricht);
				return;
			}
		}
	}
	
	public void deleteAllInvitationsBy(User u)
	{
		for (int i = 0 ; i < myUsers.size() ; i++)
		{
			myUsers.get(i).deleteSingleInvitation(u);
			//WICHTIG: Nachricht an den entsprechenden User, dass die Invitation nicht länger Besand hat!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}
	}
	
	public Vector<String> getUserString()//Gibt aus wer in der Lobby ist 
	{
		Vector<String> output = new Vector<String>();
		for (int i = 0 ; i < myUsers.size() ; i++)
		{
			if (myUsers.get(i).getStatus() == User.IN_LOBBY)
			{
				output.add(myUsers.get(i).getNick());
			}
		}
		return output;
	}
	
}
