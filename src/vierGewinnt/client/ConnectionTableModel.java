package vierGewinnt.client;

import java.util.LinkedList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class ConnectionTableModel extends AbstractTableModel
{
	private String[] columnNames = new String[]{"Nickname", "Whisper"};
	private LinkedList<Object[]> tableList = new LinkedList<Object[]>();
	private LinkedList<Object[]> bufferList;

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public int getRowCount()
	{
		return tableList.size();
	}

	@Override
	public Class getColumnClass(int column)
	{
		if (column == getColumnCount() - 1)
			return Boolean.class;
		else
			return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		Object[] s = new Object[2];
		if (column == 0)
		{
			s = new Object[]{value, getValueAt(row, 1)};
		} else if (column == 1)
		{
			s = new Object[]{getValueAt(row, 0), value};
		} else
			return;
		tableList.remove(row);
		tableList.add(row, s);
	}

	@Override
	public Object getValueAt(int rowModel, int columnModel)
	{
		return tableList.get(rowModel)[columnModel];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == 1)
			return true;
		else
			return false;
	}

	public void setData(Vector<String> connections, String... filter)
	{
		bufferList = (LinkedList<Object[]>) tableList.clone();
		tableList.clear();
		if (connections != null)
			for (String s : connections)
			{
				boolean match = false;
				for (Object[] str : bufferList)
				{
					if (str[0].equals(s))
					{
						tableList.add(str);
						match = true;
						break;
					}
				}
				if (!match)
					tableList.add(new Object[]{s, new Boolean(false)});
				String probFilterName = (String) tableList.getLast()[0];
				for(String filterNames : filter)
				{
					if(probFilterName.equals(filterNames))
					{
						tableList.removeLast();
						break;
					}
				}
			}
		bufferList.clear();
		fireTableDataChanged();
	}

	public Vector<String> getWhisperNames()
	{
		Vector<String> v = new Vector<String>();
		for (Object[] o : tableList)
		{
			if ((Boolean) o[1])
			{
				v.add((String) o[0]);
			}
		}
		return v;
	}

	public Vector<String> getNames(String... filter)
	{
		Vector<String> o = new Vector<String>();
		for (int i = 0; i < tableList.size(); i++)
		{
			String name = (String) getValueAt(i, 0);
			boolean match = false;
			if (filter != null)
			{
				for (String s : filter)
				{
					if (name.equals(s))
					{
						match = true;
						break;
					}
				}
			}
			if(!match)
				o.add(name);
		}
		return o;
	}
}
