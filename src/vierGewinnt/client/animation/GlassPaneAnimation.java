package vierGewinnt.client.animation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

public class GlassPaneAnimation extends JPanel implements Runnable
{
	/**
	 * Zeitspanne für einen Schleifen-Durchlauf in Nanosekunden.
	 */
	private long delta = 0;
	private long last = 0;
	private long fps = 0;

	private Thread thread;
	private Vector<Sprite> actors;
	private Vector<Sprite> painter;

	private boolean paused = false;
	private boolean relativeResizing = false;
	private boolean relativePositioning = false;
	private Dimension lastPanelDimension;

	private int resumeAfterMS = 0;
	
	public GlassPaneAnimation()
	{
		actors = new Vector<Sprite>();
		painter = new Vector<Sprite>();
		setOpaque(false);

		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		pause();
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (lastPanelDimension == null)
			lastPanelDimension = new Dimension(getWidth(), getHeight());
		if (!lastPanelDimension.equals(getSize()))
			doRelativeSprites();

		if (!paused)
		{
			// g.setColor(Color.BLACK);
			// g.fillRect(0, 0, getWidth(), 30);
			// g.setColor(Color.RED);
			// g.drawString("FPS: " + Long.toString(fps), getWidth() / 2, 10);
			for (Sprite s : painter)
			{
				// System.out.println("Paint Currentpic: "+s.currentpic);
				s.drawObjects(g);
			}
		}
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (actors.size() == 0)
				pause();

			computeDelta();

			if (actors.size() <= 0)
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
						resumeAfterMS = 0;
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void updateSprites()
	{
		for (int i = 0; i < actors.size(); i++)
		{
			Sprite s = actors.get(i);
			// System.out.println("Update currentpic: "+s.currentpic);
			s.doLogic(delta);

			if (s.doRemove())
			{
				actors.remove(s);
			}

			s.move(delta);

			// TODO Collisions
		}

	}

	public void addAnimation(Sprite s)
	{
		actors.add(s);
//		start();
	}

	public void removeAniamtion(Sprite s)
	{
		actors.remove(s);
	}

	
	public void resumeAfter(int afterMS)
	{
		resumeAfterMS = afterMS;
		resume();
	}
	
	public synchronized void resume()
	{
		paused = false;
		notify();
	}

	public void pause()
	{
		paused = true;
	}

	public void setRelativeSpriteResizing(boolean onOff)
	{
		relativeResizing = onOff;
	}

	public void setRelativeSpritePositioning(boolean onOff)
	{
		relativePositioning = onOff;
	}

	private void doRelativeSprites() // TODO Seitenverhältnis beibehalten
	{
		if (relativePositioning)
		{
			for (Sprite s : painter)
			{
				s.x = (s.x / lastPanelDimension.width) * getSize().width;
				s.y = (s.y / lastPanelDimension.height) * getSize().height;
			}
		}
		if (relativeResizing)
		{
			for (Sprite s : painter)
			{
				s.width = (s.width / lastPanelDimension.width) * getSize().width;
				s.height = (s.height / lastPanelDimension.height) * getSize().height;
			}
		}
		lastPanelDimension = getSize();
	}

	private void computeDelta()
	{

		delta = System.nanoTime() - last;
		last = System.nanoTime();
		fps = ((long) 1e9) / delta;

	}

	private void cloneVectors()
	{
		painter = (Vector<Sprite>) actors.clone();
	}
}
