package server;

import java.io.Serializable;

/**
 * This class is used to send messages from server to client and vice
 * versa, contains specific enum to specify the action.
 */
public class GameMessage implements Serializable {

	private static final long serialVersionUID = -1675867563027817666L;
	public enum MessageAction {
			RequestGame,	
			SubmitAnswer,
			GameFound,
			GameResults,
			AnswerIncorrect,
			GameDisconnected
	};
	
	public String from, to, message;
	public MessageAction action;
	
	public GameMessage(String from, String to, String message, MessageAction action) {
		this.from = from;
		this.to = to;
		this.message = message;
		this.action = action;
	}
}
