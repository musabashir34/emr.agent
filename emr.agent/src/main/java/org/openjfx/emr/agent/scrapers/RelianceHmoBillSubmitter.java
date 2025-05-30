package org.openjfx.emr.agent.scrapers;

import org.openjfx.emr.agent.pages.RelianceHmoSubmitClaimsPage;

public class RelianceHmoBillSubmitter extends RelianceHmoBaseScraper{
	RelianceHmoSubmitClaimsPage submitClaimsPage;
	public RelianceHmoBillSubmitter() {
		super();
		submitClaimsPage = homePage.goToSubmitClaimsPage();
	}
	public void submitClaims() {
		
	}

}
