package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import client.Player;

public class Card24Register extends UnicastRemoteObject implements Card24Int {
	
	private static final long serialVersionUID = 1L;
	static Card24Register register;
	private Persistance persistance;
	public static boolean connectedToDatabase = false;
	
	public static void registerCard24Server() {
		try {
			register = new Card24Register();
			System.setSecurityManager(new SecurityManager());
			Naming.rebind("Card24Server", register);
			System.out.println("Service Registered");
		} catch(Exception e) {
			System.err.println("Exception thrown: " + e);
		}
	}

	private Card24Register() throws RemoteException {
		//connect to database
		try {
			persistance = new Persistance();
			persistance.clearOnlinePlayers();
			connectedToDatabase = true;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}
	
	public ArrayList<Player> getPlayers() throws RemoteException {
		return persistance.getPlayers();
	}
	
	public ArrayList<String> getOnlinePlayerNames() throws RemoteException {
		return persistance.getOnlinePlayerNames();
	}
	
	public ArrayList<Player> getOnlinePlayers() throws RemoteException {
		return persistance.getOnlinePlayers();
	}

	public Player login(String username, String password) throws RemoteException {
		//get player
		Player player = getPlayer(username);
		
		//check if player registered
		if(player == null) {
			throw new RemoteException("Player not registered");
		}
		//check if correct password
		if(!password.equals(player.password)) {
			throw new RemoteException("Incorrect password");
		}
		//check if user is already online
		for(String p : getOnlinePlayerNames()) {
			if(username.equals(p)) {
				throw new RemoteException("Player is already logged in");
			}
		}
		
		persistance.addOnlinePlayer(player.username);
		return player;
	}
	
	private Player getPlayer(String username) throws RemoteException {
		Player player = null;
		for(Player p : getPlayers())
			if(username.equals(p.username))
				return p;
		return player;
	}
	
	public void logout(Player player) throws RemoteException {
		//make sure player object exists and client isn't pointing to nothing
		if(player == null) {
			throw new RemoteException("Player object null");
		}
		//make sure player exists
		if(getPlayer(player.username) == null) {
			throw new RemoteException("Player is not registered");
		}
		//make sure player is online to take off
		boolean isOnline = false;
		for(String p : getOnlinePlayerNames()) {
			if(player.username.equals(p)) {
				persistance.removeOnlinePlayer(player);
				isOnline = true;
			}
		}
		if(!isOnline) {
			throw new RemoteException("Player not online");
		}
	}
	
	public Player register(String username, String password) throws RemoteException {
		//check username isn't taken
		if(getPlayer(username) != null) {
			throw new RemoteException("Username taken");
		}
		//create new player, add to file, add to online
		Player player = new Player(username, password, 0, 0, 0, 0, false);
		persistance.addPlayer(player);
		persistance.addOnlinePlayer(player.username);
		
		return player;
	}
	
	public Player saveResult(Player player, boolean isWinner, int timeTaken) throws RemoteException {
		if(player == null) {
			throw new RemoteException("player null");
		}
		
		boolean playerExists = false;
		for(Player p: getPlayers()) {
			if(p.username.equals(player.username)) {
				playerExists = true;
			}
		}
		if(!playerExists) throw new RemoteException("Player not found");
		
		boolean playerOnline = false;
		for(Player p: getOnlinePlayers()) {
			if(p.username.equals(player.username)) {
				playerOnline = true;
			}
		}
		if(!playerOnline) throw new RemoteException("Player not online");
		
		
		player.gamesPlayed++;
		if(isWinner) {
			player.wins++;
			player.winTimeSum += timeTaken;
		}
		
		try {
			persistance.updatePlayer(player);
			return persistance.getPlayer(player.username);
		} catch(Exception e) {
			throw new RemoteException("Error updating player", e);
		}
	}

}
