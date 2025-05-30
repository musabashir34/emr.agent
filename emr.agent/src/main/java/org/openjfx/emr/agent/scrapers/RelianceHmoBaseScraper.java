package org.openjfx.emr.agent.scrapers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openjfx.emr.agent.pages.RelianceHmoHomePage;
import org.openjfx.emr.agent.pages.RelianceHmoLoginPage;

import com.microsoft.playwright.Page;

public class RelianceHmoBaseScraper {
	protected Properties prop;
	protected PlaywrightFactory pf;
	protected Page page;
	protected RelianceHmoHomePage homePage;
	public RelianceHmoBaseScraper() {
		try {
			InputStream is = RelianceHmoBaseScraper.class.getResourceAsStream("reliancehmo.properties");
			prop = new Properties();
			prop.load(is);
			pf = new PlaywrightFactory(prop);
			page = pf.initializeBrowser();
			RelianceHmoLoginPage loginPage = new RelianceHmoLoginPage(page);
			homePage = loginPage.signIn(prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void closeBrowser() {
		page.context().browser().close();
	}

}
