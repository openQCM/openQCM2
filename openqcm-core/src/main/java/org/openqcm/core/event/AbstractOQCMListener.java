package org.openqcm.core.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOQCMListener implements OpenQCMListener {

	private static final Logger logger = LoggerFactory.getLogger(AbstractOQCMListener.class);
	
	private ConsumerThread consumerThread;
	private BlockingQueue<OpenQCMEvent> eventQueue;
	
	public static final int DEFAULT_QUEUE_CAPACITY = 10;
	public static final int DEFAULT_TIMEOUT = 1000;
	public static final int DEFAULT_PERC_WASTE_BOUND = 25;
	
	private int timeout = DEFAULT_TIMEOUT;
	private int perc_waste_bound = DEFAULT_PERC_WASTE_BOUND;

	/**
	 * A Listener with an active thread that uses a queue. Registering an incoming event is a very light task.
	 * The active thread will then read from the queue calling the consumeEvent method. If the queue is full
	 * for a percentual bigger than 100 - perc_waste_bound then some events will be wasted in order to have
	 * the same speed of the event producer.
	 * 
	 * @param queueCapacity
	 * @param perc_waste_bound
	 */
	public AbstractOQCMListener(int queueCapacity, int perc_waste_bound) {
		super();
		eventQueue = new ArrayBlockingQueue<>(queueCapacity, false);
		this.perc_waste_bound = perc_waste_bound;
		startConsumerThread();
	}
	
	public AbstractOQCMListener() {
		this(DEFAULT_QUEUE_CAPACITY, DEFAULT_PERC_WASTE_BOUND);
	}

	@Override
	public void incomingEvent(OpenQCMEvent event) {
		try {
			eventQueue.offer(event, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.info("Incoming Event wasted because timeout inserting in queue");
		}
	}

	private void startConsumerThread() {
		consumerThread = new ConsumerThread();		
		consumerThread.start();
	}

	public void stopConsumerThread() {
		consumerThread.requestStop();
		logger.info("Stop requested. Waiting for consumer thread stop.");
		try {
			consumerThread.join();
			logger.info("Stop OK.");
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}

	public boolean isConsumerThreadAlive() {
		return consumerThread.isAlive();
	}

	private void readEvent() {
		while(!eventQueue.isEmpty() && !consumerThread.isStopRequested()) {
			ArrayBlockingQueue<OpenQCMEvent> bQueue = ((ArrayBlockingQueue<OpenQCMEvent>)eventQueue);
			int remainingCapacity = bQueue.remainingCapacity();
			int capacity = bQueue.size() + remainingCapacity;
			logger.info("remainingCapacity: " + remainingCapacity + ", capacity: " + capacity);
			float perc = (float)remainingCapacity / (float)capacity;
			perc *= 100;
			if(perc > perc_waste_bound) {
				logger.info("Consuming Event. Perc: " + perc);
				consumeEvent(bQueue.poll());
			} else {
				logger.info("Wasting event because too many events. Perc: " + perc);
				bQueue.poll();
			}
		}
	}

	public abstract void consumeEvent(OpenQCMEvent event);

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	
	private class ConsumerThread extends Thread {
		
		private boolean requestStop = false;

		public ConsumerThread() {
			super();
			setDaemon(true);
		}
		
		public void requestStop() {
			requestStop = true;
		}
		
		public boolean isStopRequested() {
			return requestStop;
		}

		@Override
		public void run() {
			while(!requestStop) {
				readEvent();
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					logger.error("Sleep fails!", e);
				}
			}
		}
	} 
	
}
