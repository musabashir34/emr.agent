package org.openjfx.emr.agent.pages;

import java.util.ArrayList;

import org.openjfx.emr.agent.models.Bill;
import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.predictors.DiagnosisPredictor;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

public class FBSBillingPage {
	
	private String inpatientBillTab = "In Patient Bill";
	private String inpatientBillComboBox="Search In Patient Billing";
	private String retainershipBillComboBox = "Search Retainer Account";
	private String inpatientBillSearchField="//input[@aria-label='Search']";
	private String retainershipBillSearchField="Search";
	private String accountDetailsViewLink = "#\\31 28539577694764447_orig > tbody > tr:nth-child(2) > td:nth-child(10) > a > span";
	private String inpatientsList = "#PopupLov_9_P9_SEARCH_IN_PATIENT_BILLING_dlg > div.a-PopupLOV-results.a-TMV.js-no-select > div > div.a-TMV-w-scroll > ul > li";
	private String inpatientTable = "#\\31 67097312604574488_orig > tbody";
	private String inpatientTableRow = "#\\31 67097312604574488_orig > tbody > tr:nth-child(2) > td:nth-child(";
	private String retainershipIframe = "iframe[title=\"Retainer-ship Account Details\"]";
	private String fromDateTextField = "//*[@id=\"P52_FROM_DATE_AS_AT_input\"]";
	private String toDateTextField = "//*[@id=\"P52_TO_DATE_AS_AT_input\"]";
	private String formatActionsButton = "Actions";
	private String formatMenuItem = "Format";
	private String rowPerPageMenuItem = "Rows Per Page";
	private String allRecordsOption = "All";
	private String billTable = "#\\31 2136564204765878_orig";
	private String billRows = "#\\31 2136564204765878_orig > tbody > tr";
	private String billRow = billRows+":nth-child(";
	private String balance = "#report_table_R130450159840569156 > tbody > tr > td:nth-child(5)";
	Page page;


	public FBSBillingPage(Page page) {
		
		this.page = page;
	}

	public void getInpatients(ArrayList<DefaultExample> payingInpatients) {
		page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(inpatientBillTab)).click();
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(inpatientBillComboBox)).click();
		ArrayList<String> inpatients = new ArrayList<>();
		page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions()).first().waitFor();
		for(Locator locator:page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions()).all())
			{inpatients.add(locator.innerText());
			}
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(inpatientBillComboBox)).click();

		if(!inpatients.isEmpty()) {
			int numberOfInpatients = inpatients.size();
			for(int i =0; i< numberOfInpatients;i++) {
				DefaultExample payingPatient = new DefaultExample();
				String inpatient = inpatients.get(i);
				page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(inpatientBillComboBox)).click();
				page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions()).getByText(inpatient).click();
				int index = inpatient.indexOf('-');
				String pid = inpatient.substring(0, index);
				page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(pid)).waitFor();
				String financialClass = page.locator(inpatientTableRow+5+")").innerText().trim();
				if(financialClass.equals("Single Folder")||financialClass.equals("Family Folder")) {
					payingPatient.setFinancialClass(financialClass);
					payingPatient.setPid(pid);
					String room = page.locator(inpatientTableRow+6+")").innerText().trim();
					payingPatient.setRoom(room);
					String depositedAmount = page.locator(inpatientTableRow+8+")").innerText().trim().replace(",","");
					double deposit = Double.parseDouble(depositedAmount);
					payingPatient.setTotalDepositedAmount(deposit);
					String totalBill =page.locator(inpatientTableRow+9+")").innerText().trim().replace(",","") ;
					double bill = Double.parseDouble(totalBill);
					payingPatient.setTotalBill(bill);
					String days = page.locator(inpatientTableRow+13+")").innerText().trim();
					int dayz = Integer.valueOf(days);
					payingPatient.setDaysOnAdmission(dayz);
					payingInpatients.add(payingPatient);
				}
					
					
			}
		}
	}

	public void generateBills(DiagnosisPredictor diagnosisPredictor, String retainer, String fromDate, String toDate) {
		Bill bill = null;
		ArrayList<Bill> billsArray = new ArrayList<>();
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(retainershipBillComboBox)).click();
		page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(retainershipBillSearchField)).fill(retainer);
		page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(retainershipBillSearchField)).press("Enter");
		 page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(retainer)).click();
		 page.locator(accountDetailsViewLink).click();
		 Locator fromDateInput = page.locator(retainershipIframe).contentFrame().locator(fromDateTextField);
		 Locator toDateInput = page.locator(retainershipIframe).contentFrame().locator(toDateTextField);
		 fromDateInput.fill(fromDate);
		 page.keyboard().press("Enter");
		 toDateInput.fill(toDate);
		 page.keyboard().press("Enter");
		 page.locator(retainershipIframe).contentFrame().locator(billTable).waitFor();
		 String totalBalance = page.locator(retainershipIframe).contentFrame().locator(balance).innerText();
			System.out.println(totalBalance);
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().
				setName(formatActionsButton)).click();
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.MENUITEM, new FrameLocator.GetByRoleOptions().setName(formatMenuItem	)).hover();
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.MENUITEM, new FrameLocator.GetByRoleOptions().setName(rowPerPageMenuItem	)).hover();
		page.locator(retainershipIframe).contentFrame().locator("span").filter(new Locator.FilterOptions().setHasText(allRecordsOption)).click();
		
		page.locator(retainershipIframe).contentFrame().getByLabel("Data view of Debit").getByText(totalBalance).scrollIntoViewIfNeeded();
		//System.out.println(totalBalance);
		int numberOfBills = page.locator(retainershipIframe).contentFrame().locator(billRows).all().size();
		//System.out.println(numberOfBills);
		
		
		for(int i = 2; i< numberOfBills; i++) {
			//System.out.println("I am here");
			bill = new Bill();
			String service = null;
			String prediction = null;
			page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(1)").scrollIntoViewIfNeeded();
			bill.setDate(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(1)").first().innerText());
			bill.setPid(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(2)").first().innerText());
			bill.setPatientName(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(3)").first().innerText());
			service = page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(4)").first().innerText();
			prediction = diagnosisPredictor.makeSinglePrediction(service);
			bill.setServiceDescription(service);
			bill.setDiagnosis(prediction);
			bill.setQuantity(Integer.valueOf(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(5)").first().innerText()));
			bill.setAmount(Double.valueOf(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(6)").first().innerText().replace(",","")));
			bill.setTotalAmount(Double.valueOf(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(7)").first().innerText().replace(",","")));
			bill.setIsInpatient((page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(9)").first()
					.innerText().equals(""))?"NO":"YES");
			billsArray.add(bill);}
		diagnosisPredictor.setBillsArray(billsArray);
}
	}
