package core.client;

import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.Client;
import useful.GUI;
import core.common.Chat;
import core.common.ChatHandler;
import core.common.Chip;
import core.common.Message;
import core.common.MessageGenerator;
import core.common.MessageParser;
import core.common.Player;

public class ClientVierGewinnt extends Client
{
	private MessageParser parser = new MessageParser();
	private GUIVierGewinnt gui;
	protected Player myPlayer = Player.NONE;
	protected String teammate = null;
	protected String nick = null;
	protected ChatHandler myChatHandler;
	protected TurnTimer turnTimer;
	
	protected final String BROADCAST = "Chat name used for all users online";

	public ClientVierGewinnt(String pIP, int pPort, String pNick, GUIVierGewinnt pGui)
	{
		newConnection(pIP, pPort);
		try
		{
			Thread.sleep(1000);//TODO Problem eventl eleganter l�sen?
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		sendMessage(MessageGenerator.clientSendNickNameIntroduction(pNick));
		nick = pNick;
		gui = pGui;
		myChatHandler = new ChatHandler();
		myChatHandler.addNewChat(BROADCAST);
		myChatHandler.getChat(BROADCAST).addParticipant(MessageGenerator.CHAT_SERVER);
	}

	public void playRequest(int column, Chip chip)
	{
		sendMessage(MessageGenerator.clientSendInsert(column, chip));
		turnTimer.stop();
	}

	public void gameRequest(String teammate, int rows, int columns, int turnTime, boolean expChipsZaehlenFuerSieg, boolean expChipZaehltAlsZug, int bombs)
	{
		sendMessage(MessageGenerator.sendInvitation(teammate, rows, columns, turnTime, expChipsZaehlenFuerSieg, expChipZaehltAlsZug, bombs));
	}

	public void explosionRequest(int splate, int zeile)
	{
		sendMessage(MessageGenerator.explosion(splate, zeile));
	}

	public void sendChatMessage(String message, String nick)
	{
		sendMessage(MessageGenerator.sendChatMessage(message, nick));
	}

	public void sendWhisperMessage(String mess, String senderNick, List<String> receiverNicks)
	{
		sendMessage(MessageGenerator.sendWhisperMessage(mess, senderNick, receiverNicks));
	}
	
	@Override
	public void connectionRemoved()
	{
		gui.getGUILobby().getGUIConnection().doGUIDisconnect();
	}
	
	@Override
	public void messageReceived(String absenderIP, int absenderPort, String message)
	{
		final Message myData = parser.parse(message);
		/*
		 * WIRD VOM INPUTLISTENER THREAD AUFGERUFEN; DER KEINE REFERENZ AUF
		 * OBJEKTE DES EDT HAT
		 */
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				messageReaction(myData);
			}
		});
	}

	private void messageReaction(Message myData)
	{
		switch (myData.type)
		{
			case GAME :
				reactionGame(myData);
				break;
			case LOBBY :
				reactionLobby(myData);
				break;
			case CHAT :
				reactionChat(myData);
				break;
			case ERROR :
				reactionError(myData);
		}
	}

	private void reactionGame(Message myData)
	{
		switch (myData.command)
		{
			case INSERT :
				Chip chip = Chip.valueOf(myData.arguments.get(3));
				gui.setChipAt(Player.valueOf(myData.arguments.get(0)), 
											   chip, 
											   Integer.parseInt(myData.arguments.get(1)), 
											   Integer.parseInt(myData.arguments.get(2)));
				if(chip == Chip.EXPLOSIVE)
					gui.getStatusBar().setBombInfo(gui.getPlayingFieldModel().getNumberOfBombs());
				
				turnTimer.stop();
				break;
				
			case INSERTSTATUS :

				if (Boolean.parseBoolean(myData.arguments.get(0)))
				{
					gui.setChooseChip(true, myPlayer);
					gui.getPlayingField().requestFocus();
					gui.getStatusBar().setYourTurn();
					turnTimer.start();
				} else
				{
					gui.setChooseChip(false, myPlayer);
					if(teammate != null)
						gui.getStatusBar().setOpponentsTurn(teammate);
					turnTimer.start();
				}
				break;
				
			case GAMESTART :
				teammate = myData.arguments.get(0);
				myPlayer = Player.valueOf(myData.arguments.get(4));
				turnTimer = new TurnTimer(Integer.parseInt(myData.arguments.get(3)), gui);
				gui.getPlayingFieldModel().stopAnimationHandler();
				gui.setPlayingFieldModel(new GameTableModel(Integer.parseInt(myData.arguments.get(2)), 
															Integer.parseInt(myData.arguments.get(1)), 
															Integer.parseInt(myData.arguments.get(5)), 
															myPlayer));
				gui.getStatusBar().setPlayers(nick, myPlayer, teammate);
				if(gui.getPlayingFieldModel().getNumberOfBombs() > 0)
					gui.getStatusBar().setBombInfo(gui.getPlayingFieldModel().getNumberOfBombs());
				gui.resetChat();
				gui.getGUILobby().setInGame();
				gui.getGUILobby().setVisible(false);
				gui.getGUILobby().getGUIConnection().setVisible(false);
				gui.getGUILobby().getGUIGameConfig().setVisible(false);
				break;
				
			case GAMEEND :
				if (myData.arguments.get(0).equals(MessageGenerator.GAMEEND_DRAW))
				{
					JOptionPane.showMessageDialog(gui, "Das Spiel endet unentschieden!", "Spielende", JOptionPane.INFORMATION_MESSAGE);
				} else if(myData.arguments.get(0).contentEquals(MessageGenerator.GAMEEND_QUITTING))
				{
					JOptionPane.showMessageDialog(gui, "Dein Mitspieler hat aufgegeben.", "Spielende", JOptionPane.INFORMATION_MESSAGE);
				}else
				{
					if (myData.arguments.get(0).equals(nick))
						gui.doWinnerAnimation();
					else
						gui.doLooserAnimation();
				}
				turnTimer.stop();
				gui.resetAfterGame();
				break;
				
			case LOG :
				// TODO reroute remaining log to whipser chat as soon as Lobbz/Game Chat are merged
				break;

			case EXPLOSION :
				gui.doSmallExplosion(Integer.parseInt(myData.arguments.get(0)), Integer.parseInt(myData.arguments.get(1)));
		}
	}

	private void reactionLobby(Message myData)
	{
		switch(myData.command)
		{
			case INVITE:
				sendMessage(MessageGenerator.clientSendInvitationAnswer(myData.arguments.get(0), 
																		gui.askForGame(
																				myData.arguments.get(0), 
																				myData.arguments.get(1), 
																				myData.arguments.get(2), 
																				myData.arguments.get(3),
																				myData.arguments.get(4),
																				myData.arguments.get(5),
																				myData.arguments.get(6))
																		));
				break;
		
			case USERTABLE:
				myChatHandler.getChat(BROADCAST).removeAllParticipants();
				myChatHandler.getChat(BROADCAST).addParticipants(myData.arguments);
				myChatHandler.getChat(BROADCAST).addParticipant(MessageGenerator.CHAT_SERVER);
				gui.getGUILobby().setUserTable(myData.arguments, nick);
				break;
		}
	}

	private void reactionChat(Message myData)
	{
		Chat chat;
		switch(myData.command)
		{
			case WHISPER:
				List<String> participants = myData.arguments.subList(1, myData.arguments.size());
				if(!myChatHandler.isChat(participants))
					myChatHandler.addNewChat(participants);
				
				chat = myChatHandler.getChat(participants);
				chat.addMessage(myData.arguments.get(1), myData.arguments.get(0));
				
				if(chat.isVisible())
					gui.getGUILobby().appendLobbyChat(chat.getFormattedMessageLatest(), GUI.GREENSTYLE);
				else
					;// notify for new whisper message
				
				if (myData.arguments.size() == 3 && ((myData.arguments.get(1).equals(nick) && myData.arguments.get(2).equals(teammate)) || (myData.arguments.get(2).equals(nick) && myData.arguments.get(1).equals(teammate))))
				{
					gui.appendGameChat(myChatHandler.getChat(participants).getFormattedMessageLatest(), GUI.GREENSTYLE);
				} else
				{
					//notify for new whisper message
				}
				break;
				
			case ALL:
				chat = myChatHandler.getChat(BROADCAST);
				chat.addMessage(myData.arguments.get(1), myData.arguments.get(0));
				
				if(chat.isVisible())
					gui.getGUILobby().appendLobbyChat(myChatHandler.getChat(BROADCAST).getFormattedMessageLatest(), GUI.BLACKSTYLE);
				else
					; // notify somehow for new broadcast message
				break;
		}
	}

	private void reactionError(Message myData)
	{
		if (myData.arguments.size() > 0)
			gui.showErrorMessage(myData.arguments.get(0));
		else
			gui.showErrorMessage(null);
	}

	@Override
	public void connectionAdded(String _IP)
	{
		
	}
}
