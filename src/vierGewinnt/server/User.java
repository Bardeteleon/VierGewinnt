package vierGewinnt.server;
import java.util.Vector;


public class User
{
	private String IP;
	private int port;
	private String nick;
	
	private int status; //1: Kein Nick | 2: InLobby | 3: InGame
	
	Vector<Invitation> invitations = new Vector<Invitation>();
	
	public static final int KEIN_NICK = 1;
	public static final int IN_LOBBY = 2;
	public static final int IN_GAME = 3;
	
	public User(String _IP, int _port)
	{
		status = KEIN_NICK;
		IP = _IP;
		port = _port;
	}
	
	public void setNick(String _nick)
	{
		if (!_nick.equals(""))
		{
			nick = _nick;
			status = IN_LOBBY;
			//WICHTIG: Übergeordnete Klassen müssen den User in die Lobby versetzen!!!
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
	
	public String getNick()
	{
		return nick;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int _status)
	{
		status = _status;
	}
	
	public void newInvitation(User u, int _spalten, int _zeilen, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		if (!invitedBy(u))
		{
			//System.out.println("Einladung eingetragen bei "+IP+" ("+_IP+")");
			invitations.add(new Invitation(u, _spalten, _zeilen, _expChipsZahlenFuerSieg, _explosionZahltAlsZug, _anzahlExpChips));
		}
	}
	
	public boolean invitedBy(User u)
	{
		for (int i = 0 ; i < invitations.size() ; i++)
		{
			if (invitations.get(i).getInvitedBy().equals(u))
			{
				return true;
			}
		}
		return false;
	}
	
	public Invitation getInvitationBy(User u)
	{
		for (int i = 0 ; i < invitations.size() ; i++)
		{
			if (invitations.get(i).getInvitedBy().equals(u))
			{
				return invitations.get(i);
			}
		}
		return null;
	}
	
	public void deleteSingleInvitation(User u)
	{
		for (int i = 0 ; i < 0 ; i++)
		{
			if (invitations.get(i).getInvitedBy().equals(u))
			{
				invitations.remove(i);
				return;
			}
		}
	}
	
	public void removeInvitations()
	{
		invitations = new Vector<Invitation>();
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof User)
		{
			User u = (User) o;
			if(u.getIP().equals(IP) && u.getPort() == port)
				return true;
		}
		return false;
	}
	
	
	public String toString()
	{
		return "< " + nick + ":" + IP + ":" + port + ">";
	}
}
