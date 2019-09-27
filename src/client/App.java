package client;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.JFrame;

//This is the top level window for the application
public class App extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int windowWidth = 1100;
	private static final int windowHeight = 600;
	
	public App() {
		
	    addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		//logout if player closes the window
	    		if(Card24Client.currentPlayer != null) {
	    			try {
	    				Card24Client.connectToServer().logout(Card24Client.currentPlayer);
	    			} catch (RemoteException re) {
	    				System.out.println(re.getMessage());
	    			}
	    		}
	    	}
	    });
	    
	    setTitle("Card24");
	    
	    // center the frame in the middle of the screen
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
	    setBounds(center.x - windowWidth / 2, center.y - windowHeight / 2, windowWidth, windowHeight);
	    
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setResizable(false);
	    setVisible(true); 
	}
	
}
