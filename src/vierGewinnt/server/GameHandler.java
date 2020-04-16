package vierGewinnt.server;
import java.util.Vector;


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
	
	public void einwurf(int _spalte, User _spieler, int _chipType)
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