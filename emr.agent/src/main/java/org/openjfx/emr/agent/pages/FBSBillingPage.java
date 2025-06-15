package org.openjfx.emr.agent.pages;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.openjfx.emr.agent.models.Bill;
import org.openjfx.emr.agent.predictors.DiagnosisPredictor;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class FBSBillingPage {
	
	private String inpatientBillTab = "In Patient Bill";
	private String inpatientBillComboBox="Search In Patient Billing";
	private String retainershipBillComboBox = "Search Retainer Account";
	private String inpatientBillSearchField="Search";
	private String retainershipBillSearchField="Search";
	private String accountDetailsViewLink = "Account Details";
	private String inpatientTable = "#\\31 67097312604574488_orig > tbody";
	private String inpatientTableData = "#\\31 67097312604574488_orig > tbody > tr:nth-child(2) > td:nth-child(";
	private String retainershipIframe = "iframe[title=\"Retainer-ship Account Details\"]";
	private String fromDateTextField = "";
	private String toDateTextField = "";
	private String formatActionsButton = "Actions";
	private String formatMenuItem = "Format";
	private String rowPerPageMenuItem = "Rows Per Page";
	private String allRecordsOption = "All";
	private String billRows = "";
	private String billRow = billRows+":nth-child(";
	private Instances trainingData;
	Page page;
	 private BiConsumer<Integer, Integer> inpatientProgressUpdate ;

	    public void setInpatientProgressUpdate(BiConsumer<Integer, Integer> progressUpdate) {
	        this.inpatientProgressUpdate = progressUpdate ;
	    }
	    private BiConsumer<Integer, Integer> billProgressUpdate ;

	    public void setBillProgressUpdate(BiConsumer<Integer, Integer> progressUpdate) {
	        this.billProgressUpdate = progressUpdate ;
	    }	

	public FBSBillingPage(Page page) {
		this.page = page;
	}

	public Instances getInpatientInstances() {
		ArrayList<Attribute> attributeArrayList = new ArrayList<>();
		for (int i = 0; i < trainingData.numAttributes()-1; i++) {
			Attribute attribute = trainingData.attribute(i);
			attributeArrayList.add(attribute);
		}
		Instances instanceTestingData = new Instances("testdata", attributeArrayList, 0);
		page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(inpatientBillTab)).click();
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(inpatientBillComboBox)).click();
		ArrayList<String> inpatients = new ArrayList<>();
		int numberOfInpatients = page.getByRole(AriaRole.OPTION).all().size();
		for(Locator locator:page.getByRole(AriaRole.OPTION).all())
			inpatients.add(locator.innerText());

		if(!inpatients.isEmpty()) {
			for(int i =1; i<= numberOfInpatients;i++) {
				String inpatient = inpatients.get(i-1);
				page.getByRole(AriaRole.TEXTBOX, new
						Page.GetByRoleOptions().setName(inpatientBillSearchField)).fill(inpatient);
				page.getByRole(AriaRole.TEXTBOX, new
						Page.GetByRoleOptions().setName(inpatientBillSearchField)).press("Enter");
				page.locator(inpatientTable).waitFor();
				if (page.locator(inpatientTableData+5+")").innerText().equals("Family Folder")||page.locator(inpatientTableData+5+")")
						.innerText().equals("Single Folder")) {
					DenseInstance instance = new DenseInstance(6);

					//add values of instance attributes
					instance.setValue(attributeArrayList.get(0), page.locator(inpatientTableData+3+")").innerText() );
					instance.setValue(attributeArrayList.get(1), page.locator(inpatientTableData+5+")").innerText() );
					instance.setValue(attributeArrayList.get(2), page.locator(inpatientTableData+6+")").innerText() );
					instance.setValue(attributeArrayList.get(3), page.locator(inpatientTableData+8+")").innerText() );
					instance.setValue(attributeArrayList.get(4), page.locator(inpatientTableData+9+")").innerText() );
					instance.setValue(attributeArrayList.get(5), page.locator(inpatientTableData+13+")").innerText() );
					//add the instance to the instances reference
					instanceTestingData.add(instance);}
				if (inpatientProgressUpdate != null) {
	                inpatientProgressUpdate.accept(i, numberOfInpatients);
	            }


			}



		}



		return instanceTestingData;
	}


	public void setTrainingData(Instances trainingData) {
		this.trainingData = trainingData;
	}

	public void generateBills(DiagnosisPredictor diagnosisPredictor, String retainer, String fromDate, String toDate) {
		Bill bill = null;
		ArrayList<Bill> billsArray = new ArrayList<>();
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(retainershipBillComboBox)).click();
		page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(retainershipBillSearchField)).fill(retainer);
		page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(retainershipBillSearchField)).press("Enter");
		 page.locator(retainershipIframe).contentFrame().locator("td").filter(new Locator.FilterOptions().setHasText(accountDetailsViewLink)).
		 getByRole(AriaRole.LINK).click();
		 page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.TEXTBOX, new
				 FrameLocator.GetByRoleOptions().setName(fromDateTextField)).fill(fromDate);
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.TEXTBOX, new
				FrameLocator.GetByRoleOptions().setName(toDateTextField)).fill(toDate);
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().
				setName(formatActionsButton)).click();
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.MENUITEM, new FrameLocator.GetByRoleOptions().setName(formatMenuItem	)).hover();
		page.locator(retainershipIframe).contentFrame().getByRole(AriaRole.MENUITEM, new FrameLocator.GetByRoleOptions().setName(rowPerPageMenuItem	)).hover();
		page.locator(retainershipIframe).contentFrame().locator("span").filter(new Locator.FilterOptions().setHasText(allRecordsOption)).click();
		int numberOfBills = page.locator(retainershipIframe).contentFrame().locator(billRows).all().size();
		for(int i = 2; i<= numberOfBills; i++) {
			bill = new Bill();
			String service = null;
			String prediction = null;
			bill.setDate(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(1)").first().innerText());
			bill.setPid(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(2)").first().innerText());
			bill.setPatientName(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(3)").first().innerText());
			service = page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(4)").first().innerText();
			prediction = diagnosisPredictor.makeSinglePrediction(service);
			bill.setServiceDescription(service);
			bill.setDiagnosis(prediction);
			bill.setQuantity(Integer.valueOf(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(5)").first().innerText()));
			bill.setAmount(Integer.valueOf(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(6)").first().innerText()));
			bill.setTotalAmount(Integer.valueOf(page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(7)").first().innerText()));
			bill.setIsInpatient((page.locator(retainershipIframe).contentFrame().locator(billRow+i+") > td:nth-child(9)").first()
					.innerText().equals(""))?"NO":"YES");
			billsArray.add(bill);
			if (billProgressUpdate != null) {
                billProgressUpdate.accept(i, numberOfBills);
		}
		diagnosisPredictor.setBillsArray(billsArray);

		
	}



}
	}
