package vierGewinnt.server;

public class Feld
{
	private int spieler;
	private int type;
	
	public static final int LEER = 0;
	public static final int NORMAL = 1;
	public static final int EXPLOSIV = 2;
	
	public Feld()
	{
		spieler = 0;
		type = LEER;
	}
	
	public void setSpieler(int _spieler)
	{
		spieler = _spieler;
	}
	
	public void setType(int _type)
	{
		type = _type;
	}
	
	public int getSpieler()
	{
		return spieler;
	}
	
	public int getType()
	{
		return type;
	}
	
}
