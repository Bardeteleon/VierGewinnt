package vierGewinnt.client;

import java.awt.image.BufferedImage;

public class Explosion extends Sprite
{
	int last = 0;
	
	public Explosion(BufferedImage[] images, long delay)
	{
		super(images, 0, 0, delay, null);
	}
	
	@Override
	public void doLogic(long delta)
	{
		super.doLogic(delta);
		if(currentpic == 0 && last != 0)
		{
			currentpic = -1;
			remove = true;
		}
		last = currentpic;
	}
	
	public void reset()
	{
		super.reset();
		last = 0;
	}

	@Override
	public boolean collidedWith(Sprite s)
	{
		return false;
	}
}
