package org.openjfx.emr.agent.pages;

import java.util.ArrayList;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class FBSBillingPage {
	private String inpatientBillTab = "In Patient Bill";
	private String inpatientBillComboBox="Search In Patient Billing";
	private String inpatientBillSearchField="Search";
	private String inpatientTable = "#\\31 67097312604574488_orig > tbody";
	private String inpatientTableData = "#\\31 67097312604574488_orig > tbody > tr:nth-child(2) > td:nth-child(";
	private Instances trainingData;
	Page page;

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
		for(Locator locator:page.getByRole(AriaRole.OPTION).all())
			inpatients.add(locator.innerText());

		if(!inpatients.isEmpty()) {
			for(String inpatient:inpatients) {
				page.getByRole(AriaRole.TEXTBOX, new
						Page.GetByRoleOptions().setName(inpatientBillSearchField)).fill(inpatient);
				page.getByText(inpatient).click();
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


			}



		}



		return instanceTestingData;
	}


	public void setTrainingData(Instances trainingData) {
		this.trainingData = trainingData;
	}



}
