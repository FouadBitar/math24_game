package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import javax.swing.*;


public class Login extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public Login() {
		
		// LABELS, BUTTONS & FIELDS
		JLabel userNameLbl = new JLabel("Username:");
		JLabel passwordLbl = new JLabel("Password:");
		JTextField userNameText = new JTextField();
		JTextField passwordText = new JPasswordField();
		JButton loginBtn = new JButton("Login");
		JButton registerBtn = new JButton("Register");
		
		
		// EVENT LISTENERS
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				String username = userNameText.getText();
				String password = passwordText.getText();
				
				//check if fields are not empty
				if(username.equals("") || username == null) {
					JOptionPane.showMessageDialog(Card24Client.app, "must enter username");
					return;
				}
				if(password.equals("") || password == null) {
					JOptionPane.showMessageDialog(Card24Client.app, "must enter password");
					return;
				}
				
				//check from server if credentials match
				Player player = null;
				try {
					player = Card24Client.connectToServer().login(username, password);
				} catch(RemoteException e) {
					JOptionPane.showMessageDialog(Card24Client.app, e.getMessage());
					return;
				}
				
				//save the player on the client side
				Card24Client.currentPlayer = player;
				
				
				//now close this panel and open the dash-board
				Game.gamePanel = new Game();
				Leaderboard.leaderboard = new Leaderboard();
				Profile.profile = new Profile();
				
				Card24Client.dashboard = new Dashboard(Profile.profile, Game.gamePanel, Leaderboard.leaderboard);
				Card24Client.app.remove(Card24Client.login);
				Card24Client.app.add(Card24Client.dashboard);
				Card24Client.app.revalidate();
				Card24Client.app.repaint();
			}
	    });
		
		
		//move to register page
		registerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Card24Client.register = new Register();
				Card24Client.app.remove(Card24Client.login);
				Card24Client.app.add(Card24Client.register);
				Card24Client.app.revalidate();
				Card24Client.app.repaint();
			}
	    });
	    
		
		
		// LAYOUT
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(userNameLbl)
								.addComponent(userNameText))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(passwordLbl)
								.addComponent(passwordText))
						.addGroup(layout.createSequentialGroup()
								.addComponent(loginBtn)
								.addComponent(registerBtn)))
				);
		
		layout.linkSize(SwingConstants.VERTICAL, userNameText, passwordText);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userNameLbl))
				.addComponent(userNameText)
				.addComponent(passwordLbl)
				.addComponent(passwordText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(loginBtn)
						.addComponent(registerBtn)));
		
	}

}
