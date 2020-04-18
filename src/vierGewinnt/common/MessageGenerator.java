package vierGewinnt.common;



import java.util.Vector;

import vierGewinnt.server.User;

public class MessageGenerator 
{
	//types:
	public static final String GAME = "[GAME]";
	public static final String LOBBY = "[LOBBY]";
	public static final String CHAT = "[CHAT]";
	
	//commands:
	//Game
	public static final String INSERT = "INSERT§";
	public static final String INSERTSTATUS = "INSERTSTATUS§";
	public static final String GAMESTART = "GAMESTART§";
	public static final String GAMEEND = "GAMEEND§";
	public static final String LOG = "LOG§";
	public static final String EXPLOSION = "EXPLOSION§";
	//Lobby
	public static final String USERTABLE = "USERTABLE§";
	public static final String INVITE = "INVITE§";
	public static final String INVITATIONANSWER = "INVITATIONANSWER§";
	//Chat
	public static final String WHISPER = "WHISPER§";
	
	public static String sendChatMessage(String pMessage)
	{
		return CHAT + pMessage;
	}
	
	public static String sendWhisperMessage(String message, Vector<String> nicks)
	{
		String names = "";
		for(String s : nicks)
		{
			names  += s + "§";
		}
		if (!names.equals(""))
		{
			names = names.substring(0, names.length()-1);
		}
		return CHAT + WHISPER + message + "§" + names;
	}
	
	public static String clientSendInsert(int pXCoord, Chip chip)
	{
		return GAME + INSERT + pXCoord + "§" + chip.getValue();
	}
	
	public static String clientSendNickNameIntroduction(String pNickName)
	{
		return LOBBY + USERTABLE + pNickName;
	}
	
	public static String sendInvitation(String pNickName, int rows, int columns, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		return LOBBY + INVITE + pNickName + "§" + rows + "§" + columns + "§" + _expChipsZahlenFuerSieg + "§" + _explosionZahltAlsZug + "§" + _anzahlExpChips;
	}
	
	public static String clientSendInvitationAnswer(String nick, boolean pAcceptInvitation)
	{
		if(pAcceptInvitation)
		{
			return LOBBY + INVITATIONANSWER + nick + "§" + "TRUE";
		}
		else
		{
			return LOBBY + INVITATIONANSWER + nick + "§" + "FALSE";
		}
	}
	
	public static String serverSendInsert(User _teammate, int farbe, int pXCoord, int pYCoord, Chip chip)
	{
		return _teammate.getIP() + ":" + _teammate.getPort() + GAME + INSERT + farbe + "§" + pXCoord + "§" + pYCoord + "§" + chip.getValue();
	}
	
	public static String serverSendUserTable(Vector<String> pUserList)
	{
		String names = "";
		for(String s : pUserList)
		{
			names  += s + "§";
		}
		if (!names.equals(""))
		{
			names = names.substring(0, names.length()-1);
		}
		return LOBBY + USERTABLE + names;
	}
	
	public static String serverSendInsertStatus(User _teammate, boolean b)
	{
		return _teammate.getIP() + ":" + _teammate.getPort() + GAME + INSERTSTATUS + b;
	}
	
	public static String serverSendGameStart(User _teammate, int _spalten, int _zeilen, int _color, int _anzahlExpChips)
	{
		return _teammate.getIP() + ":" + _teammate.getPort() + GAME + GAMESTART + _teammate.getNick() + "§" + _zeilen + "§" + _spalten + "§" + _color + "§" + _anzahlExpChips;
	}
	
	public static String serverSendGameEnd(User _teammate, String _winner)
	{
		return _teammate.getIP() + ":" + _teammate.getPort() + GAME + GAMEEND + _winner;
	}
	
	public static String serverSendLogMessage(User _teammate, String _message)
	{
		return _teammate.getIP() + ":" + _teammate.getPort() + GAME + LOG + _message;
	}
	
	public static String explosion(User _teammate, int _spalte, int _zeile)
	{
		String mate;
		if(_teammate == null)
			 mate = "null:null";
		else
			mate = _teammate.getIP() + ":" + _teammate.getPort();
		
		return mate + GAME + EXPLOSION + _spalte + "§" + _zeile;		
	}
	
}
