package core.server;

import core.common.Chip;
import core.common.MessageGenerator;
import core.common.Player;


public class GameControl
{	
	ServerVierGewinnt myServer;
	
	Feld[][] spielfeld;
	int spalten; //in Feldern
	int zeilen;
	int turnTime;
	int anzahlFelderFuerSieg;
	Player amZug;
	boolean beendet;
	
	boolean ruleExplosiveChipsZaehlenFuerSieg = false;
	boolean ruleExplosionZaehltAlsZug = true;
	
	private User sp1, sp2;
	
	private int turnCount;
	
	public GameControl(ServerVierGewinnt _myServer, int _spalten, int _zeilen, int _turnTime, User _sp1, User _sp2, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		myServer = _myServer;
		//EVTL Abfrage: spalten und zeilen muessen groesser sein, als anzahlFelderFuerSieg!!!
		if (_spalten < anzahlFelderFuerSieg)
		{
			spalten = anzahlFelderFuerSieg;
		}
		else
		{
			spalten = _spalten;
		}
		if (_zeilen < anzahlFelderFuerSieg)
		{
			zeilen = anzahlFelderFuerSieg;
		}
		else
		{
			zeilen = _zeilen;
		}
		spielfeld = new Feld[spalten][zeilen];
		
		for (int i = 0 ; i < spalten ; i++)
		{
			for (int j = 0 ; j < zeilen ; j++)
			{
				spielfeld[i][j] = new Feld();
			}
		}
		
		beendet = true;
		
		turnTime = _turnTime;
		
		turnCount = 0;
		
		anzahlFelderFuerSieg = 4; //VIER Gewinnt eben
		
		sp1 = _sp1;
		sp2 = _sp2;
		
		ruleExplosiveChipsZaehlenFuerSieg = _expChipsZahlenFuerSieg;
		ruleExplosionZaehltAlsZug = _explosionZahltAlsZug;
		
		sp1.setExplosiveCount(_anzahlExpChips);
		sp2.setExplosiveCount(_anzahlExpChips);
		
		neuesSpiel();
	}
	
	private void neuesSpiel()
	{
		System.out.println("Spiel beginnt (" + sp1 + " & " + sp2 + " | Spielfeldgroesse: " + spalten + "x" + zeilen + " | expChipsZahlenFuerSieg: " + ruleExplosiveChipsZaehlenFuerSieg + " | explosionZahltAlsZug: " + ruleExplosionZaehltAlsZug + " | anzahlExpChips: " + sp1.getExplosiveCount() + ")");
		
		anfangsspielerAuslosen();
		
		sp1.setPlayer(Player.RED);
		sp2.setPlayer(Player.YELLOW);

		myServer.sendMessage(getUserAmZug(), MessageGenerator.serverSendGameStart(getUserNichtAmZug().getNick(), spalten, zeilen, turnTime, getAmZug(), getUserAmZug().getExplosiveCount()));
		myServer.sendMessage(getUserNichtAmZug(), MessageGenerator.serverSendGameStart(getUserAmZug().getNick(), spalten, zeilen, turnTime, getNichtAmZug(), getUserNichtAmZug().getExplosiveCount()));

		myServer.sendMessage(getUserAmZug(), MessageGenerator.serverSendInsertStatus(true));
		myServer.sendMessage(getUserNichtAmZug(), MessageGenerator.serverSendInsertStatus(false));
		
		beendet = false;
		info("Neues Spiel gestartet");
		info(getUserAmZug().getNick() + " beginnt");
	}
	
	
	public void sprengen(int _spalte, int _zeile, User user)
	{
		if (!beendet)
		{
			if (user.equals(getUserAmZug()))
			{
				if (_spalte >= 0 && _spalte < spalten)
				{
					if (_zeile >= 0 && _zeile < zeilen)
					{
						if (spielfeld[_spalte][_zeile].getType() == Chip.EXPLOSIVE)
						{
							if (spielfeld[_spalte][_zeile].getSpieler() == user.getPlayer())
							{
								execSprengung(_spalte, _zeile);
							}
							else
							{
								System.out.println("ERROR: Das Feld gehoert nicht zu diesem Spieler");
							}
						}
						else
						{
							System.out.println("ERROR: Angegebener Chip ist kein Explosivfeld");
						}
					}
					else
					{
						System.out.println("ERROR: Angegebene Zeile liegt ausserhalb des gueltigen Bereichs");
					}
				}
				else
				{
					System.out.println("ERROR: Angegebene Spalte liegt ausserhalb des gueltigen Bereichs");
				}
			}
			else
			{
				System.out.println("ERROR: Spieler nicht gefunden oder nicht am Zug");
			}
		}
		else
		{
			System.out.println("ERROR: Das Spiel ist bereits beendet");
		}
	}
	
