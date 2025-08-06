package org.openjfx.emr.agent.scrapers;

import java.util.ArrayList;

import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.pages.FBSBillingPage;
import org.openjfx.emr.agent.predictors.DebtDefaultPredictor;
import org.openjfx.emr.agent.utilities.Event;

public class FBSInpatientDataScraper extends FBSBaseScraper{
	private FBSBillingPage fbsBillingPage;
	public FBSBillingPage getFbsBillingPage() {
		return fbsBillingPage;
	}

	public FBSInpatientDataScraper() {
		super();
		homePage = loginPage.signIn(prop);
		fbsBillingPage = homePage.goToBillingPage();
	}
	
	public void predictInpatientsDebtRisk(ArrayList<DefaultExample> inpatients) {
		
		fbsBillingPage.getInpatients(inpatients);
		broadcaster.publish(Event.PAYING_INPATIENTS_REPORT);
	}

}
