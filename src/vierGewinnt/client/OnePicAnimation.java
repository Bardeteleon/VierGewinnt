package vierGewinnt.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class OnePicAnimation extends Sprite
{
	boolean central;
	int steps;
	int currentStep = 0;
	int waitingSteps;

	public OnePicAnimation(BufferedImage[] i, JComponent parent, int steps, long delay)
	{
		super(i, 0, 0, delay, parent);
		this.steps = steps;
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
		if (currentStep <= steps - waitingSteps)
		{
			double heightByFullWidth = parent.getWidth() * (height / width);
			double widthByFullHeight = parent.getHeight() * (width / height);
			if (heightByFullWidth <= parent.getHeight())
			{
				int newWidth = currentStep * (parent.getWidth() / (steps - waitingSteps));
				height = newWidth * (height / width);
				width = newWidth;
			} else if (widthByFullHeight <= parent.getWidth())
			{
				int newHeight = currentStep * (parent.getHeight() / (steps - waitingSteps));
				width = newHeight * (width / height);
				height = newHeight;

			} else
				System.out.println("OnePicAni doLogic Error");
		}
	}

	@Override
	public void computeAnimation()
	{
		super.computeAnimation();
		if (currentStep <= steps)
		{
			currentStep++;
		} else
			remove = true;
	}

	public void reset()
	{
		super.reset();
		currentStep = 0;
	}

	public void setWaitingSteps(int steps)
	{
		if (waitingSteps <= this.steps)
			waitingSteps = steps;
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
