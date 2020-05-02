package vierGewinnt.client.animation;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class Explosion extends Sprite
{
	int lastPic = 0;
	
	public Explosion(JComponent parent, BufferedImage[] images, long delay)
	{
		super(images, delay, parent);
	}
	
	@Override
	public void doLogic(long delta)
	{
		super.doLogic(delta);
		if(animationCurrentPic == 0 && lastPic > 0)
		{
			remove = true;
		}
		lastPic = animationCurrentPic;
	}
	
	public void reset()
	{
		super.reset();
		lastPic = 0;
	}

	@Override
	public boolean collidedWith(Sprite s)
	{
		return false;
	}
}
