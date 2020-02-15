package vierGewinnt;

import java.util.Vector;

public class DataPackage
{
	public String ip;
	public int messageType;
	public int command;
	public Vector<String> arguments;

	public DataPackage()
	{
		arguments = new Vector<String>();
	}
	
	public String toString()
	{
		String s = "";
		s = "MessageType: " + messageType + "\nCommand: " + command + "\nARGS:\n";
		for(String str : arguments)
		{
			s = s + str + "\n";
		}
		return s;
	}
}
