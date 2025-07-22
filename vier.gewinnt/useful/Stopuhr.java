package useful;

/**
 * Eine Stopuhr speichert die aktuelle Zeit beim Starten in Millisekunden genau
 * ab und merkt sich die Differenz zum Start beim Stoppen. Zudem koennen
 * Zwischenstaende ausgegeben werden.
 * 
 * */

public class Stopuhr
{
	private long startMillis;
	private long time;
	private boolean stop;

	/**
	 * Erzeugt eine neue Stopuhr und startet sie automatisch mit
	 * {@link #start()}.
	 */
	public Stopuhr()
	{
		start();
	}

	/**
	 * Startet die Stopuhr, indem die aktuelle Zeit gespeichert wird.
	 */
	public void start()
	{
		stop = false;
		time = 0;
		startMillis = System.currentTimeMillis();
	}

	/**
	 * Stopt die Uhr, indem die Differenz zwischen Start und der aktuellen Zeit
	 * gebildet und abgespeichert wird.
	 * 
	 * @see #getTime()
	 */
	public void stop()
	{
		stop = true;
		startMillis = 0;
		time = System.currentTimeMillis() - startMillis;
	}

	/**
	 * Returned die Differenz die nach dem {@link #stop()} Aufruf gespeichert
	 * wurde oder falls diese Methode noch nicht aufgerufen wurde, den aktuellen
	 * Stand der Stopuhr.
	 * 
	 * @return Die Zeit der Stopuhr, wie oben beschrieben in Millisekunden.
	 */
	public long getTime()
	{
		if (stop)
			return time;
		else
			return System.currentTimeMillis() - startMillis;
	}

	/**
	 * Macht eine Konsolenausgabe mit dem Wert von {@link #getTime()}
	 */
	public void printTime()
	{
		System.out.println("Zeit:\t" + getTime());
	}
}
