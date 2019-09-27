package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Register extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public Register() {

		// LABELS, BUTTONS & FIELDS
		JLabel userNameLbl = new JLabel("Username:");
		JLabel passwordLbl = new JLabel("Password:");
		JLabel confirmPasswordLbl = new JLabel("Confirm Password:");
		JTextField userNameText = new JTextField();
		JTextField passwordText = new JPasswordField();
		JTextField confirmPasswordText = new JPasswordField();
		JButton registerBtn = new JButton("Register");
		JButton cancelBtn = new JButton("Cancel");
		
		
		// EVENT LISTENERS
		registerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				String username = userNameText.getText();
				String password = passwordText.getText();
				String confirmPassword = confirmPasswordText.getText();
				
				//check if fields are not empty
				if(username.equals("") || username == null) {
					JOptionPane.showMessageDialog(Card24Client.app, "must enter username");
					return;
				}
				if(password.equals("") || password == null) {
					JOptionPane.showMessageDialog(Card24Client.app, "must enter password");
					return;
				}
				if(confirmPassword.equals("") || confirmPassword == null) {
					JOptionPane.showMessageDialog(Card24Client.app, "must re-enter password");
					return;
				}
				if(!password.equals(confirmPassword)) {
					passwordText.setText("");
					confirmPasswordText.setText("");
					JOptionPane.showMessageDialog(Card24Client.app, "passwords do not match");
					return;
				}
				
				//check from server if sign-up is good
				Player player = null;
				try {
					player = Card24Client.connectToServer().register(username, password);
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
				Card24Client.app.remove(Card24Client.register);
				Card24Client.app.add(Card24Client.dashboard);
				Card24Client.app.revalidate();
				Card24Client.app.repaint();
				
			}
	    });
		
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Card24Client.login = new Login();
				Card24Client.app.remove(Card24Client.register);
				Card24Client.app.add(Card24Client.login);
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
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(confirmPasswordLbl)
								.addComponent(confirmPasswordText))
						.addGroup(layout.createSequentialGroup()
								.addComponent(registerBtn)
								.addComponent(cancelBtn)))
				);

		layout.linkSize(SwingConstants.VERTICAL, userNameText, passwordText, confirmPasswordText);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userNameLbl))
				.addComponent(userNameText)
				.addComponent(passwordLbl)
				.addComponent(passwordText)
				.addComponent(confirmPasswordLbl)
				.addComponent(confirmPasswordText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(registerBtn)
						.addComponent(cancelBtn)));
	}
	

	

}
