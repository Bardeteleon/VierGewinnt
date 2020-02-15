package vierGewinnt.server;

public class Invitation
{
	private User invitedBy;
	private int spalten;
	private int zeilen;
	private boolean expChipsZahlenFuerSieg;
	private boolean explosionZahltAlsZug;
	private int anzahlExpChips;

	public Invitation(User _invitedBy, int _spalten, int _zeilen, boolean _expChipsZahlenFuerSieg, boolean _explosionZahltAlsZug, int _anzahlExpChips)
	{
		invitedBy = _invitedBy;
		spalten = _spalten;
		zeilen = _zeilen;
		expChipsZahlenFuerSieg = _expChipsZahlenFuerSieg;
		explosionZahltAlsZug = _explosionZahltAlsZug;
		anzahlExpChips = _anzahlExpChips;
	}
	
	public User getInvitedBy()
	{
		return invitedBy;
	}
	
	public int getSpalten()
	{
		return spalten;
	}
	
	public int getZeilen()
	{
		return zeilen;
	}
	
	public boolean getExpChipsZahlenFuerSieg()
	{
		return expChipsZahlenFuerSieg;
	}
	
	public boolean getExplosionZahltAlsZug()
	{
		return explosionZahltAlsZug;
	}
	
	public int getAnzahlExpChips()
	{
		return anzahlExpChips;
	}
	
}
