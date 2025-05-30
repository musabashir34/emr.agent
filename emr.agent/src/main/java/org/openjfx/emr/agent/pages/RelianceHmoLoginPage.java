package org.openjfx.emr.agent.pages;

import java.util.Properties;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class RelianceHmoLoginPage {
	Page page;
	private String usernameField = "Email";
	private String passwordField = "Password";
	private String signInButton = "Sign In";
	
	public RelianceHmoLoginPage(Page page) {
		this.page = page;
	}
	
	public RelianceHmoHomePage signIn(Properties prop) {
		page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(usernameField)).click();
	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(usernameField)).
	      fill(prop.getProperty("username").trim());
	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(passwordField)).click();
	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(passwordField)).
	      fill(prop.getProperty("password").trim());
	      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(signInButton)).click();
	      return new RelianceHmoHomePage(page);
		
	}

}
