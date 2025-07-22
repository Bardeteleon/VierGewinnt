package core.common;

public enum MessageCommand {

	// Game
	INSERT, INSERTSTATUS, GAMESTART, GAMEEND, EXPLOSION, LOG,
	
	// Lobby
	USERTABLE, INVITE, INVITATIONANSWER,
	
	// Chat
	ALL, WHISPER,
	
	ERROR
	
}
