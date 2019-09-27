package server;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import server.GameMessage.MessageAction;

/**
 * This is the executed class which initializes data, registers 
 * the service on the rmi registry and initializes jms variables. It
 * contains the main server thread and starts another thread to create
 * and maintain ongoing games
 */
public class Card24Server extends Thread  {
	
	private JMSHelper jmsHelper;
	private Card24Data data;
	private ScriptEngine engine;
	private MessageConsumer queueReciever;
	private MessageProducer topicSender;

	public static void main(String [] args) {
		Card24Server server = null;
		try {
			server = new Card24Server();	
		} catch (NamingException | JMSException e) {
			System.err.println("Program aborted."+e);
		} finally {
			if(server == null) return;
			Card24Register.registerCard24Server();
			if(!Card24Register.connectedToDatabase) return;	
			server.start();
		}
	}
	
	private Card24Server() throws NamingException, JMSException {
		jmsHelper = new JMSHelper();
		data = Card24Data.getData();
		engine =  new ScriptEngineManager().getEngineByName("javascript");
	}
	
	public void run() {
		try { 
			(new Card24GameController(this)).start();
			listen();
		} catch(JMSException e) {
			System.err.println("Program aborted."+e);
		}
	}
	

	public void listen() throws JMSException {
		
		System.out.println("Server Listening");
		
		queueReciever = jmsHelper.createQueueReciever();
		topicSender = jmsHelper.createTopicSender();
		
		while(true) {
			
			//attempt to receive a message if one was sent
			Message message = receiveMessage(queueReciever);
			GameMessage gameMessage = (GameMessage)((ObjectMessage)message).getObject();
			String messageSender = gameMessage.from;
			
			switch(gameMessage.action) {
			case RequestGame:
				//check player isn't in a game or in the request queue
				if(isValidRequest(messageSender)) {
					data.addPlayerRequest(new GameRequest(messageSender));
				}
				break;
				
			case SubmitAnswer:
				//check player is actually in game
				Game senderGame = null;
				for(Game game : data.getLiveGames()) {
					if(messageSender.equals(game.player1) || messageSender.equals(game.player2)) {
						senderGame = game;
					}
				}
				if(senderGame == null) break;
				
				
				//use java script engine to evaluate the expression
				String validationError = "noError";
				Integer result = 0;
				try {
					result = (Integer) engine.eval(gameMessage.message+";");
				} catch(ScriptException | ClassCastException e) {
					validationError = e.getMessage();
				}
				
				//get opponent user name
				String opponent = senderGame.player1;
				if(opponent.equals(messageSender)) {
					opponent = senderGame.player2;
				}
				
				//answer correct, notify players and remove game 
				if(result == 24) {
					GameMessage gm = new GameMessage(messageSender, 
							opponent, gameMessage.message, MessageAction.GameResults);
					broadcastMessage(gm);
					data.removeGame(senderGame);
				} 
				// answer incorrect, send back result
				else {
					GameMessage gm = new GameMessage(messageSender, null, 
							result + ";"+validationError, MessageAction.AnswerIncorrect);
					broadcastMessage(gm);
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	private boolean isValidRequest(String username) {
		for(Game game : data.getLiveGames()) {
			if(username.equals(game.player1) || username.equals(game.player2)) {
				return false;
			}
		}
		for(GameRequest request : data.getGameRequests()) {
			if(username.equals(request.username)) {
				return false;
			}
		}
		return true;
	}
	
	private Message receiveMessage(MessageConsumer queueReciever) throws JMSException {
		try {
			Message jmsMessage = queueReciever.receive();
			return jmsMessage;
		} catch(JMSException e) {
			System.err.println("Failed to recieve message: " + e);
			throw e;
		}
	}
	
	public void broadcastMessage(GameMessage gm) {
		try {
			Message msg = jmsHelper.createMessage(gm);
			if(gm.to != null) {
				msg.setStringProperty("privateMessageTo", gm.to);
			}
			msg.setStringProperty("privateMessageFrom", gm.from);
			topicSender.send(msg);
		} catch (JMSException e) {
			System.err.println("Failed to broadcast message: " + e);
		}
	}
	

	
}
