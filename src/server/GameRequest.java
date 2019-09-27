package server;

import java.util.Date;

/**
 * This class stores a request made by a player,
 * stores time of request so as to wait 10 seconds.
 */
public class GameRequest {
	
	public String username;
	public long timeOfRequest;
	
	public GameRequest(String playerUsername) {
		this.username = playerUsername;
		this.timeOfRequest = (new Date()).getTime();
	}
}
