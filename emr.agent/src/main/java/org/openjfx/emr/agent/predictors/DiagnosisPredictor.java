package org.openjfx.emr.agent.predictors;

import java.util.ArrayList;
import java.util.Random;

import org.openjfx.emr.agent.controllers.DiagnosisDialogController;
import org.openjfx.emr.agent.models.Bill;
import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.models.DiagnosisExample;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import weka.classifiers.evaluation.Evaluation;
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
	private Broadcaster broadcaster;
	private ArrayList<Series<String, Integer>> seriesArray = new ArrayList<Series<String, Integer>>();
	public ArrayList<Series<String, Integer>> getSeriesArray() {
		return seriesArray;
	}
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
	private ArrayList<Bill> billsArray = new ArrayList<>();
	public void setBillsArray(ArrayList<Bill> billsArray) {
		this.billsArray = billsArray;
	}
	public ArrayList<Bill> getBillsArray() {
		return billsArray;
	}
	public ArrayList<DiagnosisExample> getExamplesList() {
		return examplesList;
	}
	public DiagnosisPredictor() {
		broadcaster = Broadcaster.getInstance();
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
		preprocessData(data);
		updateExamplesList();
	}
	private void updateExamplesList() {
		DiagnosisExample example = new DiagnosisExample();
		String id = "";
		String diagnosis = "";
		double classValue;

		try {
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
	public void preprocessData(Instances data) {
		String[] options1 = new String[2];
		options1[0] = "-R";                                   
		options1[1] = "1";    
		String[] options2 = new String[3];
		options2[0] = "-R";                                   
		options2[1] = "1";  
		options2[2] = "-V";  
		Remove remove = new Remove();      
		Add addFilter = new Add();

		try {
			remove.setOptions(options1); 
			remove.setInputFormat(data);
			Instances testingData1 = Filter.useFilter(data, remove);
			ntsFilter = new NominalToString();
			ntsFilter.setAttributeIndexes("first");
			ntsFilter.setInputFormat(testingData1);
			testingData = Filter.useFilter(testingData1, ntsFilter);
			if(testingData1.numAttributes()==1) {
				addFilter.setAttributeIndex("last");
				addFilter.setNominalLabels(DIAGNOSES);
				addFilter.setAttributeName("Diagnosis");
				addFilter.setInputFormat(testingData1);
				testingData = Filter.useFilter(testingData, addFilter);
			}
			testingData.setClassIndex(1);
			remove = new Remove(); 
			remove.setOptions(options2); 
			remove.setInputFormat(data);
			idData = Filter.useFilter(data, remove);
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
	public void evaluateModel(Instances data) {
		XYChart.Series<String, Integer> testSeries1=  new XYChart.Series<>(); // for whole-testing data validation
		try {
			preprocessData(data);
			/*
			 * Evaluation evaluation = new Evaluation(trainingData);
			 * evaluation.evaluateModel(fc, testingData);
			 * testSeries1.setName("Whole Testing Data Validation");
			 * testSeries1.getData().add(new XYChart.Data<String, Integer>("Accuracy", (int)
			 * (evaluation.pctCorrect()))); testSeries1.getData().add(new
			 * XYChart.Data<String, Integer>("Recall", (int)
			 * (evaluation.weightedRecall()*100)));
			 * System.out.println((evaluation.weightedRecall()*100));
			 * testSeries1.getData().add(new XYChart.Data<String, Integer>("Precision",
			 * (int) (evaluation.weightedPrecision()*100)));
			 * System.out.println(evaluation.weightedPrecision()*100);
			 * testSeries1.getData().add(new XYChart.Data<String, Integer>("F Measure",
			 * (int) (evaluation.weightedFMeasure()*100)));
			 * System.out.println(evaluation.weightedFMeasure()*100);
			 * testSeries1.getData().add(new XYChart.Data<String, Integer>("AUROC", (int)
			 * (evaluation.weightedAreaUnderROC()*100)));
			 * System.out.println(evaluation.weightedAreaUnderROC()*100);
			 * seriesArray.add(testSeries1);
			 */
			seriesArray.add(crossEvaluateModel());
			broadcaster.publish(Event.DIAGNOSIS_MODEL_EVALUATION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Series<String, Integer> crossEvaluateModel() {
		XYChart.Series<String, Integer> testSeries2=  new XYChart.Series<>(); // for 10-fold cross-validation
		Random rand = new Random(1);
		int folds = 3;
		try {
			Evaluation evaluation = new Evaluation(trainingData);
			evaluation.crossValidateModel(fc, testingData, folds, rand);
			testSeries2.setName("10-fold Cross-Validation");
			testSeries2.getData().add(new XYChart.Data<String, Integer>("Accuracy", (int) (evaluation.pctCorrect())));
			testSeries2.getData().add(new XYChart.Data<String, Integer>("Recall", (int) (evaluation.weightedRecall()*100)));
			testSeries2.getData().add(new XYChart.Data<String, Integer>("Precision", (int) (evaluation.weightedPrecision()*100)));
			testSeries2.getData().add(new XYChart.Data<String, Integer>("F Measure", (int) (evaluation.weightedFMeasure()*100)));
			testSeries2.getData().add(new XYChart.Data<String, Integer>("AUROC", (int) (evaluation.weightedAreaUnderROC()*100)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return testSeries2;
	}


}