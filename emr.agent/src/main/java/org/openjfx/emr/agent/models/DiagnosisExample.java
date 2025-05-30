package org.openjfx.emr.agent.models;

import weka.core.Instance;

public class DiagnosisExample {
	private String pid = "";

	private String item = "";
	private String diagnosis = "";
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pID) {
		pid = pID;
	}
	public DiagnosisExample(String pid, String diagnosis, Instance instance) {
		setPid(pid);
		setDiagnosis(diagnosis);
		setItem(instance.stringValue(0));
	}
	public DiagnosisExample() {
	}


}