	private void execSprengung(int _spalte, int _zeile)
	{
		myServer.sendMessage(sp1, MessageGenerator.explosion(_spalte, _zeile));
		myServer.sendMessage(sp2, MessageGenerator.explosion(_spalte, _zeile));
		
		//Entfenen des Sprengchips
		spielfeld[_spalte][_zeile].setSpieler(Player.NONE);
		spielfeld[_spalte][_zeile].setType(Chip.EMPTY);
		//Nachrutschen der anderen Chips
		for (int i = _zeile + 1 ; i < zeilen ; i++)
		{
			if (spielfeld[_spalte][i].getType() != Chip.EMPTY)
			{
				spielfeld[_spalte][i - 1].setType(spielfeld[_spalte][i].getType());
				spielfeld[_spalte][i - 1].setSpieler(spielfeld[_spalte][i].getSpieler());
				spielfeld[_spalte][i].setType(Chip.EMPTY);
				spielfeld[_spalte][i].setSpieler(Player.NONE);
			}
			else
			{
				break;
			}
		}
		
		//Anzeigen beim Spieler aktualisieren
		/*NICHT NOETIG: CLIENT AKTUALISIER SICH SELBST
		for (int i = _zeile ; i < zeilen ; i++)
		{
			myServer.sendMessage(IPs[1], MessageGenerator.serverSendInsert(spielfeld[_spalte][i].getSpieler(), _spalte, i, spielfeld[_spalte][i].getType()));
			myServer.sendMessage(IPs[2], MessageGenerator.serverSendInsert(spielfeld[_spalte][i].getSpieler(), _spalte, i, spielfeld[_spalte][i].getType()));
			if (spielfeld[_spalte][i].getType() == Feld.LEER)
			{
				break;
			}
		}
		*/
		
		//ggf. Spielerwechsel
		if (ruleExplosionZaehltAlsZug)
		{
			spielerwechsel();
		}
		
		//Endgamechecks
		Player sieger = Player.NONE;
		for (int i = 0 ; i < zeilen ; i++)
		{
			if (spielfeld[_spalte][i].getType() == Chip.EMPTY)
			{
				break;
			}
			else
			{
				Player temp = localEndGameCheck(_spalte, i);
				
				if (temp != Player.NONE)
				{
					if (sieger == Player.NONE)
					{
						sieger = temp;
					}
					else
					{
						if (sieger != temp)
						{
							spielEndetUnentschieden();
							return;
						}
					}
				}
			}
		}
		if (sieger != Player.NONE)
		{
			spielerHatGewonnen(sieger);
		}
		
	}
	
	public void neuerEinwurf(int spalte, User user, Chip chipType)
	{		
		if (!beendet)
		{
			if (user.getPlayer() == amZug)
			{
				if (spalte >= 0 && spalte < spalten)
				{
					
					if (spielfeld[spalte][zeilen - 1].getType() == Chip.EMPTY)
					{
						for (int i = 0 ; i < zeilen ; i++)
						{
							if (spielfeld[spalte][i].getType() == Chip.EMPTY)
							{
								if (chipType == Chip.NORMAL)
								{
									spielfeld[spalte][i].setSpieler(user.getPlayer());
									spielfeld[spalte][i].setType(Chip.NORMAL);
									einwurfGetaetigt(spalte, i, user.getPlayer(), chipType);
								}
								else if (chipType == Chip.EXPLOSIVE)
								{
									if (user.getExplosiveCount() > 0)
									{
										user.removeOneExplosive();
										info(getUserAmZug().getNick()+" hat noch " + user.getExplosiveCount() +" Bomben");
										spielfeld[spalte][i].setSpieler(user.getPlayer());
										spielfeld[spalte][i].setType(Chip.EXPLOSIVE);
										einwurfGetaetigt(spalte, i, user.getPlayer(), chipType);
									}
									else
									{
										System.out.println("ERROR: Der Spieler hat keine Explosivchips mehr");
									}
								}
								else
								{
									System.out.println("ERROR: Ungueltiger Chiptype");
								}
								break;
							}
						}
					}
					else
					{
						System.out.println("ERROR: Spalte ist bereits voll");
					}
				}
				else
				{
					System.out.println("ERROR: Angegebene spalte liegt ausserhalb des verfuegbaren Bereiches");
				}
			}
			else
			{
				System.out.println("ERROR: Spieler ist nicht am Zug");
			}
		}
		else
		{
			System.out.println("ERROR: Spiel ist bereits beendet");
		}
	}
	
	
	private void einwurfGetaetigt(int _spalte, int _zeile, Player _spieler, Chip chip)
	{
		System.out.println("Spieler " + _spieler + " hat in die Spalte " + _spalte + " eingeworfen (Hoehe: " + _zeile + ")");
		myServer.sendMessage(sp1, MessageGenerator.serverSendInsert(_spieler, _spalte, _zeile, chip));
		myServer.sendMessage(sp2, MessageGenerator.serverSendInsert(_spieler, _spalte, _zeile, chip));
		
		//visualisieren();
		spielerwechsel();
		
		Player sieger = localEndGameCheck(_spalte, _zeile);
		//int sieger = globalEndGameCheck();
		
		if (sieger != Player.NONE)
		{
			spielerHatGewonnen(sieger);
		}
		else
		{
			unentschiedenTester();
		}
		
	}

