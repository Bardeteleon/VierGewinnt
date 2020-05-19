package vierGewinnt.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChatHandler {

	private HashMap<String, Chat> chats;
	
	public ChatHandler()
	{
		chats = new HashMap<String, Chat>();
	}
	
	public Chat addNewChat(List<String> participants)
	{
		participants = sortSafely(participants);
		Chat chat = new Chat(participants);
		chats.put(participants.toString(), chat);
		return chat;
	}
	
	public Chat addNewChat(String key)
	{
		Chat chat = new Chat();
		chats.put(key, chat);
		return chat;
	}
	
//	public String addMessage(Vector<String> participants, String sender, String message)
//	{
//		Chat chat = getChat(participants);
//		chat.addMessage(sender, message);
//		return chat.getFormattedMessageLatest();
//	}
//	
//	public String getFormattedChat(Vector<String> participants)
//	{
//		return getChat(participants).getFormattedMessageAll();
//	}
//	
//	public String getFormattedChat(String key)
//	{
//		return getChat(key).getFormattedMessageAll();
//	}
//	
	public boolean isChat(List<String> participants)
	{
		participants = sortSafely(participants);
		return chats.containsKey(participants.toString());
	}
	
	public Chat getChat(List<String> participants)
	{
		participants = sortSafely(participants);
		Chat chat = chats.get(participants.toString());
		
		if(chat == null)
			System.out.println("ERROR: Chat with participants '" + participants.toString() + "' not found");
			
		return chat;
	}
	
	public Chat getChat(String key)
	{
		Chat chat = chats.get(key);
		if(chat == null)
			System.out.println("ERROR: Chat with key '" + key + "' not found");
			
		return chat;
	}
	
	public void setChatsInvisible()
	{
		for(String key : chats.keySet())
		{
			chats.get(key).setVisible(false);
		}
	}
	
	private List<String> sortSafely(List<String> list)
	{
		// do a copy to not alter the original list
		List<String> copyList = new LinkedList<String>(list);
		copyList.sort(null); // uses default compare of String
		return copyList;
	}
	
}
