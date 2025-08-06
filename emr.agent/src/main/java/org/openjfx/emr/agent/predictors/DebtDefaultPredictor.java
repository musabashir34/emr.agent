package org.openjfx.emr.agent.predictors;

import java.util.ArrayList;
import java.util.Random;

import org.openjfx.emr.agent.controllers.DefaultDialogController;
import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;

public class DebtDefaultPredictor {
	private Broadcaster broadcaster;
	private ArrayList<Series<String, Integer>> seriesArray = new ArrayList<Series<String, Integer>>();
	public ArrayList<Series<String, Integer>> getSeriesArray() {
		return seriesArray;
	}
	private Instances trainingData;
	public Instances getTrainingData() {
		return trainingData;
	}
	public Instances getTestingData() {
		return testingData;
	}
	public Instances getIdData() {
		return idData;
	}
	public RandomForest getRdf() {
		return rdf;
	}
	private Instances testingData;
	private Instances idData;
	private RandomForest rdf;
	private ArrayList<DefaultExample> examplesList = new ArrayList<>();
	public ArrayList<DefaultExample> getExamplesList() {
		return examplesList;
	}
	public DebtDefaultPredictor() {
		broadcaster = Broadcaster.getInstance();
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
		preprocessData(data);
		updateExamplesList();
	}
	private void updateExamplesList() {
		DefaultExample example = new DefaultExample();
		 double classValue;
		 String id = "";
			String prediction = "";
		 
		try {
			
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
	private void preprocessData(Instances data) {
		
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
			 testingData = Filter.useFilter(data, remove);
			 if(testingData.numAttributes()==5) {
				 addFilter.setAttributeIndex("last");
				 addFilter.setNominalLabels("YES, NO");;
				 addFilter.setAttributeName("Defaulted");
				 addFilter.setInputFormat(testingData);
				 testingData = Filter.useFilter(testingData, addFilter);
			 }
			 testingData.setClassIndex(testingData.numAttributes()-1);
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

	public void evaluateModel(Instances data) {
		XYChart.Series<String, Integer> testSeries1=  new XYChart.Series<>(); // for whole-testing data validation
		try {
			preprocessData(data);
			Evaluation evaluation = new Evaluation(trainingData);
			evaluation.evaluateModel(rdf, testingData);
			testSeries1.setName("Whole Testing Data Validation");
			testSeries1.getData().add(new XYChart.Data<String, Integer>("Accuracy", (int) (evaluation.pctCorrect())));
			testSeries1.getData().add(new XYChart.Data<String, Integer>("Recall", (int) (evaluation.weightedRecall()*100)));
			testSeries1.getData().add(new XYChart.Data<String, Integer>("Precision", (int) (evaluation.weightedPrecision()*100)));
			testSeries1.getData().add(new XYChart.Data<String, Integer>("F Measure", (int) (evaluation.weightedFMeasure()*100)));
			testSeries1.getData().add(new XYChart.Data<String, Integer>("AUROC", (int) (evaluation.weightedAreaUnderROC()*100)));
			seriesArray.add(testSeries1);
			seriesArray.add(crossEvaluateModel());
			broadcaster.publish(Event.DEBT_DEFAULT_MODEL_EVALUATION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Series<String, Integer> crossEvaluateModel() {
		XYChart.Series<String, Integer> testSeries2=  new XYChart.Series<>(); // for 10-fold cross-validation
		Random rand = new Random(1);
		int folds = 10;
		try {
			Evaluation evaluation = new Evaluation(trainingData);
			evaluation.crossValidateModel(rdf, testingData, folds, rand);
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
	public void predictExamples(ArrayList<DefaultExample> inpatients) {
		examplesList.clear();
		ArrayList<Attribute> attributeArrayList = new ArrayList<>();
        for (int i = 0; i < trainingData.numAttributes(); i++) {
            Attribute attribute = trainingData.attribute(i);
            attributeArrayList.add(attribute);
        }
        Instances instanceTestingData = new Instances("testdata", attributeArrayList, 0);
        instanceTestingData.setClassIndex(instanceTestingData.numAttributes() - 1);
        for (DefaultExample inpatient:inpatients) {
        	DenseInstance instance = new DenseInstance(6);
        	 String financialClass = inpatient.getFinancialClass();
             instance.setValue(attributeArrayList.get(0), financialClass );
             String room = inpatient.getRoom();
             instance.setValue(attributeArrayList.get(1), room );
             double totalDeposit = inpatient.getTotalDepositedAmount();
             instance.setValue(attributeArrayList.get(2), totalDeposit );
             double totalBill = inpatient.getTotalBill();
             instance.setValue(attributeArrayList.get(3), totalBill );
             int daysOnAdmission = inpatient.getDaysOnAdmission();
             instance.setValue(attributeArrayList.get(4), daysOnAdmission );
             instance.setDataset(instanceTestingData);
             try {
     			double classValue = rdf.classifyInstance(instance);
     			String prediction = instanceTestingData.classAttribute().value((int)  classValue  );
     			inpatient.setWillDefault(prediction);
     			examplesList.add(inpatient);
     		} catch (Exception e) {
     		}
        }
       
		
	}
}