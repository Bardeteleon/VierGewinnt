package vierGewinnt.server;

import java.util.Vector;

import net.Server;
import vierGewinnt.common.Chip;
import vierGewinnt.common.Message;
import vierGewinnt.common.MessageCommand;
import vierGewinnt.common.MessageGenerator;
import vierGewinnt.common.MessageParser;
import vierGewinnt.common.MessageType;

public class ServerVierGewinnt extends Server
{
	UserControl myUserControl = new UserControl(this);
	GameHandler myGameHandler = new GameHandler(this);

	public ServerVierGewinnt(int _port)
	{
		super(_port);
		System.out.println("Der Server wurde gestartet");
	}

	protected void finalize()
	{
		System.out.println("Der Server wurde beendet");
	}

	public void connectionAdded(String _IP, int _port)
	{
		System.out.println("Neue Verbindung hergestellt: " + _IP + ":" + _port);
		myUserControl.newUser(_IP, _port);
	}

	public void connectionRemoved(String _IP, int _port)
	{
		GameControl gc = myGameHandler.getGame(new User(_IP, _port));
		if (gc != null)
		{
			if (gc.getUser1().equals(new User(_IP, _port)))
			{
				sendMessage(gc.getUser2(), MessageGenerator.serverSendLogMessage("Die Verbindung vom Partner wurde unterbrochen"));
			} else if (gc.getUser2().equals(new User(_IP, _port)))
			{
				sendMessage(gc.getUser1(), MessageGenerator.serverSendLogMessage("Die Verbindung vom Partner wurde unterbrochen"));
			}
			spielBeendet(new User(_IP, _port));
		}

		myUserControl.deleteUser(new User(_IP, _port));
		System.out.println("Verbindung unterbrochen: " + _IP + ":" + _port);
	}
	
	public void sendMessage(User _client, String _message)
	{
		super.sendMessage(_client.getIP(), _client.getPort(), _message);
	}

	public void sendToAll(String _output)
	{
		for (int i = 0; i < myUserControl.myUsers.size(); i++)
		{
			if (myUserControl.myUsers.get(i).getStatus() != User.KEIN_NICK)
			{
				sendMessage(myUserControl.myUsers.get(i), _output);
			}
		}
	}

	public void spielBeendet(User einTeilnehmerDesSpiels)
	{
		GameControl gc = myGameHandler.getGame(einTeilnehmerDesSpiels);
		if (gc != null)
		{
			System.out.println("Spiel beendet (" + gc.getUser1() + " & " + gc.getUser2() + ")");

			User user1 = gc.getUser1();
			if (user1 != null)
			{
				user1.setStatus(User.IN_LOBBY);
			}
			User user2 = gc.getUser2();
			if (user2 != null)
			{
				user2.setStatus(User.IN_LOBBY);
			}
		} else
		{
			System.out.println("spielBeendet: Game nicht gefunden");
		}
		gc = null;
		myGameHandler.deleteGame(einTeilnehmerDesSpiels);
	}

	public void messageReceived(String _absenderIP, int _absenderPort, String _message)
	{
		User absender = myUserControl.getUser(_absenderIP, _absenderPort);
		
		if(absender == null)
			System.err.println("ERROR: Unbekannter Sender: " + _absenderIP + ":" + _absenderPort);
		else
			messageReaction(MessageParser.parse(_message), absender);
	}
	
	private void messageReaction(Message pMessageData, User pAbsender)
	{
		switch (pMessageData.type)
		{
			case GAME:
				messageReactionGame(pMessageData, pAbsender);
				break;
	
			case LOBBY:
				messageReactionLobby(pMessageData, pAbsender);
				break;
	
			case CHAT:
				messageReactionChat(pMessageData, pAbsender);	
				break;
	
			case ERROR:
				System.out.println("messageReaction: (" + pAbsender + "): Unbekannter Befehl");
		}
	}
	
