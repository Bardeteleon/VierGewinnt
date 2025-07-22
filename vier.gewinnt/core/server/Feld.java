package core.server;

import core.common.Chip;
import core.common.Player;

public class Feld
{
	private Player spieler;
	private Chip type;
	
	public Feld()
	{
		spieler = Player.NONE;
		type = Chip.EMPTY;
	}
	
	public void setSpieler(Player _spieler)
	{
		spieler = _spieler;
	}
	
	public void setType(Chip _type)
	{
		type = _type;
	}
	
	public Player getSpieler()
	{
		return spieler;
	}
	
	public Chip getType()
	{
		return type;
	}
	
}
