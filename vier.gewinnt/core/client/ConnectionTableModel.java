package core.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class ConnectionTableModel extends AbstractTableModel
{
	private String[] columnNames = new String[]{"Nickname", "Whisper"};
	private LinkedList<RowModel> tableList = new LinkedList<RowModel>();
	private LinkedList<RowModel> bufferList;
	private List<Integer> selectedWhisperRows = new LinkedList<Integer>();
	private boolean multiWhisperSelectionAllowed = false;

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
		switch(column)
		{
			case 0:
				tableList.get(row).setNick((String)value);
				break;
				
			case 1:
				boolean whisper = (boolean)value;
				if(whisper && !multiWhisperSelectionAllowed)
					removeAllWhispers();
				tableList.get(row).setWhisper(whisper);
				break;
		}
		fireTableCellUpdated(row, column);
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		return tableList.get(row).getColumn(column);
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == 1)
			return true;
		else
			return false;
	}

	public void setData(List<String> nicknames, String... filter)
	{
		bufferList = (LinkedList<RowModel>) tableList.clone();
		tableList.clear();
		if (nicknames != null)
			for (String nick : nicknames)
			{
				// check if already in the list and reuse old value to keep whisper selection
				boolean match = false;
				for (RowModel row : bufferList)
				{
					if (row.getNick().equals(nick))
					{
						tableList.add(row);
						match = true;
						break;
					}
				}
				
				// new connection
				if (!match)
					tableList.add(new RowModel(nick, false));
				
				// apply filter
				String newNickName = tableList.getLast().getNick();
				for(String filterName : filter)
				{
					if(newNickName.equals(filterName))
					{
						tableList.removeLast();
						break;
					}
				}
			}
		
		if(!(bufferList.size() == 0 && tableList.size() == 0))
		{	
			bufferList.clear();
			fireTableDataChanged();
		}
	}

	public Vector<String> getWhisperNames()
	{
		Vector<String> v = new Vector<String>();
		for (RowModel row : tableList)
		{
			if (row.isWhisper())
			{
				v.add(row.getNick());
			}
		}
		return v;
	}

	public Vector<String> getNames(String... filter)
	{
		Vector<String> names = new Vector<String>();
		for (RowModel row : tableList)
		{
			boolean match = false;
			if (filter != null)
			{
				for (String s : filter)
				{
					if (row.getNick().equals(s))
					{
						match = true;
						break;
					}
				}
			}
			if(!match)
				names.add(row.getNick());
		}
		return names;
	}
	
	public void removeAllWhispers()
	{
		for (int i = 0; i < tableList.size(); i++) {
			if(tableList.get(i).isWhisper())
			{
				tableList.get(i).setWhisper(false);
				fireTableCellUpdated(i, 1);
			}
		}
	}
	
	private class RowModel
	{
		private String nick;
		private boolean whisper;
		
		public RowModel(String nick, boolean whisper)
		{
			this.nick = nick;
			this.whisper = whisper;
		}

		public String getNick() {
			return nick;
		}

		public void setNick(String nick) {
			this.nick = nick;
		}

		public boolean isWhisper() {
			return whisper;
		}

		public void setWhisper(boolean whisper) {
			this.whisper = whisper;
		}
		
		public Object getColumn(int column)
		{
			switch(column)
			{
				case 0:
					return getNick();
				case 1:
					return isWhisper();
				default:
					return null;
			}
		}
		
	}
}
