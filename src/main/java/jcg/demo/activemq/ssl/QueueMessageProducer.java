package jcg.demo.activemq.ssl;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSslConnectionFactory;

/**
 * A simple message producer which sends the message to ActiveMQ Broker
 * 
 * @author Mary.Zheng
 *
 */
public class QueueMessageProducer {

	private String activeMqBrokerUri;
	private String username;
	private String password;

	public static void main(String[] args) {
		QueueMessageProducer queProducer = new QueueMessageProducer("ssl://active-mq.active-mq:32541", "admin",
				"admin");
		queProducer.sendDummyMessages("test.queue");

	}

	public QueueMessageProducer(String activeMqBrokerUri, String username, String password) {
		super();
		this.activeMqBrokerUri = activeMqBrokerUri;
		this.username = username;
		this.password = password;
	}

	public void sendDummyMessages(String queueName) {
		System.out.println("QueueMessageProducer started " + this.activeMqBrokerUri);
		ActiveMQSslConnectionFactory connFactory = null;
		Connection connection1 = null;
		Session session1 = null;
		MessageProducer msgProducer = null;
		
		try {
			connFactory = new ActiveMQSslConnectionFactory(activeMqBrokerUri);
			connFactory.setUserName(username);
			connFactory.setPassword(password);
			connFactory.setTrustStore("C:\\RCB\\keystore.jks");
			connFactory.setTrustStorePassword("password");
			connection1 = connFactory.createConnection();

			connection1.start();
			session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
			msgProducer = session1.createProducer(session1.createQueue(queueName));
			
			for (int i = 0; i < 4; i++) {
				TextMessage textMessage = session1.createTextMessage(buildDummyMessage(i));
				System.out.println(textMessage);
				//msgProducer.setTimeToLive(TimeUnit.SECONDS.toMillis(60000));
				msgProducer.send(textMessage);
				//msgProducer.send(textMessage, deliveryMode, priority, timeToLive);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
			
			System.out.println("QueueMessageProducer completed");
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Caught exception: " + e.getMessage());
		} catch (Exception e1) {
			System.out.println("Caught exception: " + e1.getMessage());
		}
		try {
			if (msgProducer != null) {
				msgProducer.close();
			}
			if (session1 != null) {
				session1.close();
			}
			if (connection1 != null) {
				connection1.close();
			}
		} catch (Throwable ignore) {
		}
	}

	
	private String buildDummyMessage(int value) {
		return " Test Message " + value;
	}
}
