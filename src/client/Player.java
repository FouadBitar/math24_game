package client;

import java.io.Serializable;


public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String username, password;
	public int gamesPlayed, wins, rank;
	public double winTimeSum;
	public boolean isOnline;
	
	
	public Player(String usr, String pswd, int gp, int w, double wts, int r, boolean online) {
		username = usr;
		password = pswd;
		gamesPlayed = gp;
		wins = w;
		winTimeSum = wts;
		rank = r;
		isOnline = online;
	}
}
