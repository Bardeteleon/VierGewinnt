package vierGewinnt.common;

import java.util.Vector;
import static vierGewinnt.common.Message.*;

public class MessageGenerator 
{
	
	/*
	 * CHAT
	 */
	public static String sendChatMessage(String message)
	{
		return CHAT + SEP + ALL + SEP + message;
	}
	
	public static String sendWhisperMessage(String message, Vector<String> nicks)
	{
		return CHAT + SEP + WHISPER + SEP + message + SEP + convertToArgList(nicks);
	}
	
	/*
	 * LOBBY
	 */
	public static String clientSendNickNameIntroduction(String nick)
	{
		return LOBBY + SEP + USERTABLE + SEP + nick;
	}
	
	public static String sendInvitation(String nick, int rows, int columns, boolean expChipsZahlenFuerSieg, boolean explosionZahltAlsZug, int anzahlExpChips)
	{
		return LOBBY + SEP + INVITE + SEP + nick + SEP + rows + SEP + columns + SEP + expChipsZahlenFuerSieg + SEP + explosionZahltAlsZug + SEP + anzahlExpChips;
	}
	
	public static String clientSendInvitationAnswer(String nick, boolean acceptInvitation)
	{
		return LOBBY + SEP + INVITATIONANSWER + SEP + nick + SEP + acceptInvitation;
	}
	
	public static String serverSendUserTable(Vector<String> userList)
	{
		return LOBBY + SEP + USERTABLE + SEP + convertToArgList(userList);
	}
	
	/*
	 * GAME
	 */
	public static String clientSendInsert(int xCoord, Chip chip)
	{
		return GAME + SEP + INSERT + SEP + xCoord + SEP + chip.getValue();
	}
	
	public static String serverSendInsert(int farbe, int xCoord, int pYCoord, Chip chip)
	{
		return GAME + SEP + INSERT + SEP + farbe + SEP + xCoord + SEP + pYCoord + SEP + chip.getValue();
	}
	
	public static String serverSendInsertStatus(boolean insert)
	{
		return GAME + SEP + INSERTSTATUS + SEP + insert;
	}
	
	public static String serverSendGameStart(String teammate, int spalten, int zeilen, int color, int anzahlExpChips)
	{
		return GAME + SEP + GAMESTART + SEP + teammate + SEP + zeilen + SEP + spalten + SEP + color + SEP + anzahlExpChips;
	}
	
	public static String serverSendGameEnd(String winner)
	{
		return GAME + SEP + GAMEEND + SEP + winner;
	}
	
	public static String serverSendLogMessage(String message)
	{
		return GAME + SEP + LOG + SEP + message;
	}
	
	public static String explosion(int spalte, int zeile)
	{		
		return GAME + SEP + EXPLOSION + SEP + spalte + SEP + zeile;		
	}
	
	private static String convertToArgList(Vector<String> list)
	{
		String result = "";
		
		for(String s : list)
			result  += s + SEP;
		
		if (!result.equals(""))
			result = result.substring(0, result.length()-1);
		
		return result;
	}
	
	
}
