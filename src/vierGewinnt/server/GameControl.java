package vierGewinnt.server;

import vierGewinnt.MessageGenerator;


public class GameControl
{
	public static final int NORMALCHIP = 1;
	public static final int EXPLOSIVCHIP = 2;
	
	
	ServerVierGewinnt myServer;
	
	Feld[][] spielfeld; //0: leer | 1: von Sp1 belegt | 2: von Sp2 belegt
	int spalten; //in Feldern
	int zeilen;
	int anzahlFelderFuerSieg;
	int amZug; //1: Sp1 am Zug | 2: Sp2 am Zug
	boolean beendet;
	
	boolean ruleExplosiveChipsZaehlenFuerSieg = false;
	boolean ruleExplosionZaehltAlsZug = true;
	
	int[] explosivchipsVonSp = {0, 0, 0};
	
	//String sp1IP;
	//String sp2IP;
	private User sp1, sp2;
	
	public GameControl(ServerVierGewinnt _myServer, int _spalten, int _zeilen, User _sp1, User _sp2, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		myServer = _myServer;
		//EVTL Abfrage: spalten und zeilen müssen größer sein, als anzahlFelderFuerSieg!!!
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
		
		anzahlFelderFuerSieg = 4; //VIER Gewinnt eben
		
		sp1 = _sp1;
		sp2 = _sp2;
		
		ruleExplosiveChipsZaehlenFuerSieg = _expChipsZahlenFuerSieg;
		ruleExplosionZaehltAlsZug = _explosionZahltAlsZug;
		
		explosivchipsVonSp[1] = _anzahlExpChips;
		explosivchipsVonSp[2] = _anzahlExpChips;
		
		neuesSpiel();
	}
	
	private void neuesSpiel()
	{
		System.out.println("Spiel beginnt (" + sp1 + " & " + sp2 + " | Spielfeldgröße: " + spalten + "x" + zeilen + " | expChipsZahlenFuerSieg: " + ruleExplosiveChipsZaehlenFuerSieg + " | explosionZahltAlsZug: " + ruleExplosionZaehltAlsZug + " | anzahlExpChips: " + explosivchipsVonSp[1] + ")");
		
		anfangsspielerAuslosen();

		myServer.sendMessage(getUserAmZug(), MessageGenerator.serverSendGameStart(getUserNichtAmZug(), spalten, zeilen, amZug, explosivchipsVonSp[1]));
		myServer.sendMessage(getUserNichtAmZug(), MessageGenerator.serverSendGameStart(getUserAmZug(), spalten, zeilen, getNichtAmZug(), explosivchipsVonSp[1]));

		myServer.sendMessage(getUserAmZug(), MessageGenerator.serverSendInsertStatus(getUserNichtAmZug(), true));
		myServer.sendMessage(getUserNichtAmZug(), MessageGenerator.serverSendInsertStatus(getUserAmZug(), false));
		
		beendet = false;
		info("Neues Spiel gestartet");
		info(getUserAmZug().getNick() + " beginnt");
	}
	
	
	public void sprengen(int _spalte, int _zeile, User u)
	{
		int spieler = 0;
		if (u.equals(sp1))
		{
			spieler = 1;
		}
		else if(u.equals(sp2))
		{
			spieler = 2;
		}
		else
		{
			//IP passt nicht
			fehler("SP6");
			return;
		}
		
		if (!beendet)
		{
			if (u.equals(getUserAmZug()))
			{
				if (_spalte >= 0 && _spalte < spalten)
				{
					if (_zeile >= 0 && _zeile < zeilen)
					{
						if (spielfeld[_spalte][_zeile].getType() == Feld.EXPLOSIV)
						{
							if (spielfeld[_spalte][_zeile].getSpieler() == spieler)
							{
								execSprengung(_spalte, _zeile);
							}
							else
							{
								//Das Feld gehört nicht zu diesem Spieler
								fehler("SP6");
							}
						}
						else
						{
							//Angegebener Chip ist kein Explosivfeld
							fehler("SP5");
						}
					}
					else
					{
						//Angegebene Zeile liegt außerhalb das gültigen Bereichs
						fehler("SP4");
					}
				}
				else
				{
					//Angegebene Spalte liegt außerhalb das gültigen Bereichs
					fehler("SP3");
				}
			}
			else
			{
				//Spieler nicht gefunden oder nicht am Zug
				fehler("SP2");
			}
		}
		else
		{
			//Das Spiel ist bereits beendet
			fehler("SP1");
		}
	}
	
