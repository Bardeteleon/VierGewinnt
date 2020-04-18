package vierGewinnt.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import vierGewinnt.common.Chip;

public class GameCellRenderer extends JLabel implements TableCellRenderer
{

	private JTable table;
	private GameTableModel model;
	private ImageIcon icon;
	private JPanel panBuff;
	double rows;
	double columns;
	double width;
	double height;
	double widthHeightCell;

	public GameCellRenderer(JPanel panBuffSpielFeld)
	{
		panBuff = panBuffSpielFeld;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (table.getModel() instanceof GameTableModel)
		{
			model = (GameTableModel) table.getModel();
			rows = model.getRowCount();
			columns = model.getColumnCount();
		}
		if (   hasFocus 
			&& model.getChipTypeAt(column, row) == Chip.EXPLOSIVE)
		{
			setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		} else
		{
			setBorder(BorderFactory.createEmptyBorder());
		}
		this.table = table;
		if (value instanceof ImageIcon)
		{
			icon = (ImageIcon) value;
			return this;
		} else
			return null;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (table != null && icon != null)
		{
			width = panBuff.getWidth();
			height = panBuff.getHeight();
			widthHeightCell = 0;
			if ((rows / columns) < (height / width))
			{
				widthHeightCell = width / columns;
			} else
				widthHeightCell = height / rows;
			g.drawImage(icon.getImage(), 0, 0, (int) widthHeightCell, (int) widthHeightCell, this);
		}
	}

	public void setAktSize()
	{
		if (table != null)
		{
			table.setPreferredSize(new Dimension((int) (widthHeightCell * columns), (int) (widthHeightCell * rows)));
			table.setRowHeight((int) widthHeightCell);
		}
	}

}
