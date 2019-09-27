package client;

import java.rmi.Naming;
import server.Card24Int;

//
//
//

/**
 * This is the client java application that is run.
 *
 */
public class Card24Client {
	
	//top view - frame
	public static App app;
	//sub views - panels
	public static Login login;
	public static Register register;
	public static Dashboard dashboard;
	//current player
	public static Player currentPlayer;
	
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				app = new App();
				login = new Login();
				app.add(login);
			}
      	});
	}
	
	public static Card24Int connectToServer() {
		Card24Int server = null;
		try {
			server = (Card24Int) Naming.lookup("Card24Server");
		} catch (Exception e) {
			System.err.println("Failed accessing RMI: " + e);
		}
		return server;
	}

}
