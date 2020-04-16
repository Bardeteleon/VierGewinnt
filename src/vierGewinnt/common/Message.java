package vierGewinnt.common;

import java.util.Vector;

public class Message
{
	public String ip;
	public int type;
	public int command;
	public Vector<String> arguments;

	public Message()
	{
		arguments = new Vector<String>();
	}
	
	public String toString()
	{
		String s = "";
		s = "Type: " + type + "\nCommand: " + command + "\nARGS:\n";
		for(String arg : arguments)
		{
			s = s + arg + "\n";
		}
		return s;
	}
}
