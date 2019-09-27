package server;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

public class JMSHelper {
	
	private static final String HOST = "localhost";
	private static final String JMS_CONNECTION_FACTORY = "jms/JPoker24GameConnectionFactory";
	private static final String JMS_QUEUE = "jms/JPoker24GameQueue";
	private static final String JMS_TOPIC = "jms/JPoker24GameTopic";
	
	private Context jndiContext;
	private ConnectionFactory connectionFactory;
	private Queue queue;
	private Topic topic;
	private Connection connection;
	private Session session;
	
	public JMSHelper() throws NamingException, JMSException {
		// Access JNDI
		createJNDIContext();
		
		// Lookup JMS resources
		lookupConnectionFactory();
		lookupQueue();
		lookupTopic();
		
		// Create connection
		createConnection();
	}
	
	private void createJNDIContext() throws NamingException {
		System.setProperty("org.omg.CORBA.ORBInitialHost", HOST);
		System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
		try {
			jndiContext = new InitialContext();
		} catch (NamingException e) {
			System.err.println("Could not create JNDI API context: " + e);
			throw e;
		}
	}
	
	private void lookupConnectionFactory() throws NamingException {
		try {
			connectionFactory = (ConnectionFactory)jndiContext.lookup(JMS_CONNECTION_FACTORY);
		} catch (NamingException e) {
			System.err.println("JNDI API JMS connection factory lookup failed: " + e);
			throw e;
		}
	}
	
	private void lookupQueue() throws NamingException {
		try {
			queue = (Queue)jndiContext.lookup(JMS_QUEUE);
		} catch (NamingException e) {
			System.err.println("JNDI API JMS queue lookup failed: " + e);
			throw e;
		}
	}
	
	private void lookupTopic() throws NamingException {
		try {
			topic = (Topic)jndiContext.lookup(JMS_TOPIC);
		} catch (NamingException e) {
			System.err.println("JNDI context lookup failed: " + e);
			throw e;
		}
	}
	
	private void createConnection() throws JMSException {
		try {
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException e) {
			System.err.println("Failed to create connection to JMS provider: " + e);
			throw e;
		}
	}
	
	private Session createSession() throws JMSException {
		if(session != null) {
			return session;
		} else {
			try {
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				return session;
			} catch (JMSException e) {
				System.err.println("Failed to create session: " + e);
				throw e;
			}
		}
	}
	

	public ObjectMessage createMessage(Serializable obj) throws JMSException {
		try {
			return createSession().createObjectMessage(obj);
		} catch (JMSException e) {
			System.err.println("Error preparing message: " + e);
			throw e;
		}
	}
	
	public MessageProducer createQueueSender() throws JMSException {
		try {
			return createSession().createProducer(queue);
		} catch (JMSException e) {
			System.err.println("Failed sending to queue: " + e);
			throw e;
		}
	}
	
	public MessageConsumer createQueueReciever() throws JMSException {
		try {
			return createSession().createConsumer(queue);
		} catch (JMSException e) {
			System.err.println("Failed reading from queue: " + e);
			throw e;
		}
	}
	
	public MessageProducer createTopicSender() throws JMSException {
		try {
			return createSession().createProducer(topic);
		} catch (JMSException e) {
			System.err.println("Failed sending to queue: " + e);
			throw e;
		}
	}
	
	public MessageConsumer createTopicReciever(String name) throws JMSException {
		try {
			name = name.replace("'", "''");
			String selector = "(privateMessageFrom IS NULL AND privateMessageTo IS NULL) OR " +
								"privateMessageTo = '"+name+"' OR privateMessageFrom = '"+name+"'";
			return createSession().createConsumer(topic, selector);
		} catch (JMSException e) {
			System.err.println("Failed reading from queue: " + e);
			throw e;
		}
	}
	
	
	
	
	
	
	

}
