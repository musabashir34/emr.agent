package org.openjfx.emr.agent.utilities;

public enum Event {
	FBS_WEBPAGE_LOGIN("logged in to FBS webpage"),
	PAYING_INPATIENTS_REPORT("paying inpatients data scrapped"), 
	RETAINERS_BILLS_REPORT("generating retainers bills report");
	private String topic;

	Event(String topic) {
		this.topic = topic;
	}
	public String getTopic() {
		return topic;
	}

}
