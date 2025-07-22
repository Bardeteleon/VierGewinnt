package core.common;

import static core.common.Message.ALL;
import static core.common.Message.CHAT;
import static core.common.Message.EXPLOSION;
import static core.common.Message.GAME;
import static core.common.Message.GAMEEND;
import static core.common.Message.GAMESTART;
import static core.common.Message.INSERT;
import static core.common.Message.INSERTSTATUS;
import static core.common.Message.INVITATIONANSWER;
import static core.common.Message.INVITE;
import static core.common.Message.LOBBY;
import static core.common.Message.LOG;
import static core.common.Message.SEP;
import static core.common.Message.USERTABLE;
import static core.common.Message.WHISPER;

import java.util.List;

public class MessageGenerator 
{
	
	public static final String GAMEEND_QUITTING = " Player Quit"; // spaces are important because then this specifier cannot accidently correspond to a nick name
	public static final String GAMEEND_DRAW = " Game ends with a draw";
	
	public static final String CHAT_SERVER = " Server ";
	
	/*
	 * CHAT
	 */
	public static String sendChatMessage(String message, String senderNick)
	{
		return CHAT + SEP + ALL + SEP + message + SEP + senderNick;
	}
	
	public static String sendWhisperMessage(String message, String senderNick, List<String> receiverNicks)
	{
		return CHAT + SEP + WHISPER + SEP + message + SEP + senderNick + SEP + convertToArgList(receiverNicks);
	}
	
	/*
	 * LOBBY
	 */
	public static String clientSendNickNameIntroduction(String nick)
	{
		return LOBBY + SEP + USERTABLE + SEP + nick;
	}
	
	public static String sendInvitation(String nick, int rows, int columns, int turnTime, boolean expChipsZahlenFuerSieg, boolean explosionZahltAlsZug, int anzahlExpChips)
	{
		return LOBBY + SEP + INVITE + SEP + nick + SEP + rows + SEP + columns + SEP + turnTime + SEP + expChipsZahlenFuerSieg + SEP + explosionZahltAlsZug + SEP + anzahlExpChips;
	}
	
	public static String clientSendInvitationAnswer(String nick, boolean acceptInvitation)
	{
		return LOBBY + SEP + INVITATIONANSWER + SEP + nick + SEP + acceptInvitation;
	}
	
	public static String serverSendUserTable(List<String> userList)
	{
		return LOBBY + SEP + USERTABLE + SEP + convertToArgList(userList);
	}
	
	/*
	 * GAME
	 */
	public static String clientSendInsert(int xCoord, Chip chip)
	{
		return GAME + SEP + INSERT + SEP + xCoord + SEP + chip;
	}
	
	public static String serverSendInsert(Player player, int xCoord, int pYCoord, Chip chip)
	{
		return GAME + SEP + INSERT + SEP + player + SEP + xCoord + SEP + pYCoord + SEP + chip;
	}
	
	public static String serverSendInsertStatus(boolean insert)
	{
		return GAME + SEP + INSERTSTATUS + SEP + insert;
	}
	
	public static String serverSendGameStart(String teammate, int spalten, int zeilen, int turnTime, Player player, int anzahlExpChips)
	{
		return GAME + SEP + GAMESTART + SEP + teammate + SEP + zeilen + SEP + spalten + SEP + turnTime + SEP + player + SEP + anzahlExpChips;
	}
	
	public static String sendGameEnd(String winner)
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
	
	private static String convertToArgList(List<String> list)
	{
		String result = "";
		
		for(String s : list)
			result  += s + SEP;
		
		if (!result.equals(""))
			result = result.substring(0, result.length()-1);
		
		return result;
	}
	
	
}