	private void spielerHatGewonnen(Player _sieger)
	{
		beendet = true;
		myServer.sendMessage(sp1, MessageGenerator.serverSendInsertStatus(false));
		myServer.sendMessage(sp2, MessageGenerator.serverSendInsertStatus(false));
		myServer.sendMessage(sp1, MessageGenerator.sendGameEnd(getUser(_sieger).getNick()));
		myServer.sendMessage(sp2, MessageGenerator.sendGameEnd(getUser(_sieger).getNick()));
		int turnsToWinn = Math.round(turnCount/2.0f);
		myServer.sendToAll(MessageGenerator.sendChatMessage(getUser(_sieger).getNick() + " hat gegen " + getOpponent(_sieger).getNick() + " gewonnen! (Zuege: " + turnsToWinn + ", Feld: " + zeilen + " x " + spalten + ")", MessageGenerator.CHAT_SERVER));
		info(getUser(_sieger).getNick() + " hat gewonnen");
		myServer.getGameHandler().endGame(sp1);
	}
	
	private void unentschiedenTester()
	{
		int volleSpalten = 0;
		for (int i = 0 ; i < spalten ; i++)
		{
			if (spielfeld[i][zeilen - 1].getType() != Chip.EMPTY)
			{
				volleSpalten = volleSpalten + 1;
			}
		}
		
		if (volleSpalten == spalten)
		{
			//Alle Spalten sind belegt und niemand hat gewonnen
			spielEndetUnentschieden();
		}
	}
	
	private void spielEndetUnentschieden()
	{
		beendet = true;
		myServer.sendMessage(sp1, MessageGenerator.serverSendInsertStatus(false));
		myServer.sendMessage(sp2, MessageGenerator.serverSendInsertStatus(false));
		myServer.sendMessage(sp1, MessageGenerator.sendGameEnd(MessageGenerator.GAMEEND_DRAW));
		myServer.sendMessage(sp2, MessageGenerator.sendGameEnd(MessageGenerator.GAMEEND_DRAW));
		myServer.sendToAll(MessageGenerator.sendChatMessage("Das Spiel " + sp1.getNick() + " gegen " + sp2.getNick() + " endet unentschieden.", MessageGenerator.CHAT_SERVER));
		info("Das Spiel endet Unentschieden");
		myServer.getGameHandler().endGame(sp1);
	}
	
	//EndGameChecks
	//------------------------------------------------------------------------------------------------------------
	
	
	private Player checkZeile(int _zeile)
	{
		int anzahlInReihe = 0;
		Player aktSpieler = Player.NONE;
		
		for (int i = 0 ; i < spalten ; i++)
		{
			if (spielfeld[i][_zeile].getType() == Chip.NORMAL || (spielfeld[i][_zeile].getType() == Chip.EXPLOSIVE && ruleExplosiveChipsZaehlenFuerSieg))
			{
				if (spielfeld[i][_zeile].getSpieler() == aktSpieler)
				{
					anzahlInReihe = anzahlInReihe + 1;
					if (anzahlInReihe >= anzahlFelderFuerSieg)
					{
						return aktSpieler;
					}
				}
				else
				{
					aktSpieler = spielfeld[i][_zeile].getSpieler();
					anzahlInReihe = 1;
				}
			}
			else
			{
				aktSpieler = Player.NONE;
				anzahlInReihe = 0;
			}
			
		}
		return Player.NONE;
	}
	
