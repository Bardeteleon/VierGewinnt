package vierGewinnt.common;

import java.time.LocalDateTime;

import vierGewinnt.server.User;

public class ChatMessage {

	private LocalDateTime date;
	private User sender;
	private String message;
	
	public ChatMessage(User sender, String message)
	{
		date = LocalDateTime.now();
		this.sender = sender;
		this.message = message;
	}
	
	public ChatMessage(String senderNick, String message)
	{
		date = LocalDateTime.now();
		this.sender = new User("uknown", 0000);
		this.sender.setNick(senderNick);
		this.message = message;
	}
	
	public LocalDateTime getLocalDateTime()
	{
		return date;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getSenderNick()
	{
		return sender.getNick();
	}
	
	public User getSender()
	{
		return sender;
	}
	
	public String toString()
	{
		return sender.getNick() + ": " + message;
	}
}
