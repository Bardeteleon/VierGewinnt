package vierGewinnt.client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import useful.GUI;
import useful.Stopuhr;
import vierGewinnt.server.GameControl;

public class GameTableModel extends AbstractTableModel
{
	private ImageIcon[][] playingField;
	private HashMap<String, ImageIcon> icons;
	private Vector<Animation> animations;
	protected AnimationHandler aniHandler;
	private int animationDelay = 50;

	protected static final String LEER = "leer";
	private static final String RED_O_1 = "red0";
	private static final String RED_O_2 = "red01";
	private static final String RED_O_B = "red0B";
	private static final String RED_B = "redB";
	private static final String RED_1 = "red1";
	private static final String RED_2 = "red2";
	private static final String RED_3 = "red3";
	private static final String YEL_O_1 = "yel0";
	private static final String YEL_O_2 = "yel01";
	private static final String YEL_O_B = "yel0B";
	private static final String YEL_B = "yelB";
	private static final String YEL_1 = "yel1";
	private static final String YEL_2 = "yel2";
	private static final String YEL_3 = "yel3";

	private int chipPos = -1;
	private int choosingChipColor;
	private boolean bomb = false;
	private int bombs = 0;
	private int ownColor = 0;

	public GameTableModel(int column, int row, int bombs, int ownColor)
	{
		this.bombs = bombs;
		this.ownColor = ownColor;

		icons = new HashMap<String, ImageIcon>();
		icons.put(LEER, GUI.createImageIcon(this, "bilder/VAR1/leer.jpg"));
		icons.put(RED_O_1, GUI.createImageIcon(this, "bilder/VAR1/o_red.jpg"));
		icons.put(RED_O_2, GUI.createImageIcon(this, "bilder/VAR1/o_red_pos1.jpg"));
		icons.put(RED_O_B, GUI.createImageIcon(this, "bilder/VAR1/o_B_red.jpg"));
		icons.put(RED_B, GUI.createImageIcon(this, "bilder/VAR1/B_red.jpg"));
		icons.put(RED_1, GUI.createImageIcon(this, "bilder/VAR1/red_pos1.jpg"));
		icons.put(RED_2, GUI.createImageIcon(this, "bilder/VAR1/red_pos2.jpg"));
		icons.put(RED_3, GUI.createImageIcon(this, "bilder/VAR1/red_pos3.jpg"));
		icons.put(YEL_O_1, GUI.createImageIcon(this, "bilder/VAR1/o_yel.jpg"));
		icons.put(YEL_O_2, GUI.createImageIcon(this, "bilder/VAR1/o_yel_pos1.jpg"));
		icons.put(YEL_O_B, GUI.createImageIcon(this, "bilder/VAR1/o_B_yel.jpg"));
		icons.put(YEL_B, GUI.createImageIcon(this, "bilder/VAR1/B_yel.jpg"));
		icons.put(YEL_1, GUI.createImageIcon(this, "bilder/VAR1/yel_pos1.jpg"));
		icons.put(YEL_2, GUI.createImageIcon(this, "bilder/VAR1/yel_pos2.jpg"));
		icons.put(YEL_3, GUI.createImageIcon(this, "bilder/VAR1/yel_pos3.jpg"));

		playingField = new ImageIcon[row + 1][column]; // row+1 -> eine Zeile
														// zum wählen
		clearPlayingField();
		clearChoosingField();

		animations = new Vector<Animation>();
		aniHandler = new AnimationHandler(column, row);
	}

	@Override
	public int getColumnCount()
	{
		return playingField[0].length;
	}