	private void messageReactionGame(Message pMessageData, User pAbsender)
	{
		switch (pMessageData.command)
		{
			case INSERT:
				try
				{
					int spalte = Integer.parseInt(pMessageData.arguments.get(0));

					Chip chipType = Chip.valueOf(pMessageData.arguments.get(1));
					if (chipType == Chip.NORMAL || chipType == Chip.EXPLOSIVE)
					{
						myGameHandler.einwurf(spalte, pAbsender, chipType);
					} else
					{
						System.out.println("messageReaction.Insert: Unbekannter ChipType");
					}

				} catch (Exception e)
				{
					System.out.println("messageReaction.Insert: EinleseSystem.out.println");
				}
				break;
				
			case EXPLOSION:
				try
				{
					GameControl game = myGameHandler.getGame(pAbsender);

					if (game != null)
					{
						int spalte = Integer.parseInt(pMessageData.arguments.get(0));
						int zeile = (game.zeilen - 1) - Integer.parseInt(pMessageData.arguments.get(1)); 

						System.out.println("Explosion in Spalte: " + spalte + " | Zeile: " + zeile);

						game.sprengen(spalte, zeile, pAbsender);
					} else
					{
						System.out.println("messageReaction.Explosion: Spiel nicht gefunden (Teilnehmer : " + pAbsender + ")");
					}
				} catch (Exception e)
				{
					System.out.println("messageReaction.Explosion: EinleseSystem.out.println");
				}
				break;
				
			default:
				System.out.println("messageReaction.Game: Unbekanntes Kommando: " + pMessageData.command);
				break;
		}
	}
	
	private void messageReactionLobby(Message pMessageData, User pAbsender)
	{
		switch (pMessageData.command)
		{
			case INVITE:
				if (pAbsender.getStatus() == User.IN_LOBBY)
				{
					User eingeladen = myUserControl.getUserByNick(pMessageData.arguments.get(0));
					if (eingeladen != null)
					{
						if (!eingeladen.equals(pAbsender))
						{
							if (eingeladen.getStatus() == User.IN_LOBBY)
							{
								int spalten;
								int zeilen;
								boolean expChipsZahlenFuerSieg;
								boolean explosionZahltAlsZug;
								int anzahlExpChips;

								try
								{
									spalten = Integer.parseInt(pMessageData.arguments.get(2));
									zeilen = Integer.parseInt(pMessageData.arguments.get(1));
									expChipsZahlenFuerSieg = Boolean.parseBoolean(pMessageData.arguments.get(3));
									explosionZahltAlsZug = Boolean.parseBoolean(pMessageData.arguments.get(4));
									anzahlExpChips = Integer.parseInt(pMessageData.arguments.get(5));
								} catch (Exception e)
								{
									System.out.println("Eingabeproblem");
									return;
								}

								if (spalten >= 4 && zeilen >= 4)
								{
									if (anzahlExpChips < 0)
									{
										anzahlExpChips = 0;
									}
									System.out.println("Einladung gesendet: (Einladender: " + pAbsender + " | Eingeladener: " + eingeladen + " | Spielfeld: " + spalten + "x" + zeilen
											+ " | expChipsZahlenFuerSieg: " + expChipsZahlenFuerSieg + " | explosionZahltAlsZug: " + explosionZahltAlsZug + " | anzahlExpChips: " + anzahlExpChips
											+ ")");
									eingeladen.newInvitation(pAbsender, spalten, zeilen, expChipsZahlenFuerSieg, explosionZahltAlsZug, anzahlExpChips);
									String nachricht = MessageGenerator.sendInvitation(pAbsender.getNick(), zeilen, spalten, expChipsZahlenFuerSieg, explosionZahltAlsZug, anzahlExpChips);
									sendMessage(eingeladen, nachricht);
								} else
								{
									System.out.println("messageReaction.Invite: Zeilen und/oder Spaltenangeaben nicht im gültigen Bereich");
								}

							} else
							{
								sendMessage(pAbsender, MessageGenerator.serverSendLogMessage(eingeladen.getNick() + " spielt bereits!"));
								System.out.println("messageReaction.Invite: Eingeladen (" + eingeladen.getIP() + ") befindet sich nicht in der Lobby");
							}
						} else
						{
							System.out.println("meaageReaction.Invite: Einladender (" + pAbsender.getIP() + ") hat sich selber eingeladen");
						}
					} else
					{
						System.out.println("messageReaction.Invite: Eingeladen (" + pMessageData.arguments.get(0) + ") nicht gefunden");
					}
				} else
				{
					System.out.println("messageReaction.Invite: Sender (" + pAbsender + ") ist nicht in der Lobby");
				}
				break;

			case INVITATIONANSWER:
				User eingeladener = pAbsender;
				if (eingeladener != null)
				{
					User einladender = myUserControl.getUserByNick(pMessageData.arguments.get(0));
					if (einladender != null)
					{
						Invitation inv = eingeladener.getInvitationBy(einladender);
						if (inv != null)
						{
							if (pMessageData.arguments.get(1).equals("true"))
							{
								System.out.println("(" + eingeladener.getIP() + ":" + eingeladener.getPort() + ") nimmt die Einladung von (" + einladender.getIP() + ":" + einladender.getPort() + ") an");
								einladender.removeInvitations();
								eingeladener.removeInvitations();
								myUserControl.deleteAllInvitationsBy(einladender);
								myUserControl.deleteAllInvitationsBy(eingeladener);
								einladender.setStatus(User.IN_GAME);
								eingeladener.setStatus(User.IN_GAME);
								myGameHandler.newGame(einladender, eingeladener, inv.getSpalten(), inv.getZeilen(), inv.getExpChipsZahlenFuerSieg(), inv.getExplosionZahltAlsZug(),
										inv.getAnzahlExpChips());
							} else if (pMessageData.arguments.get(1).equals("false"))
							{
								eingeladener.deleteSingleInvitation(einladender);
								sendMessage(einladender, MessageGenerator.serverSendLogMessage(eingeladener.getNick() + " lehnt die Einladung ab!"));
								System.out.println("(" + eingeladener.getIP() + ":" + eingeladener.getPort() + ") lehnt die Einladung von (" + einladender.getIP() + ":" + einladender.getPort() + ") ab");
							}
						} else
						{
							System.out.println("messageReaction: Eingeladener (" + eingeladener.getIP() + ") wurde nicht von (" + einladender.getIP() + ") eingeladen");
						}
					} else
					{
						System.out.println("messageReaction: Einladender (" + pMessageData.arguments.get(0) + ") nicht gefunden");
					}
				} else
				{
					System.out.println("messageReaction: Eingeladener (" + pAbsender + ") nicht gefunden");
				}
				break;

			case USERTABLE:
				if (pAbsender.getStatus() == User.KEIN_NICK)
				{
					System.out.println("Setze Nick für (" + pAbsender.getIP() + ":" + pAbsender.getPort() + ") auf '" + pMessageData.arguments.get(0) + "'");
					pAbsender.setNick(pMessageData.arguments.get(0));
					pAbsender.setStatus(User.IN_LOBBY);
				} else
				{
					System.out.println("messageReaction.Usertable: User (" + pAbsender + ") hat bereits einen Nick");
				}
				sendToAll(MessageGenerator.serverSendUserTable(myUserControl.getUserString()));
				break;
				
			default:
				System.out.println("messageRection.Lobby: Unbekanntes Kommando: " + pMessageData.command);
				break;
		}
	}
	
