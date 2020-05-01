package vierGewinnt.server;
import java.util.Vector;

import vierGewinnt.common.Chip;


public class GameHandler
{
	ServerVierGewinnt myServer;
	Vector<GameControl> myGames = new Vector<GameControl>();
	
	public GameHandler(ServerVierGewinnt _myServer)
	{
		myServer = _myServer;
	}
	
	public void newGame(User _sp1, User _sp2, int _spalten, int _zeilen, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		myGames.add(new GameControl(myServer, _spalten, _zeilen, _sp1, _sp2, _expChipsZahlenFuerSieg, _explosionZahltAlsZug, _anzahlExpChips));
	}
	
	public void einwurf(int _spalte, User _spieler, Chip _chipType)
	{
		for (int i = 0 ; i < myGames.size() ; i++)
		{
			if (myGames.get(i).getUser1().equals(_spieler) || myGames.get(i).getUser2().equals(_spieler))
			{
				myGames.get(i).neuerEinwurf(_spalte, _spieler, _chipType);
				return;
			}
		}
	}
	
	public void deleteGame(User _einSpieler)
	{
		for (int i = 0 ; i < myGames.size() ; i++)
		{
			if (myGames.get(i).getUser1().equals(_einSpieler) || myGames.get(i).getUser2().equals(_einSpieler))
			{
				
				myGames.remove(i);
			}
		}
	}
	
	public GameControl endGame(User einTeilnehmerDesSpiels)
	{
		GameControl gc = getGame(einTeilnehmerDesSpiels);
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
		deleteGame(einTeilnehmerDesSpiels);
		return gc;
	}
	
	public GameControl getGame(User _einSpieler)
	{
		for (int i = 0 ; i < myGames.size() ; i++)
		{
			if (myGames.get(i).getUser1().equals(_einSpieler) || myGames.get(i).getUser2().equals(_einSpieler))
			{
				return myGames.get(i);
			}
		}
		return null;
	}
}
