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

	// Überschriebene Klasse
	public void connectionAdded(String _IP, int _port)
	{
		System.out.println("(" + _IP + ":" + _port + "): Neue Verbindung hergestellt");
		myUserControl.newUser(_IP, _port);
	}

	// Überschriebene Klasse
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
		System.out.println("(" + _IP + ":" + _port + "): Verbindung unterbrochen");
	}

	public void messageReceived(String _absenderIP, int _absenderPort, String _message)
	{
		System.out.println("    <---EMPFANGEN VON (" + _absenderIP + "): " + _message);
		Message dp;
		dp = MessageParser.parse(_message);
		User absender = new User(_absenderIP, _absenderPort);
		messageReaction(dp, absender);
	}
	
	public void sendMessage(User _client, String _message)
	{
		super.sendMessage(_client.getIP(), _client.getPort(), _message);
	}

	public void sendToAll(String _output)
	{
		// System.out.println("Sende an alle: " + _output);
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
			// Game nicht gefunden
			fehler("spielBeendet: Game nicht gefunden");
		}
		gc = null;
		myGameHandler.deleteGame(einTeilnehmerDesSpiels);
	}

	private void fehler(String fehler)
	{
		// HIER KANN GGF. DER FEHLERCODE WEITERGEGEBEN UND DORT INTERPRETIERT
		// WERDEN (Verwendung nicht unbedingt erforderlich)
		System.out.println(fehler);
	}

	private void messageReaction(Message pMessageData, User pAbsender)
	{
		switch (pMessageData.type)
		{
		case GAME:
			if (pMessageData.command == MessageCommand.INSERT)
			{
				try
				{
					int spalte;
					spalte = Integer.parseInt(pMessageData.arguments.get(0));

					Chip chipType = Chip.valueOf(Integer.parseInt(pMessageData.arguments.get(1)));
					if (chipType == Chip.NORMAL || chipType == Chip.EXPLOSIVE)
					{
						myGameHandler.einwurf(spalte, pAbsender, chipType);
					} else
					{
						// Unbekannter ChipType
						fehler("messageReaction.Insert: Unbekannter ChipType");
					}

				} catch (Exception e)
				{
					// Einlesefehler
					fehler("messageReaction.Insert: Einlesefehler");
				}
			} else if (pMessageData.command == MessageCommand.EXPLOSION)
			{
				try
				{
					GameControl game = myGameHandler.getGame(pAbsender);

					if (game != null)
					{
						int spalte = Integer.parseInt(pMessageData.arguments.get(0));
						// int zeile =
						// Integer.parseInt(pMessageData.arguments.get(1));
						int zeile = (game.zeilen - 1) - Integer.parseInt(pMessageData.arguments.get(1)); // Dieser
																											// Teil,
																											// da
																											// der
																											// Client
																											// die
																											// übergebene
																											// Zahl
																											// nicht
																											// vorher
																											// konvertiert!!

						System.out.println("Explosion   spalte: " + spalte + " | zeile: " + zeile);

						game.sprengen(spalte, zeile, pAbsender);
					} else
					{
						// Spiel nicht gefunden
						fehler("messageReaction.Explosion: Spiel nicht gefunden (Teilnehmer : " + pAbsender + ")");
					}
				} catch (Exception e)
				{
					// Einlesefehler
					fehler("messageReaction.Explosion: Einlesefehler");
				}

			}
			break;

		case LOBBY:
			switch (pMessageData.command)
			{

			case INVITE:
				User sender = myUserControl.getUser(pAbsender);
				if (sender != null)
				{
					if (sender.getStatus() == User.IN_LOBBY)
					{
						User eingeladen = myUserControl.getUserByNick(pMessageData.arguments.get(0));
						if (eingeladen != null)
						{
							if (!eingeladen.equals(sender))
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
										fehler("Eingabeproblem");
										return;
									}

									if (spalten >= 4 && zeilen >= 4)
									{
										if (anzahlExpChips < 0)
										{
											anzahlExpChips = 0;
										}
										System.out.println("Einladung gesendet: (Einladender: " + sender + " | Eingeladener: " + eingeladen + " | Spielfeld: " + spalten + "x" + zeilen
												+ " | expChipsZahlenFuerSieg: " + expChipsZahlenFuerSieg + " | explosionZahltAlsZug: " + explosionZahltAlsZug + " | anzahlExpChips: " + anzahlExpChips
												+ ")");
										eingeladen.newInvitation(sender, spalten, zeilen, expChipsZahlenFuerSieg, explosionZahltAlsZug, anzahlExpChips);
										String nachricht = MessageGenerator.sendInvitation(sender.getNick(), zeilen, spalten, expChipsZahlenFuerSieg, explosionZahltAlsZug, anzahlExpChips);
										sendMessage(eingeladen, nachricht);
									} else
									{
										fehler("messageReaction.Invite: Zeilen und/oder Spaltenangeaben nicht im gültigen Bereich");
									}

								} else
								{
									// Eingeladener User befindet sich nicht in
									// der Lobby
									sendMessage(sender, MessageGenerator.serverSendLogMessage(eingeladen.getNick() + " spielt bereits!"));
									fehler("messageReaction.Invite: Eingeladen (" + eingeladen.getIP() + ") befindet sich nicht in der Lobby");
								}
							} else
							{
								// Der Sender hat sich selber eingeladen
								fehler("meaageReaction.Invite: Einladender (" + sender.getIP() + ") hat sich selber eingeladen");
							}
						} else
						{
							// Eingeladener User nicht gefunden
							fehler("messageReaction.Invite: Eingeladen (" + pMessageData.arguments.get(0) + ") nicht gefunden");
						}
					} else
					{
						// User befindet sich nicht in der Lobby
						fehler("messageReaction.Invite: Sender (" + pAbsender + ") ist nicht in der Lobby");
					}
				} else
				{
					// User nicht gefunden
					fehler("messageReaction.Invite: Sender (" + pAbsender + ") nicht gefunden");
				}

				// Lobby: Einladung hinzufügen
				// sendMessage(/*IP des einzuladenden Clients*/,
				// myMessGenerator.serverSendInvitation(/*Nick des Senders*/));
				break;

			case INVITATIONANSWER: // WICHTIG: ARG(0): IP DES EINLADENDEN
											// | ARG(1): TRUE ODER FALSE
				User eingeladener = myUserControl.getUser(pAbsender);
				if (eingeladener != null)
				{
					User einladender = myUserControl.getUserByNick(pMessageData.arguments.get(0));
					if (einladender != null)
					{
						Invitation inv = eingeladener.getInvitationBy(einladender);
						// if (eingeladener.invitedBy(einladender.getIP()))
						if (inv != null)
						{
							if (pMessageData.arguments.get(1).equals("true"))
							{
								System.out.println("(" + eingeladener.getIP() + ") nimmt die Einladung von (" + einladender.getIP() + ") an");
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
								System.out.println("(" + eingeladener.getIP() + ") lehnt die Einladung von (" + einladender.getIP() + ") ab");
							}
						} else
						{
							// User wurde von dem angegebenen User nicht
							// eingeladen
							fehler("messageReaction: Eingeladener (" + eingeladener.getIP() + ") wurde nicht von (" + einladender.getIP() + ") eingeladen");
						}
					} else
					{
						// Den einladenden User gibt es nicht mehr
						fehler("messageReaction: Einladender (" + pMessageData.arguments.get(0) + ") nicht gefunden");
					}
				} else
				{
					// User nicht gefunden
					fehler("messageReaction: Eingeladener (" + pAbsender + ") nicht gefunden");
				}

				// Lobby: Einladung löschen wenn "FALSE"
				// Wenn Einladung eingenommen
				break;

			case USERTABLE:
				User myUser = myUserControl.getUser(pAbsender);
				if (myUser != null)
				{
					if (myUser.getStatus() == User.KEIN_NICK)
					{
						System.out.println("Setze Nick für (" + pAbsender + ") auf '" + pMessageData.arguments.get(0) + "'");
						myUser.setNick(pMessageData.arguments.get(0));
						myUser.setStatus(User.IN_LOBBY);
					} else
					{
						// User hat bereits einen Nick
						fehler("messageReaction.Usertable: User (" + pAbsender + ") hat bereits einen Nick");
					}
				} else
				{
					// User nicht gefunden
					fehler("messageReaction.Usertable: User (" + pAbsender + ") nicht gefunden");
				}
				sendToAll(MessageGenerator.serverSendUserTable(myUserControl.getUserString()));
				break;
			}
			break;

		case CHAT:
			if (pMessageData.command == MessageCommand.WHISPER)
			{
				String message = pMessageData.arguments.get(0);

				User sender = myUserControl.getUser(pAbsender);
				if (sender != null)
				{
					if (sender.getStatus() != User.KEIN_NICK)
					{
						Vector<String> emfaenger = new Vector<String>();
						emfaenger.add(sender.getNick()); // Erstes Argument ist
															// der Sender-Nick
						for (int i = 1; i < pMessageData.arguments.size(); i++)
						{
							emfaenger.add(pMessageData.arguments.get(i)); // Die
																			// weiteren
																			// Argumente
																			// sind
																			// die
																			// Emfaenger
						}
						sendMessage(sender, MessageGenerator.sendWhisperMessage(message, emfaenger)); // Message
																												// an
																												// Sender
						for (int i = 1; i < pMessageData.arguments.size(); i++)
						{
							User myUser = myUserControl.getUserByNick(pMessageData.arguments.get(i));
							if (myUser != null)
							{
								if (!myUser.equals(sender))
								{
									sendMessage(myUser, MessageGenerator.sendWhisperMessage(message, emfaenger));
								} else
								{
									// Der Sender hat eine Nachricht an sich
									// selbst verschickt
									fehler("messageReaction.Chat: Sender (" + sender.getIP() + ") hat sich selbst als Empfänger angegeben");
								}
							} else
							{
								// User nicht gefunden
								fehler("messageReaction.Chat: Empfaenger (" + pMessageData.arguments.get(i) + ") nicht gefunden");
							}
						}
					} else
					{
						// Sender hat noch keinen Username
						fehler("messageReaction.Chat: Sender (" + sender.getIP() + ") hat noch keine Usernamen");
					}
				} else
				{
					// Sender nicht gefunden
					fehler("messageReaction.Chat: Sender (" + pAbsender + ") nicht gefunden");
				}
			} else
			{
				User tempUser = myUserControl.getUser(pAbsender);
				if (tempUser != null)
				{
					if (tempUser.getStatus() != User.KEIN_NICK)
					{
						sendToAll(MessageGenerator.sendChatMessage(tempUser.getNick() + ": " + pMessageData.arguments.get(0)));
					} else
					{
						// User hat noch keinen Nick
						fehler("messageReaction.Chat: Sender der Global Message (" + pAbsender + ") hat noch keinen Nick");
					}
				} else
				{
					// User nicht gefunden
					fehler("messageReaction.Chat: Sender der Global Message (" + pAbsender + ") nicht gefunden");
				}
			}

			break;

		case ERROR:
			fehler("messageReaction: (" + pAbsender + "): Unbekannter Befehl");
		}
	}

}
