package vierGewinnt.client;

import javax.swing.Timer;

public class TurnTimer{

	private GUIVierGewinnt gui;
	
	private int turnTimeInSec;
	private int currentTimeInSec;
	
	private int updateEveryMS;
	
	private Timer updateTimer;
	
	public TurnTimer(int turnTimeInSec, GUIVierGewinnt gui)
	{
		this.turnTimeInSec = turnTimeInSec;
		this.currentTimeInSec = turnTimeInSec;
		this.gui = gui;
		
		updateEveryMS = 1000;
		
		System.out.println("TurnTime: " + turnTimeInSec);
		
		updateTimer = new Timer(updateEveryMS, event -> {
			if(currentTimeInSec >= 0)
			{
				gui.getStatusBar().setRemainingTurnTime(currentTimeInSec*1000);
				currentTimeInSec--;
			}else
			{
				updateTimer.stop();
				gui.tryInsert();
			}
		});
	}
	
	public void start()
	{
		currentTimeInSec = turnTimeInSec;
		updateTimer.restart();
	}
	
	public void stop()
	{
		updateTimer.stop();
	}
	
}
