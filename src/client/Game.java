package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import server.GameMessage;
import server.JMSHelper;



public class Game extends JPanel implements MessageListener {
	
	private static final long serialVersionUID = 1L;
	
	public static Game gamePanel;
	public static Container newGameContainer;
	public static Container gameContainer;
	
	private JMSHelper jmsHelper;
	private MessageProducer queueSender;
	private MessageConsumer topicReceiver;
	
	private static int[] cards = {0,0,0,0};
	private static int[] cardValues = {0,0,0,0};
	private static Player opponent;
	private static Date gameStartTime;
	private static JLabel answerLbl;
	
	
	public Game() {
		try {
			jmsHelper = new JMSHelper();
			queueSender = jmsHelper.createQueueSender();
			topicReceiver = jmsHelper.createTopicReciever(Card24Client.currentPlayer.username);
			topicReceiver.setMessageListener(this);
			
			displayNewGameButton(false);
			
		} catch (NamingException | JMSException e) {
			JOptionPane.showMessageDialog(Card24Client.app, "Failed connecting to server: " + e.getMessage());
		}
	}
	

	
	private void displayNewGameButton(boolean isWaiting) {
		// LABELS, BUTTONS & FIELDS
		JButton newGameBtn = new JButton("New Game");
		JLabel waitingLbl = new JLabel("Waiting for players...");
		
		// LAYOUT
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(newGameBtn)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(newGameBtn)));
		
		if(isWaiting) layout.replace(newGameBtn, waitingLbl);
		
		// EVENT LISTENERS
		newGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//switch to waiting
				layout.replace(newGameBtn, waitingLbl);
				gamePanel.updateUI();
				//request a game here
				requestGame();
				
			}
	    });
	}
	
	private JLabel displayCard(int index) {
		try {
			String path = "cards/card"+cards[index]+".png";
			File f = new File(path);
			Image img = ImageIO.read(f).getScaledInstance(150, 250, Image.SCALE_DEFAULT);
			return new JLabel(new ImageIcon(img));
		}catch(IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private void displayGame() {
		
		JButton submitBtn = new JButton("Submit");
		submitBtn.setFont(new Font("Serif", Font.PLAIN, 30));
		answerLbl = new JLabel(" = 0");
		answerLbl.setFont(new Font("Serif", Font.PLAIN, 30));
		JTextField expressionText = new JTextField();
		expressionText.setFont(new Font("Serif", Font.PLAIN, 30));
		expressionText.setMaximumSize(new Dimension(600, 50));
		
		
		JLabel card1 = displayCard(0);
		JLabel card2 = displayCard(1);
		JLabel card3 = displayCard(2);
		JLabel card4 = displayCard(3);
		
		Player p1 = Card24Client.currentPlayer;
		JLabel usernameLbl1 = new JLabel(p1.username);
	    JLabel winsLbl1 = new JLabel("Wins: " + p1.wins + "/" + p1.gamesPlayed);
	    JLabel winTimeLbl1 = new JLabel("Avg. Win Time: " + ((int)(p1.winTimeSum/p1.wins)) + "s");
	    Player p2 = opponent;
		JLabel usernameLbl2 = new JLabel(p2.username);
	    JLabel winsLbl2 = new JLabel("Wins: " + p2.wins + "/" + p2.gamesPlayed);
	    JLabel winTimeLbl2 = new JLabel("Avg. Win Time: " + ((int)(p2.winTimeSum/p2.wins)) + "s");
		
		
		// LAYOUT
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(card1)
								.addComponent(card2)
								.addComponent(card3)
								.addComponent(card4)
								.addGroup(layout.createParallelGroup()
										.addComponent(usernameLbl1)
										.addComponent(winsLbl1)
										.addComponent(winTimeLbl1)
										.addComponent(usernameLbl2)
										.addComponent(winsLbl2)
										.addComponent(winTimeLbl2)))
						.addGroup(layout.createSequentialGroup()
								.addComponent(expressionText)
								.addComponent(answerLbl)
								.addComponent(submitBtn))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(card1)
						.addComponent(card2)
						.addComponent(card3)
						.addComponent(card4)
						.addGroup(layout.createSequentialGroup()
								.addComponent(usernameLbl1)
								.addComponent(winsLbl1)
								.addComponent(winTimeLbl1)
								.addComponent(usernameLbl2)
								.addComponent(winsLbl2)
								.addComponent(winTimeLbl2)))
				.addGroup(layout.createParallelGroup()
						.addComponent(expressionText)
						.addComponent(answerLbl)
						.addComponent(submitBtn)));
		
		
		//EVENT LISTENERS
		submitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("in submit");
				String input = expressionText.getText()
						.replace("K", "13").replace("k", "13")
						.replace("Q", "12").replace("q", "12")
						.replace("J", "11").replace("j", "11")
						.replace("A",  "1").replace("a",  "1");
				if(!allCardsPresent(input)) {
					JOptionPane.showMessageDialog(Card24Client.app, "You must use all cards once");	
					return;
				}
				try {
					System.out.println(input);
					Message message = jmsHelper.createMessage(
							new GameMessage(Card24Client.currentPlayer.username, null, 
									input,GameMessage.MessageAction.SubmitAnswer));
					queueSender.send(message);
					
				} catch (JMSException e) {
					JOptionPane.showMessageDialog(Card24Client.app, e.getMessage());
				}
			}
	    });	
	}
	

	private boolean allCardsPresent(String expr) {
		//catches invalid statements
		String[] input = expr.replace(" ", "").replace("(", "").replace(")", "")
				.replace("+", ",").replace("-", ",").replace("*", ",").replace("/", ",")
				.split(",");
		
		if(input.length != 4) {
			return false;
		}
		for(int cv : cardValues) {
			if(!present(""+cv, input)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean present(String card, String[] inputs) {
		for(String s : inputs) {
			if(s.equals(card)) {
				return true;
			}
		}
		return false;
	}
	
	

	private void displayResults(Player player, String winningAnswer) {
		// LABELS, BUTTONS & FIELDS
		JButton nextGameBtn = new JButton("Next Game");
		JLabel winnerLbl = new JLabel("Winner: " + player.username);
		JLabel winningAnswerLbl = new JLabel("" + winningAnswer);
		winnerLbl.setFont(new Font("Serif", Font.BOLD,22));
		winningAnswerLbl.setFont(new Font("Serif", Font.PLAIN,16));
		
		// LAYOUT
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(winnerLbl)
						.addComponent(winningAnswerLbl)
						.addComponent(nextGameBtn)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(winnerLbl)
				.addComponent(winningAnswerLbl)
				.addComponent(nextGameBtn));
		
		//EVENT LISTENERS
		nextGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//switch to waiting
				gamePanel.removeAll();
				displayNewGameButton(true);
				gamePanel.updateUI();
				//request a game here
				requestGame();
			}
	    });
	}
	
	private void requestGame() {
		try {
			Message message = jmsHelper.createMessage(
					new GameMessage(Card24Client.currentPlayer.username, null, "", GameMessage.MessageAction.RequestGame));
			queueSender.send(message);
		} catch (JMSException e) {
			JOptionPane.showMessageDialog(Card24Client.app, e.getMessage());
		}
	}

	private void loadGame(String message) {
		String[] temp = message.split(";");
		for(String a : temp) {
			System.out.println("" + a);
		}
		
		String player1 = temp[0];
		String player2 = temp[1];
		
		boolean isPlayer1 = false;
		if(Card24Client.currentPlayer.username.equals(player1.split(",")[0])) {
			isPlayer1 = true;
		}
		
		String[] playerCards;
		String opponentName;
		if(isPlayer1) {
			playerCards = player1.split(",");
			opponentName = player2.split(",")[0];
		} else {
			playerCards = player2.split(",");
			opponentName = player1.split(",")[0];
		}
		
		for(int i = 1; i < 5; i++) {
			cards[i-1] = Integer.parseInt(playerCards[i]);
		}
		for(int i = 0; i < 4; i++) {
			cardValues[i] = cards[i] % 13;
			if(cardValues[i] == 0) cardValues[i] = 13;
		}
		
		try {
			for(Player player : Card24Client.connectToServer().getPlayers()) {
				if(opponentName.equals(player.username)) {
					opponent = player;
				}
			}
		} catch (RemoteException e) {
			System.err.println("Failed to find opponent: " + e);
		}
	}
	
	
	@Override
	public void onMessage(Message jmsMessage) {
		try {
			GameMessage gameMessage = (GameMessage) ((ObjectMessage)jmsMessage).getObject();
			
			switch(gameMessage.action) {
			case GameFound: 
				loadGame(gameMessage.message);
				gamePanel.removeAll();
				displayGame();
				gamePanel.updateUI();
				gameStartTime = new Date();
				break;
				
			case GameResults:
				int timeTaken = (int) (((new Date()).getTime() - gameStartTime.getTime())/1000);
				boolean isWinner = gameMessage.from.equals(Card24Client.currentPlayer.username);
				try{
					Card24Client.currentPlayer = 
							Card24Client.connectToServer().saveResult(Card24Client.currentPlayer, isWinner, timeTaken);			
				} catch(RemoteException e) {
					System.err.println("Failed to save game results: " + e);
				}
				
				Player winner = (isWinner) ? Card24Client.currentPlayer : opponent;
				
				Leaderboard.leaderboard.update();
				Profile.profile.update();
				gamePanel.removeAll();
				displayResults(winner, gameMessage.message);
				gamePanel.updateUI();

				break;
				
			case AnswerIncorrect:
				String[] answerAndError = gameMessage.message.split(";");
				if(answerAndError[1].equals("noError")) {
					answerLbl.setText(" = " + answerAndError[0]);
				} else {
					JOptionPane.showMessageDialog(Card24Client.app, answerAndError[1]);
				}
				break;
				
			case GameDisconnected:
				JOptionPane.showMessageDialog(Card24Client.app, "Opponent disconnected");
				gamePanel.removeAll();
				displayNewGameButton(false);
				gamePanel.updateUI();
				break;
				
			default:
				break;
			}
		} catch (JMSException e) {
			System.err.println("Failed to recieve message: " + e);
		}
		
	}


}
