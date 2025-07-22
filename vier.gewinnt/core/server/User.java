package core.server;
import java.util.Vector;

import core.common.Player;


public class User
{
	private String IP;
	private int port;
	private String nick;
	
	private Player player;
	
	private int explosiveCount;
	
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
		
		// TODO refactor to seperate class UserVG, so that User stays generic
		player = Player.NONE;
		explosiveCount = -1;
	}
	
	public void setNick(String _nick)
	{
		if (!_nick.equals(""))
		{
			nick = _nick;
			status = IN_LOBBY;
			//WICHTIG: �bergeordnete Klassen m�ssen den User in die Lobby versetzen!!!
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
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public void setExplosiveCount(int count)
	{
		this.explosiveCount = count;
	}
	
	public int getExplosiveCount()
	{
		return this.explosiveCount;
	}
	
	public void removeOneExplosive()
	{
		this.explosiveCount--;
	}
	
	public void newInvitation(User u, int _spalten, int _zeilen, int _turnTime, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		if (!invitedBy(u))
		{
			invitations.add(new Invitation(u, _spalten, _zeilen, _turnTime, _expChipsZahlenFuerSieg, _explosionZahltAlsZug, _anzahlExpChips));
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