	private Player checkSpalte(int _spalte)
	{
		int anzahlInReihe = 0;
		Player aktSpieler = Player.NONE;
		
		for (int i = 0 ; i < zeilen ; i++)
		{
			if (spielfeld[_spalte][i].getType() == Chip.NORMAL || (spielfeld[_spalte][i].getType() == Chip.EXPLOSIVE && ruleExplosiveChipsZaehlenFuerSieg))
			{
				if (spielfeld[_spalte][i].getSpieler() == aktSpieler)
				{
					anzahlInReihe = anzahlInReihe + 1;
					if (anzahlInReihe >= anzahlFelderFuerSieg)
					{
						return aktSpieler;
					}
				}
				else
				{
					aktSpieler = spielfeld[_spalte][i].getSpieler();
					anzahlInReihe = 1;
				}
			}
			else
			{
				aktSpieler = Player.NONE;
				anzahlInReihe = 0;
			}
			
		}
		return Player.NONE;
	}
	
	private Player checkQuerLiUntenReOben(int _reihenIndex)
	{
		int weite;
		int hoehe;
		int anzahlInReihe = 0;
		Player aktSpieler = Player.NONE;
		
		if (_reihenIndex >= 0)
		{
			weite = _reihenIndex;
			hoehe = 0;
		}
		else
		{
			weite = 0;
			hoehe = Math.abs(_reihenIndex);
		}
		while (weite < spalten && hoehe < zeilen)
		{
			if (spielfeld[weite][hoehe].getType() == Chip.NORMAL || (spielfeld[weite][hoehe].getType() == Chip.EXPLOSIVE && ruleExplosiveChipsZaehlenFuerSieg))
			{
				if (spielfeld[weite][hoehe].getSpieler() == aktSpieler)
				{
					anzahlInReihe = anzahlInReihe + 1;
					if (anzahlInReihe >= anzahlFelderFuerSieg)
					{
						return aktSpieler;
					}
				}
				else
				{
					aktSpieler = spielfeld[weite][hoehe].getSpieler();
					anzahlInReihe = 1;
				}
			}
			else
			{
				aktSpieler = Player.NONE;
				anzahlInReihe = 0;
			}
			weite = weite + 1;
			hoehe = hoehe + 1;
		}
		return Player.NONE;
	}
	
	private Player checkQuerLiObenReUnten(int _reihenIndex)
	{
		int weite;
		int hoehe;
		int anzahlInReihe = 0;
		Player aktSpieler = Player.NONE;
		
		if (_reihenIndex >= 0)
		{
			weite = _reihenIndex;
			hoehe = zeilen - 1;
		}
		else
		{
			weite = 0;
			hoehe = (zeilen - 1) - Math.abs(_reihenIndex);
		}
		while (weite < spalten && hoehe >= 0)
		{
			if (spielfeld[weite][hoehe].getType() == Chip.NORMAL || (spielfeld[weite][hoehe].getType() == Chip.EXPLOSIVE && ruleExplosiveChipsZaehlenFuerSieg))
			{
				if (spielfeld[weite][hoehe].getSpieler() == aktSpieler)
				{
					anzahlInReihe = anzahlInReihe + 1;
					if (anzahlInReihe >= anzahlFelderFuerSieg)
					{
						return aktSpieler;
					}
				}
				else
				{
					aktSpieler = spielfeld[weite][hoehe].getSpieler();
					anzahlInReihe = 1;
				}
			}
			else
			{
				aktSpieler = Player.NONE;
				anzahlInReihe = 0;
			}
			weite = weite + 1;
			hoehe = hoehe - 1;
		}
		return Player.NONE;
	}
	
