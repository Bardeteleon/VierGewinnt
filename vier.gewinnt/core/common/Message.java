package core.common;

import java.util.Vector;

public class Message
{
	// Types:
	protected static final String GAME = "GAME";
	protected static final String LOBBY = "LOBBY";
	protected static final String CHAT = "CHAT";
	
	// Commands:
	// Game
	protected static final String INSERT = "INSERT";
	protected static final String INSERTSTATUS = "INSERTSTATUS";
	protected static final String GAMESTART = "GAMESTART";
	protected static final String GAMEEND = "GAMEEND";
	protected static final String LOG = "LOG";
	protected static final String EXPLOSION = "EXPLOSION";
	// Lobby
	protected static final String USERTABLE = "USERTABLE";
	protected static final String INVITE = "INVITE";
	protected static final String INVITATIONANSWER = "INVITATIONANSWER";
	// Chat
	protected static final String WHISPER = "WHISPER";
	protected static final String ALL = "ALL";
	
	protected static final String SEP = "§";
	
	public MessageType type;
	public MessageCommand command;
	public Vector<String> arguments;

	public Message()
	{
		arguments = new Vector<String>();
	}
	
	public String toString()
	{
		String s = "";
		s = "Type: " + type + "\nCommand: " + command + "\nARGS:\n";
		for(String arg : arguments)
		{
			s = s + arg + "\n";
		}
		return s;
	}
}
