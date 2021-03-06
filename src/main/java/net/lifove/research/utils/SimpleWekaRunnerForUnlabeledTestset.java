package net.lifove.research.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;

public class SimpleWekaRunnerForUnlabeledTestset {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SimpleWekaRunnerForUnlabeledTestset().run(args);
	}
	
	public void run(String[] args){
		Boolean header = args[0].equals("header")?true:false;
		String strClassifier = args[1];
		String trainingDataPath = args[2];
		String testDataPath = args[3];
		String trainingDate = args[4];
		String testDate = args[5];
		String pathToIDNameData = args[6];
		
		// load ID names for each instance
		ArrayList<String> idNames = FileUtil.getLines(pathToIDNameData, false);
		
		Instances trainingInstances = loadArff(trainingDataPath);
		Instances testInstances = loadArff(testDataPath);
	
		try {
			Classifier classifier = (Classifier) Utils.forName(Classifier.class, strClassifier, null);
			classifier.buildClassifier(trainingInstances);
			
			for(int i=0;i<testInstances.numInstances();i++){
				double result = classifier.classifyInstance(testInstances.get(i));
				
				String originalLabel = "";
				
				if(testInstances.get(i).classValue()==WekaUtils.getClassValueIndex(testInstances, WekaUtils.strPos))
					originalLabel = " (" + WekaUtils.strPos + ")";
				if(testInstances.get(i).classValue()==WekaUtils.getClassValueIndex(testInstances, WekaUtils.strNeg))
					originalLabel = " (" + WekaUtils.strNeg + ")";
				
				if(result==WekaUtils.getClassValueIndex(testInstances, WekaUtils.strPos))
					System.out.println(idNames.get(i) + ", " + WekaUtils.strPos + originalLabel);
				else
					System.out.println(idNames.get(i) + ", " + WekaUtils.strNeg + originalLabel);
			}
			
			Evaluation eval = new Evaluation(trainingInstances);
			eval.evaluateModel(classifier, testInstances);
			
			int posClassIndex = WekaUtils.getClassValueIndex(trainingInstances,WekaUtils.strPos);
			
			double accuracy = eval.pctCorrect()/100;
			double fmeasure = eval.fMeasure(posClassIndex);
			double precision = eval.precision(posClassIndex);
			double recall = eval.recall(posClassIndex);
			double auc = eval.areaUnderROC(posClassIndex);
			double mcc = eval.matthewsCorrelationCoefficient(posClassIndex);
			
			
			DecimalFormat dec = new DecimalFormat("0.000");
			if(header)
				System.out.println("trainingDate,testDate,accuracy,fmeasure,recall,precision,auc,mcc,#trainingAPIs,#testAPIs");
			System.out.println(trainingDate + "," + testDate + "," + 
								dec.format(accuracy) + "," + 
								dec.format(fmeasure) + "," + 
								dec.format(recall) + "," + 
								dec.format(precision) + "," + 
								dec.format(auc) + "," +
								dec.format(mcc) + "," +
								trainingInstances.numInstances() + "," +
								testInstances.numInstances());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Instances loadArff(String path){
		Instances instances=null;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			instances = new Instances(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		instances.setClassIndex(instances.numAttributes()-1);
		
		return instances;
	}
}
