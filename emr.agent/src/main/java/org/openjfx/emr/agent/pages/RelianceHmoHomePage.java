package org.openjfx.emr.agent.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class RelianceHmoHomePage {
	Page page;
	private String submitClaimsLink = "Click here to submit claims";
	public RelianceHmoHomePage(Page page) {
		this.page = page;
	}
	
	public boolean isLoggedIn() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(submitClaimsLink)).isVisible();
	}
	
	public RelianceHmoSubmitClaimsPage goToSubmitClaimsPage() {
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(submitClaimsLink)).click();
		return new RelianceHmoSubmitClaimsPage(page);
	}


}
