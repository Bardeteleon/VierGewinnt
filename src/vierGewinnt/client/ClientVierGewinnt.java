package vierGewinnt.client;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import useful.GUI;
import vierGewinnt.DataPackage;
import vierGewinnt.MessageGenerator;
import vierGewinnt.Parser;
import vierGewinnt.net.Client;

public class ClientVierGewinnt extends Client
{
	private Parser parser = new Parser();
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

	public void playRequest(int column, boolean bomb)
	{
		sendMessage(MessageGenerator.clientSendInsert(column, GameTableModel.bombToChipType(bomb)));
	}

	public void gameRequest(String teammate, int rows, int columns, boolean expChipsZaehlenFuerSieg, boolean expChipZaehltAlsZug, int bombs)
	{
		sendMessage(MessageGenerator.sendInvitation(teammate, rows, columns, expChipsZaehlenFuerSieg, expChipZaehltAlsZug, bombs));
	}

	public void explosionRequest(int splate, int zeile)
	{
		sendMessage(MessageGenerator.explosion(null, splate, zeile));
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
		final DataPackage myData = parser.parse(message);
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

	private void messageReaction(DataPackage myData)
	{
		switch (myData.messageType)
		{
			case Parser.GAME :
				reactionGame(myData);
				break;
			case Parser.LOBBY :
				reactionLobby(myData);
				break;
			case Parser.CHAT :
				reactionChat(myData);
				break;
			case Parser.ERROR :
				reactionError(myData);
		}
	}

	private void reactionGame(DataPackage myData)
	{
		switch (myData.command)
		{
			case Parser.INSERT :
				gui.setChipAt(Integer.parseInt(myData.arguments.get(0)), Integer.parseInt(myData.arguments.get(3)), Integer.parseInt(myData.arguments.get(1)), Integer.parseInt(myData.arguments.get(2)), true);
				break;
			case Parser.INSERTSTATUS :

				if (Boolean.parseBoolean(myData.arguments.get(0)))
				{
					gui.setChooseChip(true, color);
					gui.tbPlayingField.requestFocus();
				} else
				{
					gui.setChooseChip(false, color);
				}
				break;
			case Parser.GAMESTART :
				teammate = myData.arguments.get(0);
				color = Integer.parseInt(myData.arguments.get(3));
				gui.playingFieldModel.stopAnimationHandler();
				gui.playingFieldModel = new GameTableModel(Integer.parseInt(myData.arguments.get(2)), Integer.parseInt(myData.arguments.get(1)), Integer.parseInt(myData.arguments.get(4)), color);
				gui.resetLogChat();
				gui.tbPlayingField.setModel(gui.playingFieldModel);
				gui.guiLobby.setVisible(false);
				gui.guiLobby.guiConnection.setVisible(false);
				gui.guiLobby.guiGameConfig.setVisible(false);
				break;
			case Parser.GAMEEND :
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
				break;
			case Parser.LOG :
				gui.insertLogMessage(myData.arguments.get(0));
				break;

			case Parser.EXPLOSION :
				gui.doSmallExplosion(Integer.parseInt(myData.arguments.get(0)), Integer.parseInt(myData.arguments.get(1)));
		}
	}

	private void reactionLobby(DataPackage myData)
	{
		// INVITE
		if (myData.command == Parser.INVITE)
		{
			sendMessage(MessageGenerator.clientSendInvitationAnswer(myData.arguments.get(0), gui.askForGame(myData.arguments.get(0), myData.arguments.get(1), myData.arguments.get(2), myData.arguments.get(3),myData.arguments.get(4),myData.arguments.get(5))));
		}
		// USERTABLE
		if (myData.command == Parser.USERTABLE)
		{
			gui.setUserTable(myData.arguments, nick);
		}
	}

	private void reactionChat(DataPackage myData)
	{
		if (myData.command == Parser.WHISPER)
		{
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
		} else
		{
			gui.insertChatMessage(myData.arguments.get(0), GUI.BLACKSTYLE);
		}
	}

	private void reactionError(DataPackage myData)
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
