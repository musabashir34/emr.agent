package org.openjfx.emr.agent.scrapers;

import java.util.Properties;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightFactory {
	private Properties prop;
	private static ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
	private static ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
	private static ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
	private static ThreadLocal<Page> tlPage = new ThreadLocal<>();
	public static Playwright getPlaywright() {
		return tlPlaywright.get();
	}
	public static Browser getBrowser() {
		return tlBrowser.get();
	}
	public static BrowserContext getBrowserContext() {
		return tlBrowserContext.get();
	}
	public static Page getPage() {
		return tlPage.get();
	}
	public PlaywrightFactory(Properties prop) {
		this.prop = prop;
		
	}
	public Page initializeBrowser() {
		tlPlaywright.set(Playwright.create());
		tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(true)));
		tlBrowserContext.set(getBrowser().newContext());
		tlPage.set(getBrowserContext().newPage());
		getPage().navigate(prop.getProperty("url").trim());
		return getPage();
		
	}
	

}
