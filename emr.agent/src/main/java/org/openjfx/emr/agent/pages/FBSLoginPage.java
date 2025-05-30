package org.openjfx.emr.agent.pages;


import org.openjfx.emr.agent.utilities.UserLoginDetails;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class FBSLoginPage {
	Page page;
	private String usernameField = "Username";
	private String passwordField = "Password";
	private String signInButton = "Log In";
	
	public FBSLoginPage(Page page) {
		this.page = page;
	}
	
	public FBSHomePage signIn(UserLoginDetails userlogindetails) {
		page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(usernameField)).click();
	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(usernameField)).
	      fill(userlogindetails.getUserName());
	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(passwordField)).click();
	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(passwordField)).
	      fill(userlogindetails.getPassWord());
	      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(signInButton)).click();
	      return new FBSHomePage(page);
		
	}

}
