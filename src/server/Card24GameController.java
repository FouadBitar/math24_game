package server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import client.Player;
import server.GameMessage.MessageAction;

/**
 * This class is the thread responsible for starting and maintaining games,
 * it accesses data from Card24Data and if there are two or more requests 
 * starts a game and notifies the players.
 */
public class Card24GameController extends Thread {

	private Card24Data data;
	private Card24Server server;
	private Card24Register register;
	
	public Card24GameController(Card24Server server) {
		this.server = server;
		this.data = Card24Data.getData();
		this.register = Card24Register.register;
	}
	
	public void run() {
		Queue<GameRequest> gameRequests;
		long currentTime = new Date().getTime();
		
		
		while(true) {
			//get latest request queue
			gameRequests = data.getGameRequests();
			
			//if there are two or more requests, start a game and notify players
			if(gameRequests.size() > 1) {
				if(isXSecondsPast(gameRequests.peek().timeOfRequest, 10)) {
					String player1 = gameRequests.remove().username;
					String player2 = gameRequests.remove().username;
					Game newGame = new Game(player1, player2);
					data.addGame(newGame);
					data.removeFirstTwoRequests();;
					
					server.broadcastMessage(
							new GameMessage(player1, player2, newGame.toString(), MessageAction.GameFound));
				}
			} 
			
			// Remove offline players from queue
			removeRequestsWithOfflinePlayer();
			
			// Check for games with offline players
			if(isXSecondsPast(currentTime, 5)) {
				removeGamesWithOfflinePlayers();
				currentTime = new Date().getTime();
			}
			
			// Sleep to reduce blocking
			try { Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
	}
	
	private void removeRequestsWithOfflinePlayer() {
		Queue<GameRequest> gameRequests = data.getGameRequests();
		for(GameRequest request : gameRequests) {
			if(!isPlayerOnline(request.username)) {
				data.removePlayerRequest(request.username);
			}
		}
	}

	private void removeGamesWithOfflinePlayers() {
		//if player 1 is offline, send message to player 2 and disconnect game
		for(Game game : data.getLiveGames()) {
			if(!isPlayerOnline(game.player1)) {  
				server.broadcastMessage(new GameMessage(game.player2, null, "", MessageAction.GameDisconnected));
				data.removeGame(game);
			} else if (!isPlayerOnline(game.player2)) {
				server.broadcastMessage(new GameMessage(game.player1, null, "", MessageAction.GameDisconnected));
				data.removeGame(game);
			}
		}
	}
	
	private boolean isPlayerOnline(String username) {
		ArrayList<Player> onlinePlayers = null;
		try {
			onlinePlayers = register.getOnlinePlayers();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		for(Player player : onlinePlayers) {
			if(username.equals(player.username)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isXSecondsPast(long timeOfRequest, int seconds) {
		int timeTaken = (int) (((new Date()).getTime() - timeOfRequest)/1000);
		return (timeTaken >= seconds) ? true : false;
	}
	
}