	private void execSprengung(int _spalte, int _zeile)
	{
		myServer.sendMessage(sp1, MessageGenerator.explosion(sp2, _spalte, _zeile));
		myServer.sendMessage(sp2, MessageGenerator.explosion(sp1, _spalte, _zeile));
		
		//Entfenen des Sprengchips
		spielfeld[_spalte][_zeile].setSpieler(0);
		spielfeld[_spalte][_zeile].setType(Feld.LEER);
		//Nachrutschen der anderen Chips
		for (int i = _zeile + 1 ; i < zeilen ; i++)
		{
			if (spielfeld[_spalte][i].getType() != Feld.LEER)
			{
				spielfeld[_spalte][i - 1].setType(spielfeld[_spalte][i].getType());
				spielfeld[_spalte][i - 1].setSpieler(spielfeld[_spalte][i].getSpieler());
				spielfeld[_spalte][i].setType(0);
				spielfeld[_spalte][i].setSpieler(0);
			}
			else
			{
				break;
			}
		}
		
		//Anzeigen beim Spieler aktualisieren
		/*NICHT NÖTIG: CLIENT AKTUALISIER SICH SELBST
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
		int sieger = 0;
		for (int i = 0 ; i < zeilen ; i++)
		{
			if (spielfeld[_spalte][i].getType() == Feld.LEER)
			{
				break;
			}
			else
			{
				int temp = localEndGameCheck(_spalte, i);
				
				if (temp != 0)
				{
					if (sieger == 0)
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
		if (sieger != 0)
		{
			spielerHatGewonnen(sieger);
		}
		
	}
	
	public void neuerEinwurf(int _spalte, User _spieler, int chipType)
	{
		int spieler = 0;
		if (_spieler.equals(sp1))
		{
			spieler = 1;
		}
		else if(_spieler.equals(sp2))
		{
			spieler = 2;
		}
		else
		{
			//IP passt nicht
			fehler("NE6");
			return;
		}
		
		if (!beendet)
		{
			if (spieler == 1 || spieler == 2)
			{
				if (spieler == amZug)
				{
					if (_spalte >= 0 && _spalte < spalten)
					{
						
						if (spielfeld[_spalte][zeilen - 1].getType() == Feld.LEER)
						{
							for (int i = 0 ; i < zeilen ; i++)
							{
								if (spielfeld[_spalte][i].getType() == Feld.LEER)
								{
									if (chipType == NORMALCHIP)
									{
										spielfeld[_spalte][i].setSpieler(spieler);
										spielfeld[_spalte][i].setType(Feld.NORMAL);
										einwurfGetaetigt(_spalte, i, spieler, chipType);
									}
									else if (chipType == EXPLOSIVCHIP)
									{
										if (explosivchipsVonSp[amZug] > 0)
										{
											explosivchipsVonSp[amZug] = explosivchipsVonSp[amZug] - 1;
											info(getUserAmZug().getNick()+" hat noch " + explosivchipsVonSp[amZug]+" Bomben");
											spielfeld[_spalte][i].setSpieler(spieler);
											spielfeld[_spalte][i].setType(Feld.EXPLOSIV);
											einwurfGetaetigt(_spalte, i, spieler, chipType);
										}
										else
										{
											//Der Spieler hat keine Explosivchips mehr
											fehler("NE8");
										}
									}
									else
									{
										//Ungültiger Chiptype
										fehler("NE7");
									}
									break;
								}
							}
						}
						else
						{
							//Spalte ist bereits voll
							fehler("NE5");
						}
					}
					else
					{
						//Angegebene spalte liegt außerhalb des verfügbaren Bereiches
						fehler("NE3");
					}
				}
				else
				{
					//Spieler ist nicht am Zug
					fehler("NE1");
				}
			}
			else
			{
				//Spieler gibt es nicht
				fehler("NE4");
			}
		}
		else
		{
			//Spiel ist bereits beendet
			fehler("NE2");
		}
	}
	
	
	private void einwurfGetaetigt(int _spalte, int _zeile, int _spieler, int chipType)
	{
		//System.out.println("Spieler " + " hat in die Spalte " + _spalte + " eingeworfen (Höhe: " + _zeile + ")");
		myServer.sendMessage(sp1, MessageGenerator.serverSendInsert(sp2, _spieler, _spalte, _zeile, chipType));
		myServer.sendMessage(sp2, MessageGenerator.serverSendInsert(sp1, _spieler, _spalte, _zeile, chipType));
		
		//visualisieren();
		spielerwechsel();
		
		int sieger = localEndGameCheck(_spalte, _zeile);
		//int sieger = globalEndGameCheck();
		
		if (sieger != 0)
		{
			spielerHatGewonnen(sieger);
		}
		else
		{
			unentschiedenTester();
		}
		
	}

	private void spielerHatGewonnen(int _sieger)
	{
		beendet = true;
		myServer.sendMessage(sp1, MessageGenerator.serverSendGameEnd(sp2, getUser(_sieger).getNick()));
		myServer.sendMessage(sp2, MessageGenerator.serverSendGameEnd(sp1, getUser(_sieger).getNick()));
		//System.out.println("Sieger: " + sieger);
		myServer.sendMessage(sp1, MessageGenerator.serverSendInsertStatus(sp2, false));
		myServer.sendMessage(sp2, MessageGenerator.serverSendInsertStatus(sp1, false));
		info(getUser(_sieger).getNick() + " hat gewonnen");
		myServer.spielBeendet(sp1);
	}
	
	private void unentschiedenTester()
	{
		int volleSpalten = 0;
		for (int i = 0 ; i < spalten ; i++)
		{
			if (spielfeld[i][zeilen - 1].getType() != Feld.LEER)
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
		myServer.sendMessage(sp1, MessageGenerator.serverSendGameEnd(sp2, " "));
		myServer.sendMessage(sp2, MessageGenerator.serverSendGameEnd(sp1, " "));
		//System.out.println("Unentschieden");
		myServer.sendMessage(sp1, MessageGenerator.serverSendInsertStatus(sp2, false));
		myServer.sendMessage(sp2, MessageGenerator.serverSendInsertStatus(sp1, false));
		info("Das Spiel endet Unentschieden");
		myServer.spielBeendet(sp1);
	}
	
	//EndGameChecks
	//------------------------------------------------------------------------------------------------------------
	
	
	private int checkZeile(int _zeile)
	{
		int anzahlInReihe = 0;
		int aktSpieler = 0;
		for (int i = 0 ; i < spalten ; i++)
		{
			if (spielfeld[i][_zeile].getType() == Feld.NORMAL || (spielfeld[i][_zeile].getType() == Feld.EXPLOSIV && ruleExplosiveChipsZaehlenFuerSieg))
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
				aktSpieler = 0;
				anzahlInReihe = 0;
			}
			
		}
		return 0;
	}
	
	private int checkSpalte(int _spalte)
	{
		int anzahlInReihe = 0;
		int aktSpieler = 0;
		for (int i = 0 ; i < zeilen ; i++)
		{
			if (spielfeld[_spalte][i].getType() == Feld.NORMAL || (spielfeld[_spalte][i].getType() == Feld.EXPLOSIV && ruleExplosiveChipsZaehlenFuerSieg))
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
				aktSpieler = 0;
				anzahlInReihe = 0;
			}
			
		}
		return 0;
	}
	
	private int checkQuerLiUntenReOben(int _reihenIndex)
	{
		int weite;
		int hoehe;
		int anzahlInReihe = 0;
		int aktSpieler = 0;
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
			if (spielfeld[weite][hoehe].getType() == Feld.NORMAL || (spielfeld[weite][hoehe].getType() == Feld.EXPLOSIV && ruleExplosiveChipsZaehlenFuerSieg))
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
				aktSpieler = 0;
				anzahlInReihe = 0;
			}
			weite = weite + 1;
			hoehe = hoehe + 1;
		}
		return 0;
	}
	
	private int checkQuerLiObenReUnten(int _reihenIndex)
	{
		int weite;
		int hoehe;
		int anzahlInReihe = 0;
		int aktSpieler = 0;
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
			if (spielfeld[weite][hoehe].getType() == Feld.NORMAL || (spielfeld[weite][hoehe].getType() == Feld.EXPLOSIV && ruleExplosiveChipsZaehlenFuerSieg))
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
				aktSpieler = 0;
				anzahlInReihe = 0;
			}
			weite = weite + 1;
			hoehe = hoehe - 1;
		}
		return 0;
	}
	
	private int localEndGameCheck(int _spalte, int _zeile)
	{
		int resultat = 0;
		
		//Horizontal Check
		resultat = checkZeile(_zeile);
		if (resultat != 0)
		{
			return resultat;
		}
		
		//Vertical Check
		resultat = checkSpalte(_spalte);
		if (resultat != 0)
		{
			return resultat;
		}
		
		//Diagonal Check Links Unten - Rechts Oben
		if (_spalte - _zeile >= 0 - (zeilen - anzahlFelderFuerSieg) && _spalte - _zeile <= spalten - anzahlFelderFuerSieg)
		{
			resultat = checkQuerLiUntenReOben(_spalte - _zeile);
			if (resultat != 0)
			{
				return resultat;
			}
		}
		
		//Diagonal Check Links Oben - Rechts Unten
		if (_spalte - ((zeilen - 1) - _zeile) >= 0 - (zeilen - anzahlFelderFuerSieg) && _spalte - ((zeilen - 1) - _zeile) <= spalten - anzahlFelderFuerSieg)
		{
			resultat = checkQuerLiObenReUnten(_spalte - ((zeilen - 1) - _zeile));
			if (resultat != 0)
			{
				return resultat;
			}
		}
		
		return 0;
	}
	
	//Gibt die SpielerID des Gewinners aus (oder 0 bei keinem Gewinner)
	//Wird nicht angewendet, da localEndGameCheck(int, int) effizienter ist
	private int globalEndGameCheck()
	{
		//Horizontal Check
		for (int i = 0 ; i < zeilen ; i++)
		{
			int sieger = 0;
			sieger = checkZeile(i);
			if (sieger != 0)
			{
				return sieger;
			}
		}
		
		//Vertical Check
		for (int i = 0 ; i < spalten ; i++)
		{
			int sieger = 0;
			sieger = checkSpalte(i);
			if (sieger != 0)
			{
				return sieger;
			}
		}
		
		//DiagonalCheck Links Unten - Rechts Oben 
		for (int i = 0 - (zeilen - anzahlFelderFuerSieg) ; i <= spalten - anzahlFelderFuerSieg ; i++)
		{
			int sieger = checkQuerLiUntenReOben(i);
			if (sieger != 0)
			{
				return sieger;
			}
		}
		
		//DiagonalCheck Links Oben - Rechts Unten
		for (int i = 0 - (zeilen - anzahlFelderFuerSieg) ; i <= spalten - anzahlFelderFuerSieg ; i++)
		{
			int sieger = checkQuerLiObenReUnten(i);
			if (sieger != 0)
			{
				return sieger;
			}
		}
		
		return 0;
		
	}

	//------------------------------------------------------------------------------------------------------------
	
	
	private void spielerwechsel()
	{
		if (amZug == 1)
		{
			amZug = 2;
		}
		else
		{
			amZug = 1;
		}
		info(getUserAmZug().getNick() + " ist am Zug");
		myServer.sendMessage(getUserAmZug(), MessageGenerator.serverSendInsertStatus(getUserNichtAmZug(), true));
		myServer.sendMessage(getUserNichtAmZug(), MessageGenerator.serverSendInsertStatus(getUserAmZug(), false));
	}
	
	private void anfangsspielerAuslosen()
	{
		int anfangsspieler = (int) Math.round(Math.random()) + 1;
		amZug = anfangsspieler;
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
	
	private void fehler(String _fehlercode)
	{
		String fehlercode = "fehlercode:" + _fehlercode;
		//HIER KANN GGF. DER FEHLERCODE WEITERGEGEBEN UND DORT INTERPRETIERT WERDEN (Verwendung nicht unbedingt erforderlich)
		System.out.println(fehlercode);
	}
	
	private void info(String _info)
	{
		String info = _info;
		//System.out.println(info);
		myServer.sendMessage(sp1, MessageGenerator.serverSendLogMessage(sp2, info));
		myServer.sendMessage(sp2, MessageGenerator.serverSendLogMessage(sp1, info));
	}
	
	private User getUserAmZug()
	{
		if (amZug == 1)
		{
			return sp1;
		}
		else if (amZug == 2)
		{
			return sp2;
		}
		else
		{
			return null;
		}
	}
	
	private User getUserNichtAmZug()
	{
		if (amZug == 1)
		{
			return sp2;
		}
		else if (amZug == 2)
		{
			return sp1;
		}
		else
		{
			return null;
		}
	}
	
	private int getNichtAmZug()
	{
		if (amZug == 1)
		{
			return 2;
		}
		else if (amZug == 2)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	public User getUser(int u)
	{
		if(u == 1)
			return sp1;
		else if(u == 2)
			return sp2;
		else return null;
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
