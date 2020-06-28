package vierGewinnt.client.animation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import useful.IO;

public abstract class Sprite extends Rectangle2D.Double implements Drawable, Movable
{
	JComponent parent;
	
	long initialWaitingDelayMS;
	long initialWaitingTimeMS;
	
	long animationDelayMS; // Pause zwischen den einzelnen Bildern in Millisekunden.
	long animationTimeMS;
	BufferedImage[] animationPics;
	int animationCurrentPic;
	int animationLoopFrom;
	int animationLoopTo;

	protected double dx; // Horizontale Geschwindigkeit in Pixel pro Sekunde.
	protected double dy; // Vertikale Geschwindigkeit in Pixel pro Sekunde.

	boolean remove;

	Dimension lastParentSize;
	boolean doRelativeResizing;
	boolean doRelativePositioning;

	public Sprite(BufferedImage[] pics, long delay, JComponent parent)
	{
		this.x = 0;
		this.y = 0;
		this.width = pics[0].getWidth();
		this.height = pics[0].getHeight();
		
		this.parent = parent;
		
		this.initialWaitingDelayMS = 0;
		this.initialWaitingTimeMS = 0;
		
		this.animationPics = pics;
		this.animationDelayMS = delay;
		this.animationLoopFrom = 0;
		this.animationLoopTo = pics.length - 1;
		this.animationTimeMS = 0;
		this.animationCurrentPic = 0;

		this.lastParentSize = null;
		this.doRelativeResizing = false;
		this.doRelativePositioning = false;
	}

	public void drawObjects(Graphics graphics)
	{
		if(lastParentSize == null)
			lastParentSize = parent.getSize();
		
		if(!lastParentSize.equals(parent.getSize()))
			computeRelativeSprite();
			
		if (!isWaiting())
			graphics.drawImage(animationPics[animationCurrentPic], (int) x, (int) y, (int) width, (int) height, null);
	}
	
	// in the beginning a sprite can wait a certain time until it gets active
	// in the transition between waiting and active, doWaiting and doLogic are executed both (depends on GlasPaneAni impl)
	public void doWaiting(long deltaTimePassedNS)
	{
		initialWaitingTimeMS += (deltaTimePassedNS / 1e6);
	}
	
	public boolean isWaiting()
	{
		return initialWaitingTimeMS < initialWaitingDelayMS;
	}

	// Run after every calculation delay of sprite handler
	public void doLogic(long deltaTimePassedNS)
	{
		animationTimeMS += (deltaTimePassedNS / 1e6);
		if (animationTimeMS > animationDelayMS)
		{
			animationTimeMS = 0;
			computeAnimation();
		}
	}

	// Run after every animationDelay
	public void computeAnimation()
	{
//		System.out.println("Compute animation " + animationCurrentPic);
		
		animationCurrentPic++;

		if (animationCurrentPic > animationLoopTo)
		{
			animationCurrentPic = animationLoopFrom;
		}
	}

	public void setLoop(int from, int to)
	{
		animationLoopFrom = from;
		animationLoopTo = to;
		animationCurrentPic = from;
	}

	public void move(long deltaTimePassedNS)
	{
		if (dx != 0)
		{
			x += dx * (deltaTimePassedNS / 1e9);
		}

		if (dy != 0)
		{
			y += dy * (deltaTimePassedNS / 1e9);
		}
	}
	
	private void computeRelativeSprite() // TODO Seitenverh�ltnis beibehalten
	{
		if (doRelativePositioning)
		{
			x = x * parent.getSize().width / lastParentSize.width;
			y = y * parent.getSize().height / lastParentSize.height;
		}
		if (doRelativeResizing)
		{
			width = width * parent.getSize().width / lastParentSize.width;
			height = height * parent.getSize().height / lastParentSize.height;
		}
		lastParentSize = parent.getSize();
	}

	public void setRelativeResizing(boolean onOff)
	{
		doRelativeResizing = onOff;
	}

	public void setRelativePositioning(boolean onOff)
	{
		doRelativePositioning = onOff;
	}

	public abstract boolean collidedWith(Sprite s);

	public double getHorizontalSpeed()
	{
		return dx;
	}

	public void setHorizontalSpeed(double dx)
	{
		this.dx = dx;
	}

	public double getVerticalSpeed()
	{
		return dy;
	}

	public void setVerticalSpeed(double dy)
	{
		this.dy = dy;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setPosition(double d, double e)
	{
		this.x = d;
		this.y = e;
	}
	
	public void setInitialWaitingDelayMS(long delay)
	{
		this.initialWaitingDelayMS = delay;
	}
	
	public void reset()
	{
		animationCurrentPic = 0;
		remove = false;
		initialWaitingTimeMS = 0;
		animationTimeMS = 0;
	}

	public static BufferedImage[] loadPics(Object parent, String path, int pics)
	{

		BufferedImage[] anim = new BufferedImage[pics];
		BufferedImage source = null;

		URL pic_url = IO.getResourceURL(path);

		try
		{
			source = ImageIO.read(pic_url);
		} catch (IOException e)
		{
		}

		for (int x = 0; x < pics; x++)
		{
			anim[x] = source.getSubimage(x * source.getWidth() / pics, 0, source.getWidth() / pics, source.getHeight());
		}

		return anim;
	}

	public long getDelay() {
		return animationDelayMS;
	}

	public int getLoopTo() {
		return animationLoopTo;
	}

	public int getLoopFrom() {
		return animationLoopFrom;
	}

	public boolean doRemove() {
		return remove;
	}

}
