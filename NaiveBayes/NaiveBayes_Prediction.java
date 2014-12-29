package hw4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class NaiveBayes_Prediction {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		ArrayList<String[]> file = FileRead.readFile("F:/DM hw4/781670.f1/diabetic_data_initial_original.csv");

		//		for (String[] strings : file) {
		//			for (String string : strings) {
		//				System.out.print(string+" ");
		//			}
		//			System.out.println();
		//		}


		ArrayList<String>classes = findClasses(file);
		HashMap<String, Double>classesCount = findClassesCount(classes,file);// stores count of each class

		HashMap<String, Double>classPriorProbability = new HashMap<String, Double>();// prior probability of each class
		for (String cls : classesCount.keySet()) {
			double temp = classesCount.get(cls);
			classPriorProbability.put(cls, temp/file.size());
		}

	//	System.out.println(classPriorProbability);

		ArrayList<Integer>descriptorIndex = new ArrayList<Integer>();
		descriptorIndex.add(2);
		descriptorIndex.add(3);
		descriptorIndex.add(23);
		descriptorIndex.add(26);
		descriptorIndex.add(48);
				descriptorIndex.add(49);
		//		descriptorIndex.add(13);
		//		descriptorIndex.add(15);
		ArrayList<HashMap<String, Double>>descriptorCount = findDescriptorCount(descriptorIndex,file);// stores count of each descriptor
		ArrayList<HashMap<String, Double>>descriptorPriorProbability = new ArrayList<HashMap<String,Double>>();
		for (HashMap<String, Double> hashMap : descriptorCount) {
			HashMap<String, Double> intermediate = new HashMap<String, Double>();
			for (String des: hashMap.keySet()) {
				double temp = hashMap.get(des);
				intermediate.put(des, temp/file.size());
			}
			descriptorPriorProbability.add(intermediate);
		}



		HashMap<String, Double> finalDescriptorPriorProbability = new HashMap<String, Double>();

		for (HashMap<String, Double> hashMap : descriptorPriorProbability) {
			for (Entry<String, Double> e:hashMap.entrySet()) {
				finalDescriptorPriorProbability.put(e.getKey(), e.getValue());
			}
		}


//		for (Entry<String, Double> e : finalDescriptorPriorProbability.entrySet()) {
//			System.out.println(e.getKey()+"    "+e.getValue());
//		}



		//System.out.println("\n\n\n");



		ArrayList<HashMap<String, Double>>descriptorPosteriorCount = findDescriptorPosteriorCount(classes,descriptorIndex,file);
		ArrayList<HashMap<String, Double>>descriptorPosteriorProbability = new ArrayList<HashMap<String,Double>>();
		for (HashMap<String, Double> hashMap : descriptorPosteriorCount) {

			HashMap<String, Double>intermediate = new HashMap<String, Double>();
			for (String desPlusClass : hashMap.keySet()) {
				String split[] = desPlusClass.split("->");
				double denom = classesCount.get(split[1]);
				double numor = hashMap.get(desPlusClass);
				intermediate.put(desPlusClass, numor/denom);
			}
			descriptorPosteriorProbability.add(intermediate);
		}
		//		for (HashMap<String, Double> hashMap : descriptorPosteriorProbability) {
		//			System.out.println(hashMap);	
		//		}


		HashMap<String, Double> finalDescriptorPosteriorProbability = new HashMap<String, Double>();

		for (HashMap<String, Double> hashMap : descriptorPosteriorProbability) {
			for (Entry<String, Double> e:hashMap.entrySet()) {
				finalDescriptorPosteriorProbability.put(e.getKey(), e.getValue());
			}
		}



		//String input= "age:31-40, income: medium, student: yes, credit: fair";
		String input="race:Caucasian, gender:Female,A1Cresult:None,metformin:Steady,diabetesMed:No"; //////////Input string
		String h1="NO";
		String h2="<30";
		String h3=">30";
		
		
		double prob1 = findProbability(finalDescriptorPosteriorProbability,finalDescriptorPriorProbability,classPriorProbability,h1,input);
		double prob2 = findProbability(finalDescriptorPosteriorProbability,finalDescriptorPriorProbability,classPriorProbability,h2,input);
		double prob3 = findProbability(finalDescriptorPosteriorProbability,finalDescriptorPriorProbability,classPriorProbability,h3,input);
		
//		System.out.println("No is "+prob1);
//		System.out.println("<30 is "+prob2);
//		System.out.println(">30 is "+prob3);
		
		String predicted="";
		double max = Math.max(prob1, Math.max(prob2, prob3));
		if(max == prob1){
			//		System.out.println("NO");
			predicted="NO";
			//countNo++;
		}
		if(max == prob2){
			//		System.out.println("<30");
			predicted="<30";
		}
		if(max == prob3){
			//		System.out.println(">30");
			predicted=">30";
		}
		
		System.out.println("Prediction is "+predicted);
		
	}





	private static double findProbability(HashMap<String, Double> finalDescriptorPosteriorProbability,HashMap<String, Double> finalDescriptorPriorProbability,HashMap<String, Double> classPriorProbability, String h1,String input) {
		// TODO Auto-generated method stub

		String allInputAttr[] = input.split(",");
		String attrValues[]=new String[allInputAttr.length];
		for (int i=0;i<allInputAttr.length;i++) {
			attrValues[i]=allInputAttr[i].split(":")[1].trim();
		}

		ArrayList<Double>numer = new ArrayList<Double>();

		for(int i=0;i<attrValues.length;i++){
			numer.add(   finalDescriptorPosteriorProbability.get(    attrValues[i]+"->"+h1)                    );
		}

		numer.add(classPriorProbability.get(h1));

		ArrayList<Double>denom = new ArrayList<Double>();
		for(int i=0;i<attrValues.length;i++){
			denom.add(   finalDescriptorPriorProbability.get(    attrValues[i])                    );
		}

		double numerator=1;
		for (Double double1 : numer) {
			numerator=numerator*double1;
		}

		double denominator=1;
		for (Double double1 : denom) {
			denominator=denominator*double1;
		}


		//System.out.println(numerator/denominator);
		return numerator/denominator;
	}





	private static ArrayList<HashMap<String, Double>> findDescriptorPosteriorCount(ArrayList<String> classes, ArrayList<Integer> descriptorIndex,ArrayList<String[]> file) {

		//String currClass = classes.get(0);
		//Integer currColumnIndex = descriptorIndex.get(0);
		ArrayList<HashMap<String, Double>>descriptorPosteriorCount = new ArrayList<HashMap<String,Double>>(); 

		for(Integer currColumnIndex:descriptorIndex){
			HashMap<String, Double>posterialCount = new HashMap<String, Double>();
			for (String[] strings : file){
				String descPlusClass = strings[currColumnIndex]+"->"+strings[strings.length-1];
				if(posterialCount.containsKey(descPlusClass)){
					Double temp = posterialCount.get(descPlusClass);
					posterialCount.put(descPlusClass, (double) (temp+1));
				}
				else
					posterialCount.put(descPlusClass, (double) 1);
			}

			//	System.out.println(posterialCount);
			descriptorPosteriorCount.add(posterialCount);
		}

		return descriptorPosteriorCount;
	}





	private static ArrayList<HashMap<String, Double>> findDescriptorCount(ArrayList<Integer> descriptorIndex, ArrayList<String[]> file) {
		// TODO Auto-generated method stub


		ArrayList<HashMap<String, Double>> descriptorCount = new ArrayList<HashMap<String,Double>>();

		for (Integer index : descriptorIndex) {
			HashMap<String, Double>priorProbability = new HashMap<String, Double>();
			for (String[] strings : file) {
				String cls = strings[index];
				if(priorProbability.containsKey(cls)){
					Double temp = priorProbability.get(cls);
					priorProbability.put(cls, (double) (temp+1));
				}
				else
					priorProbability.put(cls, (double) 1);
			}
			descriptorCount.add(priorProbability);

		}


		return descriptorCount;
	}





	private static HashMap<String, Double> findClassesCount(ArrayList<String> classes, ArrayList<String[]> file) {
		HashMap<String, Double>priorProbability = new HashMap<String, Double>();
		for (String cls : classes) {
			priorProbability.put(cls,(double) 0);
		}
		for (String[] strings : file) {
			String cls = strings[strings.length-1];
			Double temp = priorProbability.get(cls);
			priorProbability.put(cls, (double) (temp+1));
		}

		return priorProbability;
	}


	private static ArrayList<String> findClasses(ArrayList<String[]> file) {
		ArrayList<String> classes = new ArrayList<>();
		for (String[] strings : file) {
			if(!classes.contains(strings[strings.length-1]))
				classes.add(strings[strings.length-1]);
		}
		return classes;
	}




}
