
package com.thing.t.thing.mq.client;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.thing.t.thing.mq.dto.MQDetailDTO;

public class MQClient {

	// Create variables for the connection to MQ
	private String hostName; // Host name or IP address
	private int portNumber; // Listener port for your queue manager
	private String channelName ; // Channel name
	private String mqManagerName; // Queue manager name
	private String appUserName; // User name that application uses to connect to MQ
	private String appPassword; // Password that the application uses to connect to MQ
	private String mqName; // Queue that the application uses to put and get messages to and
														// from

	public String putMessageOnMQ(MQDetailDTO detailDTO, String mqMessage) {

		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;
		String receivedMessage =null;

		setMQProperties(detailDTO);

		try {
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, this.hostName);
			cf.setIntProperty(WMQConstants.WMQ_PORT, this.portNumber);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, this.channelName);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, this.mqManagerName);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "ThingTThing(MQClient)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, this.appUserName);
			cf.setStringProperty(WMQConstants.PASSWORD, this.appPassword);

			context = cf.createContext();
			destination = context.createQueue("queue:///" + this.mqName);

			long uniqueNumber = System.currentTimeMillis() % 1000;
			TextMessage message = context.createTextMessage(mqMessage);

			producer = context.createProducer();
			producer.send(destination, message);
			System.out.println("Sent message:\n" + message);

			consumer = context.createConsumer(destination); // autoclosable
			receivedMessage = consumer.receiveBody(String.class, 15000); // in ms or 15 seconds

			System.out.println("\nReceived message:\n" + receivedMessage);

			recordSuccess();
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}
		return receivedMessage;
	}

	private void setMQProperties(MQDetailDTO detailDTO) {
		this.hostName = getPropertyValue(detailDTO.getHostName(), "vdev-oms2-na-mq001-m.gid.gap.com");
		this.portNumber = Integer.parseInt(getPropertyValue(detailDTO.getPortNumber(), "1422"));
		this.channelName = getPropertyValue(detailDTO.getChannelName(), "SYSTEM.ADMIN.SVRCONN");
		this.mqManagerName = getPropertyValue(detailDTO.getQueueManagerName(), "OMS201VDNA");
		this.mqName = getPropertyValue(detailDTO.getQueueName(), "QC.MONARCH.CREATE_ORDER");
		this.appUserName = getPropertyValue(detailDTO.getAppUserName(), null);
		this.appPassword = getPropertyValue(detailDTO.getAppPassword(), null);

	}

	private String getPropertyValue(String value, String defaultValue) {
		return (value != null) ? value : defaultValue;
	}

	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		System.out.println("SUCCESS");
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		return;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}

}
