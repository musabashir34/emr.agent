package org.openjfx.emr.agent.scrapers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openjfx.emr.agent.pages.FBSHomePage;
import org.openjfx.emr.agent.pages.FBSLoginPage;
import org.openjfx.emr.agent.pages.RelianceHmoHomePage;
import org.openjfx.emr.agent.pages.RelianceHmoLoginPage;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;
import org.openjfx.emr.agent.utilities.UserLoginDetails;

import com.microsoft.playwright.Page;

public class FBSBaseScraper {
	protected Properties prop;
	protected PlaywrightFactory pf;
	protected Page page;
	protected FBSLoginPage loginPage;
	protected FBSHomePage homePage;
	protected Broadcaster broadcaster = Broadcaster.getInstance();
	public FBSBaseScraper() {
		try {
			InputStream is = FBSBaseScraper.class.getResourceAsStream("fbs.properties");
			prop = new Properties();
			prop.load(is);
			pf = new PlaywrightFactory(prop);
			page = pf.initializeBrowser();
			loginPage = new FBSLoginPage(page);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signIn(UserLoginDetails userlogindetails) {
		homePage = loginPage.signIn(userlogindetails);
		userlogindetails.setValidated(homePage.isLoggedIn());
		broadcaster.publish(Event.FBS_WEBPAGE_LOGIN);

	}
	public void closeBrowser() {
		page.context().browser().close();
	}

}
