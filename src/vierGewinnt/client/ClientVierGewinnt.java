package vierGewinnt.client;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.Client;
import useful.GUI;
import vierGewinnt.common.Chip;
import vierGewinnt.common.Message;
import vierGewinnt.common.MessageGenerator;
import vierGewinnt.common.MessageParser;

public class ClientVierGewinnt extends Client
{
	private MessageParser parser = new MessageParser();
	private GUIVierGewinnt gui;
	protected int color = 0;
	protected String teammate = null;
	protected String nick = null;

	public ClientVierGewinnt(String pIP, int pPort, String pNick, GUIVierGewinnt pGui)
	{
		newConnection(pIP, pPort);
		try
		{
			Thread.sleep(1000);//TODO Problem eventl eleganter lösen?
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		sendMessage(MessageGenerator.clientSendNickNameIntroduction(pNick));
		nick = pNick;
		gui = pGui;
	}

	public void playRequest(int column, Chip chip)
	{
		sendMessage(MessageGenerator.clientSendInsert(column, chip));
	}

	public void gameRequest(String teammate, int rows, int columns, boolean expChipsZaehlenFuerSieg, boolean expChipZaehltAlsZug, int bombs)
	{
		sendMessage(MessageGenerator.sendInvitation(teammate, rows, columns, expChipsZaehlenFuerSieg, expChipZaehltAlsZug, bombs));
	}

	public void explosionRequest(int splate, int zeile)
	{
		sendMessage(MessageGenerator.explosion(splate, zeile));
	}

	public void sendChatMessage(String message)
	{
		sendMessage(MessageGenerator.sendChatMessage(message));
	}

	public void sendWhisperMessage(String mess, Vector<String> nicks)
	{
		sendMessage(MessageGenerator.sendWhisperMessage(mess, nicks));
	}
	
	@Override
	public void connectionRemoved()
	{
		gui.guiLobby.guiConnection.doGUIDisconnect();
	}
	
	@Override
	public void messageReceived(String absenderIP, int absenderPort, String message)
	{
		System.out.println("Received: " + message);
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
				Chip chip = Chip.valueOf(Integer.parseInt(myData.arguments.get(3)));
				gui.setChipAt(Integer.parseInt(myData.arguments.get(0)), 
											   chip, 
											   Integer.parseInt(myData.arguments.get(1)), 
											   Integer.parseInt(myData.arguments.get(2)));
				if(chip == Chip.EXPLOSIVE)
					gui.getStatusBar().setBombInfo(gui.getPlayingFieldModel().getNumberOfBombs());
				break;
				
			case INSERTSTATUS :

				if (Boolean.parseBoolean(myData.arguments.get(0)))
				{
					gui.setChooseChip(true, color);
					gui.getPlayingField().requestFocus();
					gui.getStatusBar().setYourTurn();
				} else
				{
					gui.setChooseChip(false, color);
					if(teammate != null)
						gui.getStatusBar().setOpponentsTurn(teammate);
				}
				break;
				
			case GAMESTART :
				teammate = myData.arguments.get(0);
				color = Integer.parseInt(myData.arguments.get(3));
				gui.getPlayingFieldModel().stopAnimationHandler();
				gui.setPlayingFieldModel(new GameTableModel(Integer.parseInt(myData.arguments.get(2)), 
															Integer.parseInt(myData.arguments.get(1)), 
															Integer.parseInt(myData.arguments.get(4)), 
															color));
				gui.getStatusBar().setPlayers(nick, color, teammate);
				if(gui.getPlayingFieldModel().getNumberOfBombs() > 0)
					gui.getStatusBar().setBombInfo(gui.getPlayingFieldModel().getNumberOfBombs());
				gui.resetChat();
				gui.guiLobby.setVisible(false);
				gui.guiLobby.guiConnection.setVisible(false);
				gui.guiLobby.guiGameConfig.setVisible(false);
				break;
				
			case GAMEEND :
				teammate = null;
				if (myData.arguments.get(0).equals(" "))
				{
					JOptionPane.showMessageDialog(gui, "Das Spiel endet unentschieden!", "Spielende", JOptionPane.INFORMATION_MESSAGE);
				} else
				{
					if (myData.arguments.get(0).equals(nick))
						gui.doWinnerAnimation();
					else
						gui.doLooserAnimation();
				}
				gui.getStatusBar().clear();
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
																		gui.askForGame(myData.arguments.get(0), 
																		myData.arguments.get(1), 
																		myData.arguments.get(2), 
																		myData.arguments.get(3),
																		myData.arguments.get(4),
																		myData.arguments.get(5))));
				break;
		
			case USERTABLE:
				gui.setUserTable(myData.arguments, nick);
				break;
		}
	}

	private void reactionChat(Message myData)
	{
		switch(myData.command)
		{
			case WHISPER:
				if (myData.arguments.size() == 3 && ((myData.arguments.get(1).equals(nick) && myData.arguments.get(2).equals(teammate)) || (myData.arguments.get(2).equals(nick) && myData.arguments.get(1).equals(teammate))))
				{
					gui.insertGameChatMessage(myData.arguments.get(1) + ": " + myData.arguments.get(0), GUI.BLACKSTYLE);
				} else
				{
					String empfaenger = "";
					for (int i = 2; i < myData.arguments.size(); i++)
						empfaenger += myData.arguments.get(i) + ",";
					empfaenger = empfaenger.substring(0, empfaenger.length() - 1);
					gui.insertChatMessage(myData.arguments.get(1) + "@" + empfaenger + ": " + myData.arguments.get(0), GUI.GREENSTYLE);
				}
				break;
				
			case ALL:
				gui.insertChatMessage(myData.arguments.get(0), GUI.BLACKSTYLE);
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
