package org.openjfx.emr.agent.predictors;

import java.util.ArrayList;

import org.openjfx.emr.agent.controllers.DiagnosisDialogController;
import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.models.DiagnosisExample;

import javafx.scene.control.Alert;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.Remove;

public class DiagnosisPredictor {
	private  static final String DIAGNOSES = "Acute Diarrheal Disease,Conjunctivitis,Allergy,Anemia,Arthritis,Asthma,"
			+ "Sepsis,Depressive Illness + Anxiety DisorderDermatophytosis,Diabetes Mellitus,Heart Failure,Hypertension,"
			+ "Gastroenteritis,Helminthiasis,Hepatitis,HIV Infection,Metabolic Syndrome,Hypovolemia + Dyselectrolytemia,"
			+ "Malaria,Peptic Ulcer Disease,Pregnancy,Respiratory Tract Infection,Seizures,Sickle Cell Disease,"
			+ "Urinary Tract Infection,Viral Syndrome";
	private Instances trainingData;
	private Instances testingData;
	Instances instanceTestingData;
	ArrayList<Attribute> attributeArrayList = new ArrayList<>();
	private Instances idData;
	private FilteredClassifier fc;
	private NominalToString ntsFilter;
	private ArrayList<DiagnosisExample> examplesList = new ArrayList<>();
	public ArrayList<DiagnosisExample> getExamplesList() {
		return examplesList;
	}
	public DiagnosisPredictor() {
		CSVLoader loader = new CSVLoader();
		try {
			loader.setSource(DiagnosisPredictor.
					class.getResourceAsStream("diagnosis_balanced.csv"));
			Instances trainingData1 = loader.getDataSet();
			ntsFilter = new NominalToString();
			ntsFilter.setAttributeIndexes("first");
			ntsFilter.setInputFormat(trainingData1);
			trainingData = Filter.useFilter(trainingData1, ntsFilter);
			trainingData.setClassIndex(1);
	        for (int i = 0; i < trainingData.numAttributes(); i++) {
	            Attribute attribute = trainingData.attribute(i);
	            attributeArrayList.add(attribute);
	        }
	        instanceTestingData = new Instances("testdata", attributeArrayList, 0);
	        instanceTestingData.setClassIndex(instanceTestingData.numAttributes() - 1);
			fc = (FilteredClassifier) weka.core.SerializationHelper.read(DiagnosisPredictor.
					class.getResourceAsStream("filteredtextclassifier.model"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void makePredictions(Instances data) {
		updateExamplesList(data);
	}
	private void updateExamplesList(Instances data) {
		DiagnosisExample example = new DiagnosisExample();
		String id = "";
		String diagnosis = "";
		String[] options1 = new String[2];
		options1[0] = "-R";                                   
		options1[1] = "1";    
		String[] options2 = new String[3];
		options2[0] = "-R";                                   
		options2[1] = "1";  
		options2[2] = "-V";  
		Remove remove = new Remove();      
		Add addFilter = new Add();
		double classValue;

		try {
			remove.setOptions(options1); 
			remove.setInputFormat(data);
			Instances testingData1 = Filter.useFilter(data, remove);
			ntsFilter = new NominalToString();
			ntsFilter.setAttributeIndexes("first");
			ntsFilter.setInputFormat(testingData1);
			testingData1 = Filter.useFilter(testingData1, ntsFilter);
			addFilter.setAttributeIndex("last");
			addFilter.setNominalLabels(DIAGNOSES);
			addFilter.setAttributeName("Diagnosis");
			addFilter.setInputFormat(testingData1);
			testingData = Filter.useFilter(testingData1, addFilter);
			testingData.setClassIndex(1);
			remove = new Remove(); 
			remove.setOptions(options2); 
			remove.setInputFormat(data);
			idData = Filter.useFilter(data, remove);
			for (int i =0; i < testingData.numInstances();i++) {
				id = String.valueOf(idData.get(i).value(0));
				classValue = fc.classifyInstance(testingData.get(i));
				diagnosis = trainingData.classAttribute().value((int)  classValue  );
				example = new DiagnosisExample(id, diagnosis, testingData.instance(i));
				examplesList.add(example);
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to Make Predictions. CSV file not in profer format. Click Help button to learn more about the data format") {

			};
			alert.showAndWait();
		}
	}
	public DiagnosisExample makeSinglePrediction(DiagnosisDialogController dialogContentController) {
		DiagnosisExample example = null;
        DenseInstance instance = new DenseInstance(2);
        String id = dialogContentController.getId();
        if (id.equals(""))
        	return null;
        String billItem = dialogContentController.getBillItem();
        instance.setValue(attributeArrayList.get(0), billItem );
        instance.setDataset(instanceTestingData);
        try {
			double classValue = fc.classifyInstance(instance);
			String prediction = instanceTestingData.classAttribute().value((int)  classValue  );
			example = new DiagnosisExample(id, prediction, instance);
		} catch (Exception e) {
			return null;
		}
		return example;
	}
	public String makeSinglePrediction(String billItem) {
		String prediction;
		DenseInstance instance = new DenseInstance(2);
		instance.setValue(attributeArrayList.get(0), billItem );
		 instance.setDataset(instanceTestingData);
		 try {
				double classValue = fc.classifyInstance(instance);
				prediction = instanceTestingData.classAttribute().value((int)  classValue  );
			} catch (Exception e) {
				return null;
			}
			return prediction;
		
	}


}