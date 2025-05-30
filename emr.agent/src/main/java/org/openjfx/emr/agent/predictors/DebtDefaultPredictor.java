package org.openjfx.emr.agent.predictors;

import java.util.ArrayList;

import org.openjfx.emr.agent.controllers.DefaultDialogController;
import org.openjfx.emr.agent.models.DefaultExample;

import javafx.scene.control.Alert;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;

public class DebtDefaultPredictor {
	private Instances trainingData;
	private Instances testingData;
	private Instances idData;
	private RandomForest rdf;
	private ArrayList<DefaultExample> examplesList = new ArrayList<>();
	public ArrayList<DefaultExample> getExamplesList() {
		return examplesList;
	}
	public DebtDefaultPredictor() {
		CSVLoader loader = new CSVLoader();
		try {
			loader.setSource(DebtDefaultPredictor.
					class.getResourceAsStream("debt_default_balanced.csv"));
			trainingData = loader.getDataSet();
			trainingData.setClassIndex(trainingData.numAttributes()-1);
			rdf = (RandomForest) weka.core.SerializationHelper.read(DebtDefaultPredictor.
					class.getResourceAsStream("defaultrdf.model"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void makePredictions(Instances data) {
		updateExamplesList(data);
	}
	private void updateExamplesList(Instances data) {
		DefaultExample example = new DefaultExample();
		String id = "";
		String prediction = "";
		String[] options1 = new String[2];
		 options1[0] = "-R";                                   
		 options1[1] = "1";    
		 String[] options2 = new String[3];
		 options2[0] = "-R";                                   
		 options2[1] = "1";  
		 options2[2] = "-V";  
		 Remove remove = new Remove();      
		 Add filter = new Add();
		 double classValue;
		 
		try {
			remove.setOptions(options1); 
			 remove.setInputFormat(data);
			 testingData = Filter.useFilter(data, remove);
			 filter.setAttributeIndex("last");
			 filter.setNominalLabels("YES, NO");;
			 filter.setAttributeName("Defaulted");
			 filter.setInputFormat(testingData);
			 testingData = Filter.useFilter(testingData, filter);
			 testingData.setClassIndex(testingData.numAttributes()-1);
			 remove = new Remove(); 
			 remove.setOptions(options2); 
			 remove.setInputFormat(data);
			 idData = Filter.useFilter(data, remove);
			 for (int i =0; i < testingData.numInstances();i++) {
					id = String.valueOf(idData.get(i).value(0));
					classValue = rdf.classifyInstance(testingData.get(i));
					prediction = testingData.classAttribute().value((int)  classValue  );
					example = new DefaultExample(id, prediction, testingData.instance(i));
					examplesList.add(example);
				}
		} catch (Exception e) {
Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to Make Predictions. CSV file not in profer format. Click Help button to learn more about the data format") {
				
			};
			alert.showAndWait();
		}
	}
	public DefaultExample makeSinglePrediction(DefaultDialogController dialogContentController) {
		DefaultExample example = null;
        ArrayList<Attribute> attributeArrayList = new ArrayList<>();
        for (int i = 0; i < trainingData.numAttributes(); i++) {
            Attribute attribute = trainingData.attribute(i);
            attributeArrayList.add(attribute);
        }
        Instances instanceTestingData = new Instances("testdata", attributeArrayList, 0);
        DenseInstance instance = new DenseInstance(6);
        String id = dialogContentController.getId();
        if (id.equals(""))
        	return null;
        String financialClass = dialogContentController.getFinancialClassChoice();
        instance.setValue(attributeArrayList.get(0), financialClass );
        String room = dialogContentController.getRoomChoice();
        instance.setValue(attributeArrayList.get(1), room );
        double totalDeposit = dialogContentController.getTotalDeposit();
        if ((int)totalDeposit == 0)
        	return null;
        instance.setValue(attributeArrayList.get(2), totalDeposit );
        double totalBill = dialogContentController.getTotalBill();
        if ((int)totalBill == 0)
        	return null;
        instance.setValue(attributeArrayList.get(3), totalBill );
        int daysOnAdmission = dialogContentController.getDaysOnAdmission();
        if (daysOnAdmission == 0)
        	return null;
        instance.setValue(attributeArrayList.get(4), daysOnAdmission );
        instance.setDataset(instanceTestingData);
        instanceTestingData.setClassIndex(instanceTestingData.numAttributes() - 1);
        try {
			double classValue = rdf.classifyInstance(instance);
			String prediction = instanceTestingData.classAttribute().value((int)  classValue  );
			example = new DefaultExample(id, prediction, instance);
		} catch (Exception e) {
			return null;
		}
		return example;
	}

	
}