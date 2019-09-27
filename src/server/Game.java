package server;

/**
 * This class creates a game with two player user names,
 * as well as cards for the game between the two players.
 * The cards will contain integers from 1-52 as the client 
 * application has 52 images with cards from all suits.
 */
public class Game {
	
	public String player1, player2;
	public int[] gameCards = new int[4];
	
	public Game(String p1, String p2) {
		this.player1 = p1;
		this.player2 = p2;
		
		for(int i=0; i<4; i++) {
			this.gameCards[i] = (int)(Math.random() * 52) + 1;
		}
	}
	
	/**
	 * This method is used to create a string of the game's data to send to the
	 * players in a message
	 */
	public String toString() {
		return "" + player1 + "," + gameCards[0] + "," + gameCards[1] + "," + gameCards[2] + "," + gameCards[3]		
				+ ";"
				+ player2 + "," + gameCards[0] + "," + gameCards[1] + "," + gameCards[2] + "," + gameCards[3];
	}
}
