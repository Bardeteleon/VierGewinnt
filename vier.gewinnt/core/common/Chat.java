package core.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import core.server.User;

public class Chat {

	private List<User> participants;
	private List<ChatMessage> history;
	private boolean visible;
	
	public Chat()
	{
		this.participants = new Vector<User>();
		this.history = new Vector<ChatMessage>();
		this.visible = false;
	}
	
	public Chat(List<String> participants)
	{
		this.participants = getDummyUsers(participants);
		this.history = new LinkedList<ChatMessage>();
		this.visible = false;
	}
	
	public void addParticipant(User user)
	{
		if(!isParticipant(user))
			participants.add(user);
		else
			System.out.println("ERROR: User '" + user.getNick() + "' is already participant");
	}
	
	public void addParticipant(String nick)
	{
		addParticipant(getDummyUser(nick));
	}
	
	public void addParticipants(Vector<String> nicks)
	{
		for (String nick : nicks) {
			addParticipant(nick);
		}
	}
	
	public boolean removeParticipant(User user)
	{
		for (int i = 0; i < participants.size(); i++)
			if(participants.get(i).getNick().equals(user.getNick()))
			{
				participants.remove(i);
				return true;
			}
		return false;
		
	}
	
	public boolean removeParticipant(String nick)
	{
		return removeParticipant(getDummyUser(nick));
	}
	
	public void removeAllParticipants()
	{
		this.participants = new Vector<User>();
	}
	
	public boolean isParticipant(User user)
	{
		for(User tmp : participants)
			if(tmp.getNick().equals(user.getNick()))
				return true;

		return false;
	}
	
	public boolean isParticipant(String nick)
	{
		return isParticipant(getDummyUser(nick));
	}
	
	public String addMessage(User sender, String message)
	{
		if(isParticipant(sender))
		{
			history.add(new ChatMessage(sender, message));
			return getFormattedMessageLatest();
		}else
		{
			System.out.println("ERROR: Sender (" + sender.getNick() + ") is not a participant of this chat");
			return "";
		}
	}
	
	public String addMessage(String sender, String message)
	{
		return addMessage(getDummyUser(sender), message);
	}
	
	public String getFormattedMessageAll()
	{
		String result = "";
		
		for(ChatMessage msg : history)
		{
			result = result + msg.toString() + "\n";
		}
				
		return result;
	}
	
	public String getFormattedMessageLatest()
	{
		return history.get(history.size()-1).toString() + "\n";
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	private User getDummyUser(String nick)
	{
		User tmp = new User("unknown", 0000);
		tmp.setNick(nick);
		return tmp;
	}
	
	private List<User> getDummyUsers(List<String> nicks)
	{
		List<User> result = new LinkedList<User>();
		for(String nick : nicks)
		{
			result.add(getDummyUser(nick));
		}
		return result;
	}
	
	
}
