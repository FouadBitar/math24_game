package server;

import java.rmi.*;
import java.util.ArrayList;
import client.Player;

public interface Card24Int extends java.rmi.Remote {
	
	ArrayList<Player> getPlayers() throws RemoteException;
	
	ArrayList<Player> getOnlinePlayers() throws RemoteException;
	
	ArrayList<String> getOnlinePlayerNames() throws RemoteException;
	
	Player login(String username, String password) throws RemoteException;
	
	void logout(Player player) throws RemoteException;
	
	Player register(String username, String password) throws RemoteException;
	
	public Player saveResult(Player user, boolean wongame, int sec) throws RemoteException;
	
}