	@Override
	public int getRowCount()
	{
		return playingField.length;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		return playingField[row][column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return Icon.class;

	}

	public void clearPlayingField()
	{
		for (int i = 1; i < playingField.length; i++)
			for (int j = 0; j < playingField[i].length; j++)
			{
				playingField[i][j] = icons.get(LEER);
			}
		update();
	}

	public void clearChoosingField()
	{
		for (int i = 0; i < playingField[0].length; i++)
			playingField[0][i] = null;
		update();
	}

	protected void setIconAt(String icon, int columnIndex, int rowIndex)
	{
		rowIndex++;
		playingField[rowIndex][columnIndex] = icons.get(icon);
		update();
	}

	public void setChipAnimatedAt(int spieler, boolean bomb, int column, int fromRow, int toRow)
	{
		animations.add(new Animation(spieler, bomb, column, fromRow, toRow, false));
		aniHandler.waikUp();
	}

	public void setChipAnimatedAt(int spieler, boolean bomb, int column, int row)
	{
		if (bomb)
		{
			if (spieler == ownColor)
				if (bombs > 0)
				{
					bombs--;
					setChooserChipToBomb(false);
				} else
					bomb = false;
		}
		animations.add(new Animation(spieler, bomb, column, 0, row, true));
		aniHandler.waikUp();
	}

	private class Animation extends Thread
	{
		int column;
		int fromRow;
		int toRow;
		int spieler;
		boolean first;
		boolean bomb;
		Stopuhr s;
		long deleteAfter = 10000;

		public Animation(int spieler, boolean bomb, int column, int fromRow, int toRow, boolean first)
		{
			setName("Animation: Column " + column + " fromRow: " + fromRow + " toRow: " + toRow + " Spieler: " + spieler + " Bomb: " + bomb + " First: " + first);
			this.column = column;
			this.fromRow = fromRow;
			this.toRow = toRow;
			this.spieler = spieler;
			this.first = first;
			this.bomb = bomb;
			s = new Stopuhr();
		}
		@Override
		public void run()
		{
			if (spieler == GUIVierGewinnt.RED)
				if (bomb)
					animate(RED_1, RED_B, RED_3, RED_O_2);
				else
					animate(RED_1, RED_2, RED_3, RED_O_2);
			else if (spieler == GUIVierGewinnt.YEL)
				if (bomb)
					animate(YEL_1, YEL_B, YEL_3, YEL_O_2);
				else
					animate(YEL_1, YEL_2, YEL_3, YEL_O_2);

		}
		private void animate(String pos1, String pos2, String pos3, String oPos2)
		{
			if (first)
			{
				setIconAt(oPos2, column, -1);
				setIconAt(pos1, column, 0);
				waitingFor(animationDelay);
				setIconAt(null, column, -1);
				setIconAt(pos2, column, 0);
				waitingFor(animationDelay);
			}

			for (int i = fromRow; i < toRow; i++)
			{
				setIconAt(pos3, column, i);
				setIconAt(pos1, column, i + 1);
				waitingFor(animationDelay);
				setIconAt(LEER, column, i);
				setIconAt(pos2, column, i + 1);
				waitingFor(animationDelay);
			}
			aniHandler.setColumnCheck(column, true);
		}

		public boolean isOutdated()
		{
			return s.getTime() > deleteAfter;
		}
		@Override
		public String toString()
		{
			return "Animation: Column " + column + " fromRow: " + fromRow + " toRow: " + toRow + " Spieler: " + spieler + " Bomb: " + bomb + " First: " + first;
		}
	}

	private class AnimationHandler extends Thread
	{
		volatile boolean[] columnCheck;
		boolean stop = false;
		Stopuhr s;
		volatile long delay = 0;

		public AnimationHandler(int columns, int row)
		{
			setName("AnimationHandler");
			columnCheck = new boolean[columns];
			for (int i = 0; i < columnCheck.length; i++)
			{
				columnCheck[i] = true;
			}
			setDaemon(true);
			start();
		}

		@Override
		public void run()
		{
			while (!stop)
			{
				if (animations.size() > 0 && delay <= 0)
				{
					for (int i = 0; i < animations.size(); i++)
					{
						Animation a = animations.get(i);
						if (columnCheck[a.column] && getFreeRow(a.column) == a.toRow)
						{
							setColumnCheck(a.column, false);
							animations.remove(i);
							a.start();
						} else if (a.isOutdated())
						{
							animations.remove(i);
						}
					}
					waitingFor(animationDelay / 5);
				} else
				{
					synchronized (this)
					{

						try
						{
							if (delay > 0)
							{
								wait(delay);
								if (s.getTime() < delay)
								{
									delay -= s.getTime();
									s.start();
								} else
									delay = 0;
							} else
							{
								wait();
							}
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

		public synchronized void waikUp()
		{
			notify();
		}

		public void destroy()
		{
			stop = true;
		}

		public void delay(long millis)
		{
			delay = millis;
			s = new Stopuhr();
		}

		public void setColumnCheck(int column, boolean b)
		{
			columnCheck[column] = b;
		}
	}

	protected int getFreeRow(int column)
	{
		for (int j = playingField.length - 1; j > 0; j--)
		{
			if (playingField[j][column].equals(icons.get(LEER)))
			{
				return j - 1;
			}
		}
		return -1;
	}

	public void waitingFor(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void setChipAt(int spieler, boolean bomb, int columnIndex, int rowIndex)
	{
		if (bomb)
		{
			if (spieler == ownColor)
				if (bombs > 0)
				{
					bombs--;
					setChooserChipToBomb(false);
				} else
					bomb = false;
		}
		if (spieler == GUIVierGewinnt.RED)
			if (bomb)
				setIconAt(RED_B, columnIndex, rowIndex);
			else
				setIconAt(RED_2, columnIndex, rowIndex);
		else if (spieler == GUIVierGewinnt.YEL)
			if (bomb)
				setIconAt(YEL_B, columnIndex, rowIndex);
			else
				setIconAt(YEL_2, columnIndex, rowIndex);

	}

	public void setChooseChipRight()
	{
		if (chipPos >= 0)
			if (chipPos + 1 < getColumnCount())
			{
				chipPos++;
				setChooseChipAt(chipPos);
			} else
			{
				chipPos = 0;
				setChooseChipAt(chipPos);
			}
	}

	public void setChooseChipLeft()
	{
		if (chipPos >= 0)
			if (chipPos > 0)
			{
				chipPos--;
				setChooseChipAt(chipPos);
			} else
			{
				chipPos = getColumnCount() - 1;
				setChooseChipAt(chipPos);
			}
	}

	private void setChooseChipAt(int column)
	{
		clearChoosingField();
		if (choosingChipColor == GUIVierGewinnt.RED)
			if (bomb)
				playingField[0][column] = icons.get(RED_O_B);
			else
				playingField[0][column] = icons.get(RED_O_1);
		else if (choosingChipColor == GUIVierGewinnt.YEL)
			if (bomb)
				playingField[0][column] = icons.get(YEL_O_B);
			else
				playingField[0][column] = icons.get(YEL_O_1);
		update();
	}

	public void setChooseChipEnable(boolean b, int color)
	{
		if (b)
		{
			if (chipPos < 0)
			{
				chipPos = 0;
				choosingChipColor = color;
				setChooseChipAt(chipPos);
			}
		} else
		{
			chipPos = -1;
			choosingChipColor = 0;
			clearChoosingField();
		}
	}

	private void update()
	{
		// wird eigentlich schon vom EDT ausgeführt, aber setChipAnimatedAt ruft
		// diese Methode von externen Thread aus auf, deswegen Update Methode
		// vom EDT ausführen lassen
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				fireTableDataChanged();
			}
		});
	}

	public boolean isChooseChipEnable()
	{
		return chipPos >= 0;
	}

	public int getChoosingChipPos()
	{
		return chipPos;
	}

	public boolean isBombChoosingChip()
	{
		return bomb;
	}

	public boolean isBombPosition(int column, int row)
	{
		if (icons.get(RED_B).equals(playingField[row][column]) || icons.get(YEL_B).equals(playingField[row][column]))
		{
			return true;
		} else
			return false;
	}

	public void setChooserChipToBomb(boolean enable)
	{
		if (bombs > 0)
			if (enable)
				bomb = true;
			else
				bomb = false;
		else
			bomb = false;
		if (isChooseChipEnable())
			setChooseChipAt(chipPos);
	}

	public Vector<Point> getChipPositionsOver(int column, int row)
	{
		row++;
		Vector<Point> points = new Vector<Point>();
		for (int i = row - 1; i >= 1; i--)
		{
			if (!playingField[i][column].equals(icons.get(LEER)))
			{
				points.add(new Point(column, i - 1));
			}
		}
		return points;
	}

	public int getSpielerColorAt(int column, int row)
	{
		if (playingField[row + 1][column].equals(icons.get(RED_2)) || playingField[row + 1][column].equals(icons.get(RED_B)))
			return GUIVierGewinnt.RED;
		else if (playingField[row + 1][column].equals(icons.get(YEL_2)) || playingField[row + 1][column].equals(icons.get(YEL_B)))
			return GUIVierGewinnt.YEL;
		return -1;
	}

	public void stopAnimationHandler()
	{
		aniHandler.destroy();
		aniHandler.waikUp();
	}

	public void delayAnimationHandler(long millis)
	{
		aniHandler.delay(millis);
	}

	public static int bombToChipType(boolean bomb)
	{
		if (bomb)
			return GameControl.EXPLOSIVCHIP;
		else
			return GameControl.NORMALCHIP;
	}

	public static boolean chipTypeToBomb(int chipType)
	{
		if (chipType == GameControl.EXPLOSIVCHIP)
			return true;
		else
			return false;

	}
}
