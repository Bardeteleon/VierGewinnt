package vierGewinnt.client.animation;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class OnePicAnimation extends Sprite
{
	boolean central;
	int stepsTotal;
	int stepCurrent = 1;
	int stepsHoldingInTheEnd;

	public OnePicAnimation(JComponent parent, BufferedImage[] pics, long delay, int steps)
	{
		super(pics, delay, parent);
		this.stepsTotal = steps;
	}

	@Override
	public void drawObjects(Graphics g)
	{
		if (central)
		{
			x = parent.getWidth() / 2 - width / 2;
			y = parent.getHeight() / 2 - height / 2;
		}
		super.drawObjects(g);
	}

	@Override
	public void doLogic(long delta)
	{
		super.doLogic(delta);
		// logic can be in computeAnimation() right?
		if (stepCurrent <= stepsTotal - stepsHoldingInTheEnd)
		{
			if (height / width <= parent.getHeight() / parent.getWidth())
			{
				int newWidth = stepCurrent * (parent.getWidth() / (stepsTotal - stepsHoldingInTheEnd));
				height = newWidth * (height / width); // keep size ratio
				width = newWidth;
			} else if (width / height <= parent.getWidth() / parent.getHeight())
			{
				int newHeight = stepCurrent * (parent.getHeight() / (stepsTotal - stepsHoldingInTheEnd));
				width = newHeight * (width / height); // keep size ratio
				height = newHeight;

			} else
			{
				System.out.println("OnePicAni doLogic Error");
				System.out.println("Parent width: " + parent.getWidth());
				System.out.println("Parent height: " + parent.getHeight());
				System.out.println("Sprite width: " + width);
				System.out.println("Sprite height: " + height);
			}
		}
	}

	@Override
	public void computeAnimation()
	{
		super.computeAnimation();
		if (stepCurrent <= stepsTotal)
		{
			stepCurrent++;
		} else
			remove = true;
	}

	public void reset()
	{
		super.reset();
		stepCurrent = 1;
	}

	public void setWaitingSteps(int steps)
	{
		if (stepsHoldingInTheEnd <= this.stepsTotal)
			stepsHoldingInTheEnd = steps;
		else
			throw new IllegalArgumentException("waitingSteps > steps");

	}
	
	public void setCentral(boolean c)
	{
		central = c;
	}

	@Override
	public boolean collidedWith(Sprite s)
	{
		return false;
	}

}
