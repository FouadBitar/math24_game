package client;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Profile extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public static Profile profile;
	
	private static GroupLayout layout;
	private static JLabel userNameLbl;
	private static JLabel wins;
	private static JLabel gamesPlayed;
	private static JLabel avgWinTime;
	private static JLabel rank;

	public Profile() {
		update();
		
	}
	
	public void update() {
		removeAll();
		Player currentPlayer = Card24Client.currentPlayer;
		
		// LABELS, BUTTONS & FIELDS
		userNameLbl = new JLabel("" + currentPlayer.username);
		userNameLbl.setFont(new Font("Serif", Font.BOLD,22));
		wins = new JLabel("Number of Wins: " + currentPlayer.wins);
		gamesPlayed = new JLabel("Number of Games Played: " + currentPlayer.gamesPlayed);
		avgWinTime = new JLabel(
				"Average Win Time: " + Integer.toString((int)(currentPlayer.winTimeSum/currentPlayer.wins)));
		rank = new JLabel("Rank: " + currentPlayer.rank);
		
		// LAYOUT
		layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(userNameLbl)
						.addComponent(wins)
						.addComponent(gamesPlayed)
						.addComponent(avgWinTime)
						.addComponent(rank))
				);
		
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userNameLbl))
				.addComponent(wins)
				.addComponent(gamesPlayed)
				.addComponent(avgWinTime)
				.addComponent(rank));
		
		
		this.updateUI();
		
	}
	


}
