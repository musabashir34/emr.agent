package org.openjfx.emr.agent.models;

import weka.core.Instance;

public class DefaultExample {
	public void setPid(String pID) {
		pid = pID;
	}
	public void setFinancialClass(String financialClass) {
		this.financialClass = financialClass;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public void setTotalDepositedAmount(double totalDepositedAmount) {
		this.totalDepositedAmount = totalDepositedAmount;
	}
	public void setTotalBill(double totalBill) {
		this.totalBill = totalBill;
	}
	public void setDaysOnAdmission(int daysOnAdmission) {
		this.daysOnAdmission = daysOnAdmission;
	}
	public String getPid() {
		return pid;
	}
	public String getFinancialClass() {
		return financialClass;
	}
	public String getRoom() {
		return room;
	}
	public double getTotalDepositedAmount() {
		return totalDepositedAmount;
	}
	public double getTotalBill() {
		return totalBill;
	}
	public int getDaysOnAdmission() {
		return daysOnAdmission;
	}
	public String getWillDefault() {
		if(willDefault.equals(""))
			return "";
		return willDefault.equals("YES")? "HIGH": "LOW";
	}
	public void setWillDefault(String willDefault) {
		this.willDefault = willDefault;
	}
	
	public DefaultExample(String pid, String prediction, Instance instance) {
		setPid(pid);
		setWillDefault(prediction);
		setFinancialClass(instance.stringValue(0));
		setRoom(instance.stringValue(1));
		setTotalDepositedAmount(instance.value(2));
		setTotalBill(instance.value(3));
		setDaysOnAdmission((int) instance.value(4));
	}
	public DefaultExample() {
	}
	private String pid = "";
	private String financialClass = "";
	private String room = "";
	private double totalDepositedAmount = 0;
	private double totalBill = 0;
	private int daysOnAdmission = 0;
	private String willDefault = "";
	
	
	}
