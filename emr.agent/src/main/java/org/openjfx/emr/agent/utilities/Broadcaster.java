package org.openjfx.emr.agent.utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.application.Platform;

public class Broadcaster {



	private Broadcaster() {
	}

	private final Map<String, List<SubscriberObject>> subscribers = new LinkedHashMap<>();

	private static Broadcaster instance = new Broadcaster();
	
	public static Broadcaster getInstance() {
		return instance;
	}

	public void publish(Event event) {

		Platform.runLater( () -> {
			
			List<SubscriberObject> subscriberList = instance.subscribers.get(event.getTopic());

			if (subscriberList != null) {

				subscriberList.forEach(
						subscriberObject -> subscriberObject.getCb().accept(event.getTopic())
						);

				// event ends after last subscriber gets callback
			}
		} );
	}

	public void subscribe(Event event, Object subscriber, Consumer<String> cb) {
		String topic = event.getTopic();

		if( !instance.subscribers.containsKey(topic) ) {
			List<SubscriberObject> slist = new ArrayList<>();
			instance.subscribers.put( topic, slist );
		}

		List<SubscriberObject> subscriberList = instance.subscribers.get( topic );

		subscriberList.add( new SubscriberObject(subscriber, cb) );
	}

	public void unsubscribe(String topic, Object subscriber) {

		List<SubscriberObject> subscriberList = instance.subscribers.get( topic );

		if (subscriberList == null) {
			subscriberList.remove( subscriber );
		}
	}

	static class SubscriberObject {

		private final Object subscriber;
		private final Consumer<String> cb;

		public SubscriberObject(Object subscriber,
				Consumer<String> cb) {
			this.subscriber = subscriber;
			this.cb = cb;
		}

		public Object getSubscriber() {
			return subscriber;
		}

		public Consumer<String> getCb() {
			return cb;
		}

		@Override
		public int hashCode() {
			return subscriber.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return subscriber.equals(obj);
		}
	}
}