	private Player localEndGameCheck(int _spalte, int _zeile)
	{
		Player resultat = Player.NONE;
		
		//Horizontal Check
		resultat = checkZeile(_zeile);
		if (resultat != Player.NONE)
		{
			return resultat;
		}
		
		//Vertical Check
		resultat = checkSpalte(_spalte);
		if (resultat != Player.NONE)
		{
			return resultat;
		}
		
		//Diagonal Check Links Unten - Rechts Oben
		if (_spalte - _zeile >= 0 - (zeilen - anzahlFelderFuerSieg) && _spalte - _zeile <= spalten - anzahlFelderFuerSieg)
		{
			resultat = checkQuerLiUntenReOben(_spalte - _zeile);
			if (resultat != Player.NONE)
			{
				return resultat;
			}
		}
		
		//Diagonal Check Links Oben - Rechts Unten
		if (_spalte - ((zeilen - 1) - _zeile) >= 0 - (zeilen - anzahlFelderFuerSieg) && _spalte - ((zeilen - 1) - _zeile) <= spalten - anzahlFelderFuerSieg)
		{
			resultat = checkQuerLiObenReUnten(_spalte - ((zeilen - 1) - _zeile));
			if (resultat != Player.NONE)
			{
				return resultat;
			}
		}
		
		return Player.NONE;
	}
	
	//Gibt die SpielerID des Gewinners aus (oder 0 bei keinem Gewinner)
	//Wird nicht angewendet, da localEndGameCheck(int, int) effizienter ist
	private Player globalEndGameCheck()
	{
		//Horizontal Check
		for (int i = 0 ; i < zeilen ; i++)
		{
			Player sieger = Player.NONE;
			sieger = checkZeile(i);
			if (sieger != Player.NONE)
			{
				return sieger;
			}
		}
		
		//Vertical Check
		for (int i = 0 ; i < spalten ; i++)
		{
			Player sieger = Player.NONE;
			sieger = checkSpalte(i);
			if (sieger != Player.NONE)
			{
				return sieger;
			}
		}
		
		//DiagonalCheck Links Unten - Rechts Oben 
		for (int i = 0 - (zeilen - anzahlFelderFuerSieg) ; i <= spalten - anzahlFelderFuerSieg ; i++)
		{
			Player sieger = checkQuerLiUntenReOben(i);
			if (sieger != Player.NONE)
			{
				return sieger;
			}
		}
		
		//DiagonalCheck Links Oben - Rechts Unten
		for (int i = 0 - (zeilen - anzahlFelderFuerSieg) ; i <= spalten - anzahlFelderFuerSieg ; i++)
		{
			Player sieger = checkQuerLiObenReUnten(i);
			if (sieger != Player.NONE)
			{
				return sieger;
			}
		}
		
		return Player.NONE;
		
	}

	//------------------------------------------------------------------------------------------------------------
	
	
	private void spielerwechsel()
	{
		turnCount++;
		amZug = getNichtAmZug();
		info(getUserAmZug().getNick() + " ist am Zug");
		myServer.sendMessage(getUserAmZug(), MessageGenerator.serverSendInsertStatus(true));
		myServer.sendMessage(getUserNichtAmZug(), MessageGenerator.serverSendInsertStatus(false));
	}
	
	private void anfangsspielerAuslosen()
	{
		switch((int) Math.round(Math.random()))
		{
			case 0:
				amZug = Player.RED;
				break;
			case 1:
				amZug = Player.YELLOW;
				break;
			default:
				System.out.println("System.out.println beim Anfangsspieler auslosen");
		}
	}
	
	private void visualisieren()
	{
		System.out.println("--------------------------");
		for (int i = zeilen - 1 ; i >= 0 ; i--)
		{
			for (int j = 0 ; j < spalten ; j++)
			{
				System.out.print("   " + spielfeld[j][i]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
	
	private void info(String _info)
	{
		String info = _info;
		myServer.sendMessage(sp1, MessageGenerator.serverSendLogMessage(info));
		myServer.sendMessage(sp2, MessageGenerator.serverSendLogMessage(info));
	}
	
	private User getUser(Player spieler)
	{
		if(sp1.getPlayer() == spieler)
			return sp1;
		else if(sp2.getPlayer() == spieler)
			return sp2;
		else
			return null;
	}
	
	private User getOpponent(Player spieler)
	{
		if(sp1.getPlayer() == spieler)
			return sp2;
		else if(sp2.getPlayer() == spieler)
			return sp1;
		else
			return null;
	}
	
	private User getUserAmZug()
	{
		return getUser(getAmZug());
	}
	
	private User getUserNichtAmZug() 
	{
		return getUser(getNichtAmZug());
	}
	
	private Player getAmZug()
	{
		return amZug;
	}
	
	private Player getNichtAmZug()
	{
		switch(getAmZug())
		{
			case RED:
				return Player.YELLOW;
			case YELLOW:
				return Player.RED;
			default:
				return Player.NONE;
		}
	}
	
	public User getUser1()
	{
		return sp1;
	}
	
	public User getUser2()
	{
		return sp2;
	}
	
}
