package org.openjfx.emr.agent.scrapers;

import org.openjfx.emr.agent.pages.FBSBillingPage;
import org.openjfx.emr.agent.predictors.DebtDefaultPredictor;
import org.openjfx.emr.agent.utilities.Event;

public class FBSInpatientDataScraper extends FBSBaseScraper{
	public DebtDefaultPredictor debtPredictor;
	private FBSBillingPage fbsBillingPage;
	public FBSInpatientDataScraper(DebtDefaultPredictor debtPredictor) {
		super();
		this.debtPredictor = debtPredictor;
		fbsBillingPage = homePage.goToBillingPage();
		fbsBillingPage.setTrainingData(debtPredictor.getTrainingData());
	}
	
	public void predictInpatientsDebtRisk() {
		debtPredictor.makePredictions(fbsBillingPage.getInpatientInstances());
		broadcaster.publish(Event.PAYING_INPATIENTS_REPORT);
		
	}

}
