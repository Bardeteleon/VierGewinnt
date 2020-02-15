package useful;

import java.awt.Color;

import javax.swing.JColorChooser;

public class ColorChooser
{
	public static void main(String[] args)
	{
		JColorChooser c  = new JColorChooser();
		Color color = c.showDialog(null, "Colors", Color.BLUE);
		System.out.println(color);
	}
}
