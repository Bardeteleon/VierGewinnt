package vierGewinnt.client.animation;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GlassPaneAnimation extends JPanel implements Runnable
{
	private long delayTimeMS;
	
	private long runningTimeNS;
	
	private long deltaTimeNS;
	private long lastTimeNS;
	private long fps;

	private Thread thread;
	
	private Vector<Sprite> waitingActors;
	private Vector<Sprite> activeActors;
	private Vector<Sprite> painter;

	private boolean paused;

	private int resumeAfterMS;
	
	public GlassPaneAnimation()
	{
		delayTimeMS = 10;
		
		deltaTimeNS = 0;
		lastTimeNS = System.nanoTime();
		fps = 0;
		
		paused = false;
		
		resumeAfterMS = 1; // 1 because 0 is like wait().. until notified
		
		waitingActors = new Vector<Sprite>();
		activeActors = new Vector<Sprite>();
		painter = new Vector<Sprite>();
		
		setOpaque(false);

		runningTimeNS = 0;

		pause();
		
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		
	}

	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		if (!paused)
		{
			// g.setColor(Color.BLACK);
			// g.fillRect(0, 0, getWidth(), 30);
			// g.setColor(Color.RED);
			// g.drawString("FPS: " + Long.toString(fps), getWidth() / 2, 10);
			for (Sprite sprite : painter)
			{
				sprite.drawObjects(graphics);
			}
		}
	}

	@Override
	public void run()
	{
		while (true)
		{
			computeDelta();

//			System.out.println("Time: " + (runningTimeNS/1e6));
			
			activateWaitingActors();
			
//			System.out.println(waitingActors);
//			System.out.println(activeActors);
			
			if (noActorsAvailable())
				pause();
						
			if (!paused)
			{
				cloneVectors();
				updateSprites();
				repaint();
			} else
			{
				synchronized (this)
				{
					try
					{
						painter = new Vector<Sprite>();
						repaint();
						wait();
						wait(resumeAfterMS);
						resumeAfterMS = 1; // 1 because 0 is like wait().. until notified
						lastTimeNS = System.nanoTime();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			try
			{
				Thread.sleep(delayTimeMS);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void computeDelta()
	{
		deltaTimeNS = System.nanoTime() - lastTimeNS;
		lastTimeNS = System.nanoTime();
		fps = ((long) 1e9) / deltaTimeNS;
		runningTimeNS += deltaTimeNS;
	}
	
	private void activateWaitingActors()
	{
		for(int i = 0; i < waitingActors.size(); i++)
		{
			Sprite sprite = waitingActors.get(i);
			sprite.doWaiting(deltaTimeNS);
			if(!sprite.isWaiting())
			{
				waitingActors.remove(i);
				i--;
				activeActors.add(sprite);
			}
		}
	}

	private void updateSprites()
	{
		for (Sprite sprite : activeActors)
		{
			sprite.doLogic(deltaTimeNS);
			
			sprite.move(deltaTimeNS);

			// TODO Collisions
		}
		activeActors.removeIf(sprite -> sprite.doRemove());
	}

	public void addAnimation(Sprite s)
	{
		waitingActors.add(s);
	}

//	public void removeAniamtion(Sprite s)
//	{
//		activeActors.remove(s);
//	}
	
	public synchronized void resume()
	{
		paused = false;
		notify();
	}
	
	public void resumeAfter(int afterMS)
	{
		if(paused)
		{
			resumeAfterMS = afterMS;
			resume();
		}
	}

	public void pause()
	{
		paused = true;
	}

	private void cloneVectors()
	{
		painter = (Vector<Sprite>) activeActors.clone();
	}
	
	private boolean noActorsAvailable()
	{
		return activeActors.size() == 0 && waitingActors.size() == 0;
	}
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			
			JFrame frame = new JFrame();
			frame.setSize(600, 700);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			GlassPaneAnimation glassAni = new GlassPaneAnimation();
			frame.setGlassPane(glassAni);
			frame.getGlassPane().setVisible(true);
			
			Explosion expAni = new Explosion(glassAni, Sprite.loadPics(glassAni, "vierGewinnt/client/bilder/ExplosionOne.png", 17), 100);
			expAni.setRelativePositioning(true);
			expAni.setRelativeResizing(true);
			expAni.setPosition(0, 0);
			
			Explosion expAni2 = new Explosion(glassAni, Sprite.loadPics(glassAni, "vierGewinnt/client/bilder/ExplosionOne.png", 17), 100);
			expAni2.setRelativePositioning(true);
			expAni2.setRelativeResizing(true);
			expAni2.setPosition(200, 200);
			expAni2.setInitialWaitingDelayMS(1800);
			
			OnePicAnimation winnerAni = new OnePicAnimation(glassAni, Sprite.loadPics(glassAni, "vierGewinnt/client/bilder/gewonnen.jpg", 1), 100, 20);
			winnerAni.setCentral(true);
			winnerAni.setWaitingSteps(5);
			winnerAni.setInitialWaitingDelayMS(4000);
			
			frame.setVisible(true);
			
			glassAni.addAnimation(expAni);
			glassAni.addAnimation(expAni2);
			glassAni.addAnimation(winnerAni);
			glassAni.resumeAfter(2000);
			
		});
	}
}
