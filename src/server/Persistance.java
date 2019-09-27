package server;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import client.Player;


/**
 * This is the class that connects to the database and 
 * is used to create, list, update, and remove from the database.
 * The database used is a free remote mysql database, the credentials
 * are below.
 */
public class Persistance {
	
	//database keys for connection
	private static final String DB_HOST = "remotemysql.com:3306";
	private static final String DB_USER = "YjH8RhOcJo";
	private static final String DB_PASS = "zJYvGsklGX";
	private static final String DB_NAME = "YjH8RhOcJo";
	
	private Connection conn;
	
	public Persistance() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection("jdbc:mysql://"+DB_HOST+
				"/"+DB_NAME+
				"?user="+DB_USER+
				"&password="+DB_PASS);
		System.out.println("Database connection successful.");
	}
	
	public void addPlayer(Player player) {	
		try {
			PreparedStatement stmt = 
					conn.prepareStatement("INSERT INTO Player "
							+ "(username, password, gamesPlayed, gamesWon, winTimeSum, isOnline) "
							+ "VALUES (?, ?, ?, ?, ?, ?)");
			stmt.setString(1, player.username);
			stmt.setString(2, player.password);
			stmt.setInt(3, 0);
			stmt.setInt(4, 0);
			stmt.setInt(5, 0);
			stmt.setBoolean(6, false);
			stmt.execute();
			System.out.println("Record created");
		} catch (SQLException | IllegalArgumentException e) {
			System.err.println("Error inserting player: " + e);
		}
	}
	
	public Player getPlayer(String player) {
		ArrayList<Player> players = getPlayers();
		
		for(Player p: players) {
			if(p.username.equals(player)) {
				return p;
			}
		}
		return null;
	}
	
	public ArrayList<Player> getPlayers(){
		ArrayList<Player> players = new ArrayList<Player>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT username, password, gamesPlayed, gamesWon, winTimeSum, isOnline FROM Player");
			while(rs.next()) {
				players.add(new Player(rs.getString(1), rs.getString(2), 
						rs.getInt(3), rs.getInt(4), rs.getInt(5), -1, rs.getBoolean(6)));
			}
			//maybe calculate rank here
		} catch (SQLException e) {
			System.err.println("Error getting players: " + e);
		}
		return players;
	}
	
	
	public ArrayList<String> getOnlinePlayerNames(){
		ArrayList<String> onlinePlayers = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT username FROM Player WHERE isOnline = true");
			while(rs.next()) {
				onlinePlayers.add(rs.getString(1));
			}
		} catch (SQLException e) {
			System.err.println("Error getting online players: " + e);
		}
		return onlinePlayers;
	}
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> onlinePlayers = new ArrayList<Player>();
		for(Player player : getPlayers()) {
			if(player.isOnline) {
				onlinePlayers.add(player);
			}
		}	
		return onlinePlayers;
	}
	
	
	public void addOnlinePlayer(String player) {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE Player SET isOnline = true WHERE username = ?");
			stmt.setString(1, player);	
			int rows = stmt.executeUpdate();
			if(rows > 0) {
				System.out.println(player + " is now online ");
			} else {
				System.out.println(player + " was not found!");
			}
		} catch (SQLException e) {
			System.err.println("Error adding online player: " + e);
		}
	}
	
	public void removeOnlinePlayer(Player player) {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE Player SET isOnline = false WHERE username = ?");
			stmt.setString(1, player.username);	
			int rows = stmt.executeUpdate();
			if(rows > 0) {
				System.out.println(player.username + " is now offline");
			} else {
				System.out.println(player + " was not found!");
			}
		} catch (SQLException e) {
			System.err.println("Error removing online player: " + e);
		}
	}
	
	public void clearOnlinePlayers() {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE Player SET isOnline = false");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error taking all players offline: " + e);
		}
	}
	
	public void updatePlayer(Player player) throws Exception {
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE Player SET " + "gamesPlayed = ?, gamesWon = ?, winTimeSum = ? WHERE username = ?");
			stmt.setInt(1, player.gamesPlayed);
			stmt.setInt(2, player.wins);
			stmt.setDouble(3, player.winTimeSum);
			stmt.setString(4, player.username);
			
			int rows = stmt.executeUpdate();
			if(rows > 0) {
				System.out.println(player.username + " updated");
			} else {
				System.out.println(player.username + " not found");
			}
		} catch (SQLException e) {
			throw new Exception("error updating player", e);
		}
	}

}
