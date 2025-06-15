package org.openjfx.emr.agent.scrapers;

import org.openjfx.emr.agent.pages.FBSBillingPage;
import org.openjfx.emr.agent.predictors.DiagnosisPredictor;
import org.openjfx.emr.agent.utilities.Event;

public class FBSBillsScraper extends FBSBaseScraper{
	private DiagnosisPredictor diagnosisPredictor;
	

	private FBSBillingPage fbsBillingPage;
	public FBSBillsScraper(DiagnosisPredictor diagnosisPredictor) {
		super();
		fbsBillingPage = homePage.goToBillingPage();
		this.diagnosisPredictor = diagnosisPredictor;
	}
	
	public void generateBills(String retainer, String fromDate, String toDate) {
		fbsBillingPage.generateBills(diagnosisPredictor, retainer, fromDate, toDate);
		broadcaster.publish(Event.RETAINERS_BILLS_REPORT);
	}

	public FBSBillingPage getFbsBillingPage() {
		// TODO Auto-generated method stub
		return fbsBillingPage;
	}

}
