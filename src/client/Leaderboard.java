package client;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Leaderboard extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final String[] COLUMN_NAMES = {"Rank" ,"Player", "Games Won", "Games Played", "Avg. Winning Time"};
	
	public static Leaderboard leaderboard;
	
	public Leaderboard() {
		update();
	}
	
	public void update() {
		removeAll();
		try {
			ArrayList<Player> players = Card24Client.connectToServer().getPlayers();
			Collections.sort(players, new Comparator<Player>() {
				@Override
				public int compare(Player player1, Player player2) {
					if(player1.wins < player2.wins) return 1;
					else if(player1.wins > player2.wins) return -1;
					else if(((int)(player1.winTimeSum/player1.wins)) > ((int)(player2.winTimeSum/player2.wins))) return 1;
					else if(((int)(player1.winTimeSum/player1.wins)) < ((int)(player2.winTimeSum/player2.wins))) return -1;
					else return 0;
				}
			});
			
			String[][] rows = new String[players.size()][5];
			int i = 0;
			for(Player player : players) {
				if(Card24Client.currentPlayer.username.equals(player.username)) Card24Client.currentPlayer.rank = i+1;
				rows[i][0] = Integer.toString(i+1);
				rows[i][1] = player.username;
				rows[i][2] = Integer.toString(player.wins);
				rows[i][3] = Integer.toString(player.gamesPlayed);
				rows[i][4] = Integer.toString((int)(player.winTimeSum/player.wins)) + "s";
				i++;
			}
			
			add(new JScrollPane(new JTable(rows, COLUMN_NAMES)));

		} catch (RemoteException e) {
			System.out.println("could not get players for leaderboard");
			JOptionPane.showMessageDialog(Card24Client.app, "Could not connect to server");
		}
	}
}