	private void messageReactionChat(Message pMessageData, User pAbsender)
	{
		switch (pMessageData.command)
		{
			case WHISPER:
				String message = pMessageData.arguments.get(0);

				if (pAbsender.getStatus() != User.KEIN_NICK)
				{
					Vector<String> emfaenger = new Vector<String>();
					emfaenger.add(pAbsender.getNick()); // Erstes Argument ist der Sender-Nick
					for (int i = 1; i < pMessageData.arguments.size(); i++)
					{
						emfaenger.add(pMessageData.arguments.get(i)); // Die weiteren Argumente sind die Empfaenger
					}
					sendMessage(pAbsender, MessageGenerator.sendWhisperMessage(message, emfaenger));
					
					for (int i = 1; i < pMessageData.arguments.size(); i++)
					{
						User myUser = myUserControl.getUserByNick(pMessageData.arguments.get(i));
						if (myUser != null)
						{
							if (!myUser.equals(pAbsender))
							{
								sendMessage(myUser, MessageGenerator.sendWhisperMessage(message, emfaenger));
							} else
							{
								System.out.println("messageReaction.Chat: Sender (" + pAbsender.getIP() + ") hat sich selbst als Empfänger angegeben");
							}
						} else
						{
							System.out.println("messageReaction.Chat: Empfaenger (" + pMessageData.arguments.get(i) + ") nicht gefunden");
						}
					}
				} else
				{
					System.out.println("messageReaction.Chat: Sender (" + pAbsender.getIP() + ") hat noch keine Usernamen");
				}
				break;
				
			case ALL:
				if (pAbsender.getStatus() != User.KEIN_NICK)
				{
					sendToAll(MessageGenerator.sendChatMessage(pAbsender.getNick() + ": " + pMessageData.arguments.get(0)));
				} else
				{
					System.out.println("messageReaction.Chat: Sender der Global Message (" + pAbsender + ") hat noch keinen Nick");
				}
				break;
				
			default:
				System.out.println("messageReaction.Chat: Unbekanntes Kommando fuer den Chat: " + pMessageData.command);
				break;
		}
	}

}
