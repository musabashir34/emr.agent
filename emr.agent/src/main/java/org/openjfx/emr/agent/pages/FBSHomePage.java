package org.openjfx.emr.agent.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class FBSHomePage {
	Page page;
	private String BillingLink = "Billing";
	public FBSHomePage(Page page) {
		this.page = page;
	}
	
	public boolean isLoggedIn() {
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(BillingLink)).first().waitFor();
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(BillingLink)).first().isVisible();
	}
	
	public FBSBillingPage goToBillingPage() {
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(BillingLink)).first().click();
		return new FBSBillingPage(page);
	}


}
