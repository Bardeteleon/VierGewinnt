package vierGewinnt.client.animation;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public abstract class Sprite extends Rectangle2D.Double implements Drawable, Movable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Pause zwischen den einzelnen Bildern in Millisekunden.
	 */
	long delay;
	long animation = 0;
	JComponent parent;
	BufferedImage[] pics;
	int currentpic = -1;

	/**
	 * Horizontale Geschwindigkeit in Pixel pro Sekunde.
	 */
	protected double dx;
	/**
	 * Vertikale Geschwindigkeit in Pixel pro Sekunde.
	 */
	protected double dy;

	int loop_from;
	int loop_to;

	boolean remove;

	public Sprite(BufferedImage[] i, double x, double y, long delay, JComponent p)
	{
		pics = i;
		this.x = x;
		this.y = y;
		this.delay = delay;
		this.width = pics[0].getWidth();
		this.height = pics[0].getHeight();
		parent = p;
		loop_from = 0;
		loop_to = pics.length - 1;
	}

	public void drawObjects(Graphics g)
	{
		if (currentpic >= 0)
			g.drawImage(pics[currentpic], (int) x, (int) y, (int) width, (int) height, null);
	}

	public void doLogic(long delta)
	{

		animation += (delta / 1000000);
		if (animation > getDelay())
		{
			animation = 0;
			computeAnimation();
		}

	}

	public void computeAnimation()
	{

		currentpic++;

		if (currentpic > getLoopTo())
		{
			currentpic = getLoopFrom();
		}

	}

	public void setLoop(int from, int to)
	{
		loop_from = from;
		loop_to = to;
		currentpic = from;
	}

	public void move(long delta)
	{

		if (dx != 0)
		{
			x += dx * (delta / 1e9);
		}

		if (dy != 0)
		{
			y += dy * (delta / 1e9);
		}

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
	
	public void reset()
	{
		currentpic = -1;
		remove = false;
	}

	public static BufferedImage[] loadPics(Object parent, String path, int pics)
	{

		BufferedImage[] anim = new BufferedImage[pics];
		BufferedImage source = null;

		URL pic_url = parent.getClass().getClassLoader().getResource(path);

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
		return delay;
	}

	public int getLoopTo() {
		return loop_to;
	}

	public int getLoopFrom() {
		return loop_from;
	}

	public boolean doRemove() {
		return remove;
	}

}
