package vierGewinnt.common;

import java.util.Vector;
import static vierGewinnt.common.Message.*;

public class MessageParser
{

	public static Message parse(String pMessage)
	{
		Message message = new Message();
		
		// Type
		message.type = getMessageType(pMessage.substring(0, pMessage.indexOf(SEP)));
		if (message.type == MessageType.ERROR)
			System.out.println("ERROR: Message type could not be extracted from: " + pMessage);
		pMessage = pMessage.substring(pMessage.indexOf(SEP) + 1);

		// Command
		message.command = getCommand(pMessage.substring(0, pMessage.indexOf(SEP)));
		if (message.command == MessageCommand.ERROR)
			System.out.println("ERROR: Message command could not be extracted from: " + pMessage);
		pMessage = pMessage.substring(pMessage.indexOf(SEP) + 1);

		// Parameters
		message.arguments = getArguments(pMessage);

		return message;
	}

	private static MessageType getMessageType(String pMessage)
	{
		if (pMessage.equals(GAME))
		{
			return MessageType.GAME;
		} else if (pMessage.equals(LOBBY))
		{
			return MessageType.LOBBY;
		} else if (pMessage.equals(CHAT))
		{
			return MessageType.CHAT;
		}
		return MessageType.ERROR;
	}

	private static MessageCommand getCommand(String pCommand)
	{
		if (pCommand.equals(INSERT))
		{
			return MessageCommand.INSERT;
		} 
		else if (pCommand.equals(INSERTSTATUS))
		{
			return MessageCommand.INSERTSTATUS;
		} 
		else if (pCommand.equals(GAMESTART))
		{
			return MessageCommand.GAMESTART;
		} 
		else if (pCommand.equals(GAMEEND))
		{
			return MessageCommand.GAMEEND;
		} 
		else if (pCommand.equals(LOG))
		{
			return MessageCommand.LOG;
		} 
		else if(pCommand.equals(EXPLOSION))
		{
			return MessageCommand.EXPLOSION;
		} 
		else if (pCommand.equals(USERTABLE))
		{
			return MessageCommand.USERTABLE;
		} 
		else if (pCommand.equals(INVITE))
		{
			return MessageCommand.INVITE;
		} 
		else if (pCommand.equals(INVITATIONANSWER))
		{
			return MessageCommand.INVITATIONANSWER;
		} 
		else if(pCommand.equals(WHISPER))
		{
			return MessageCommand.WHISPER;
		} 
		else if(pCommand.equals(ALL))
		{
			return MessageCommand.ALL;
		}
		return MessageCommand.ERROR;
	}

	private static Vector<String> getArguments(String pMessage)
	{
		Vector<String> result = new Vector<String>();
		
		for (int counter = 0; pMessage.contains(SEP); counter++)
		{
			result.add(pMessage.substring(0, pMessage.indexOf(SEP)));
			pMessage = pMessage.substring(pMessage.indexOf(SEP) + 1);
		}
		result.add(pMessage);
		return result;
	}

}
