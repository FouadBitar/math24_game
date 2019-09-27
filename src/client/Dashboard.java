package client;

import java.awt.Container;
import java.rmi.RemoteException;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class Dashboard extends JTabbedPane {
	
	private static final long serialVersionUID = 1L;

	public Dashboard(Profile profile, Game game, Leaderboard leaderboard) {
		
		addTab("Profile", null, profile, "view your stats");
		addTab("Play Game", null, Game.gamePanel, "play a game");
		addTab("Leader Board", null, leaderboard, "check where you stand on the board");
		addTab("Logout", null, new Container(), "logout as this user");
		
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane selectedTab = (JTabbedPane) e.getSource();
				if(selectedTab.getSelectedIndex() == 3) {
					//logout
					try {
						Card24Client.connectToServer().logout(Card24Client.currentPlayer);
					} catch (RemoteException re) {
						System.out.println(re.getMessage());
						return;
					}
					Card24Client.currentPlayer = null;
					//go back to login page
					Card24Client.login = new Login();
					Card24Client.app.remove(Card24Client.dashboard);
					Card24Client.app.add(Card24Client.login);
					Card24Client.app.revalidate();
					Card24Client.app.repaint();
				}
			}
		});
		
		setVisible(true);
		
	}
	
}
