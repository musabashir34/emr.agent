package org.openjfx.emr.agent.utilities;

public enum Event {
	FBS_WEBPAGE_LOGIN("logged in to FBS webpage"),
	PAYING_INPATIENTS_REPORT("paying inpatients data scrapped"), 
	RETAINERS_BILLS_REPORT("retainers/insurance bills report generated"),
	DEBT_DEFAULT_MODEL_EVALUATION("debt default model evaluated"),
	DIAGNOSIS_MODEL_EVALUATION("diagnosis model evaluated");
	private String topic;

	Event(String topic) {
		this.topic = topic;
	}
	public String getTopic() {
		return topic;
	}

}
