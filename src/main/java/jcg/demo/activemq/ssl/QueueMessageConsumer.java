package jcg.demo.activemq.ssl;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * A simple message consumer which consumes the message from ActiveMQ Broker
 * 
 * @author Mary.Zheng
 *
 */
public class QueueMessageConsumer implements MessageListener {

	private String activeMqBrokerUri;
	private String username;
	private String password;
	private String destinationName;
	private Connection connection = null;
	private Session session = null;

	public static void main(String[] args) throws Exception {

		QueueMessageConsumer queueMsgListener = new QueueMessageConsumer("ssl://active-mq.active-mq:32541", "admin", "admin");
		queueMsgListener.setDestinationName("test.queue");

		try {
			queueMsgListener.run();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public QueueMessageConsumer(String activeMqBrokerUri, String username, String password) {
		super();
		this.activeMqBrokerUri = activeMqBrokerUri;
		this.username = username;
		this.password = password;
	}

	public void run() throws Exception {
//		ActiveMQConnectionFactory factory = new ActiveMQSslConnectionFactory(activeMqBrokerUri);
//		factory.setPassword(password);
//		Connection connection = factory.createConnection();
//		connection.setClientID("MaryClient");
		ActiveMQSslConnectionFactory factory = new ActiveMQSslConnectionFactory(activeMqBrokerUri);
		factory.setUserName(username);
		factory.setPassword(password);
		factory.setTrustStore("C:\\RCB\\keystore.jks");
		factory.setTrustStorePassword("password");
		connection = factory.createConnection();
		connection.setClientID("MaryClient");
		//connection = connFactory.createConnection();
		//connection.start();
		
		connection.start();
	    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(destinationName);
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(this);

		System.out.println(String.format("QueueMessageConsumer Waiting for messages at %s %s", destinationName,
				this.activeMqBrokerUri));
	}

	@Override
	public void onMessage(Message message) {
		String msg;
		try {
			//ActiveMQTextMessage ack = null;
			 msg =  String.format("QueueMessageConsumer Received message [ %s ]", ((TextMessage) message).getText());
			Thread.sleep(10000);// sleep for 10 seconds
			System.out.println(msg);
			acknowledge(message,session);
		} catch (JMSException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	
	private void acknowledge(final Message message, final Session session) throws JMSException {
		  if (message != null && session.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE) {
		    message.acknowledge();
		  }
		}

}
