package vierGewinnt.common;



public class MessageParser
{

	// messageTypes:
	public static final int ERROR = -1;
	public static final int GAME = 0;
	public static final int LOBBY = 1;
	public static final int CHAT = 2;

	// commands:
	//Game
	public static final int INSERT = 0;
	public static final int INSERTSTATUS = 1;
	public static final int GAMESTART = 7;
	public static final int LOG = 8;
	public static final int EXPLOSION = 9;
	public static final int GAMEEND = 10;
	
	//Lobby
	public static final int USERTABLE = 2;
	public static final int INVITE = 3;
	public static final int INVITATIONANSWER = 4;

	//Chat
	public static final int WHISPER = 5;
	

	private static Message errorOutput(String errMessage)
	{
		Message messageData = new Message();
		messageData.type = ERROR;
		messageData.arguments.add(errMessage);
		return messageData;
	}

	private static int getMessageType(String pMessage)
	{
		if (pMessage.equals("GAME"))
		{
			return GAME;
		} else if (pMessage.equals("LOBBY"))
		{
			return LOBBY;
		} else if (pMessage.equals("CHAT"))
		{
			return CHAT;
		}
		return ERROR;
	}

	private static int getCommand(String pCommand)
	{
		if (pCommand.equals("INSERT"))
		{
			return INSERT;
		} else if (pCommand.equals("INSERTSTATUS"))
		{
			return INSERTSTATUS;
		} else if (pCommand.equals("GAMESTART"))
		{
			return GAMESTART;
		} else if (pCommand.equals("GAMEEND"))
		{
			return GAMEEND;
		} else if (pCommand.equals("LOG"))
		{
			return LOG;
		} else if (pCommand.equals("USERTABLE"))
		{
			return USERTABLE;
		} else if (pCommand.equals("INVITE"))
		{
			return INVITE;
		} else if (pCommand.equals("INVITATIONANSWER"))
		{
			return INVITATIONANSWER;
		} else if(pCommand.equals("WHISPER"))
		{
			return WHISPER;
		} else if(pCommand.equals("EXPLOSION"))
		{
			return EXPLOSION;
		}
		return ERROR;
	}

	private static Message getArguments(Message pMessageData, String pMessage)
	{
		for (int counter = 0; pMessage.contains("�"); counter++)
		{
			pMessageData.arguments.add(pMessage.substring(0, pMessage.indexOf("�")));
			pMessage = pMessage.substring(pMessage.indexOf("�") + 1);
		}
		pMessageData.arguments.add(pMessage);
		return pMessageData;
	}

	public static Message parse(String pMessage)
	{
		if(pMessage == null)
		{
			return errorOutput("pMessage == null");
		}
		Message message = new Message();
		
		// server Message with IP at the front
		if (!pMessage.substring(0, 1).equals("["))
		{
			message.ip = pMessage.substring(0, pMessage.indexOf("["));
		}
		
		message.type = getMessageType(pMessage.substring(pMessage.indexOf("[")+1, pMessage.indexOf("]")));
		if (message.type == ERROR)
		{
			return errorOutput("messageType Error 2");
		}

		// command:
		pMessage = pMessage.substring(pMessage.indexOf("]") + 1);
		if (message.type != CHAT || pMessage.contains(MessageGenerator.WHISPER))
		{
			if (!pMessage.contains("�"))
			{
				return errorOutput("commend Error");
			}
			message.command = getCommand(pMessage.substring(0, pMessage.indexOf("�")));
		} else
		{
			message.arguments.add(pMessage);
			return message;
		}

		// parameters:
		pMessage = pMessage.substring(pMessage.indexOf("�") + 1);
		if (pMessage.equals(""))
		{
			return errorOutput("Missing parameters");
		}
		message = getArguments(message, pMessage);

		return message;
	}

}