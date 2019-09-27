package server;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class maintains data for the running server 
 * Stores the game requests and the active games
 * Multiple classes access this data, locking is used
 */
public class Card24Data {

	private static Card24Data data = null;

	private static Lock lock;

	private volatile Queue<GameRequest> gameRequests;
	private volatile ArrayList<Game> liveGames;
	
	private Card24Data() {
		gameRequests = new LinkedList<GameRequest>();
		liveGames = new ArrayList<Game>();
		
		lock = new ReentrantLock(true);
	}
	
	//access synchronized, ensures only one is created
	public synchronized static Card24Data getData() {
		if(data != null) {
			return data;
		} else {
			data = new Card24Data();
			return data;
		}
	}
	
	// gets copy of live game array list
	public ArrayList<Game> getLiveGames(){
		ArrayList<Game> copyLiveGames = new ArrayList<Game>();
		lock.lock();
		try {
			for(Game game : liveGames) {
				copyLiveGames.add(game);
			}
		} catch(Exception e) {
		} finally {
			lock.unlock();
		}	
		return copyLiveGames;
	}
	
	// gets copy of game request array list
	public Queue<GameRequest> getGameRequests(){
		Queue<GameRequest> copyGameRequests = new LinkedList<GameRequest>();
		lock.lock();
		try {
			for(GameRequest request : gameRequests) {
				copyGameRequests.add(request);
			}
		} catch(Exception e) {
		} finally {
			lock.unlock();
		}	
		return copyGameRequests;
	}
	
	public void addPlayerRequest(GameRequest request) {
		lock.lock();
		try {
			gameRequests.add(request);
		} catch(Exception e) {
		} finally {
			lock.unlock();
		}
	}
	
	public void removePlayerRequest(String player) {
		Queue<GameRequest> newRequests = new LinkedList<GameRequest>();
		lock.lock();
		try {
			for(GameRequest request : gameRequests) {
				if(!request.username.equals(player)) {
					newRequests.add(request);
				}
			}
			gameRequests = newRequests;
		} catch (Exception e) { 
		}
		finally {
			lock.unlock();
		}
	}
	
	public void addGame(Game game) {
		lock.lock();
		try {
			liveGames.add(game);
		} catch(Exception e) {
		} finally {
			lock.unlock();
		}
	}
	
	public void removeGame(Game game) {
		lock.lock();
		try {
			liveGames.remove(game);
		} catch(Exception e) {	
		} finally {
			lock.unlock();
		}
	}
	
	public void removeFirstTwoRequests() {
		lock.lock();
		try {
			gameRequests.remove();
			gameRequests.remove();
		} catch(Exception e) {
		} finally {
			lock.unlock();
		}
	}
}
