
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class Main {

	public static void main(String[] args) throws IOException {


		LinkedHashMap<String, ArrayList<String>> possibleValues = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> temp1 = new ArrayList<String>();
		
		
		//Caucasian, AfricanAmerican, Other, ?, Asian, Hispanic
		temp1.add("Caucasian");
		temp1.add("AfricanAmerican");
		temp1.add("Other");
		temp1.add("Asian");
		temp1.add("Hispanic");
		possibleValues.put("race", temp1);
		temp1 = new ArrayList<String>();

		temp1.add("Female");
		temp1.add("Male");
		possibleValues.put("gender", temp1);
		temp1 = new ArrayList<String>();

		
		//[0-10), [10-20), [20-30), [30-40), [40-50), [50-60), [60-70), [70-80), [80-90), [90-100)
		temp1.add("None");
		temp1.add(">7");
		temp1.add(">8");
		temp1.add("Norm");
		possibleValues.put("A1Cresult", temp1);
		
		//No, Steady, Up, Down
		temp1 = new ArrayList<String>();
		temp1.add("No");	
		temp1.add("Steady");	
		temp1.add("Up");	
		temp1.add("Down");	
		possibleValues.put("metformin", temp1);
		
		
		temp1 = new ArrayList<String>();
		temp1.add("No");	
		temp1.add("Yes");	
		possibleValues.put("diabetesMed", temp1);
		BufferedReader br = new BufferedReader(new FileReader("C:/Users/evenstar/Desktop/dm/trainingset1.csv"));
		br.readLine();
		String line = br.readLine();
		int i=0;
		ArrayList<ArrayList<String>> records = new ArrayList<ArrayList<String>>();
		while(line!=null){
			String split[] = line.split(",");
			ArrayList<String> temp = new ArrayList<String>();
			if(!line.contains("?") && !line.contains("Unknown/Invalid") ){
				for (String string : split) {
					temp.add(string);
				}

				records.add(temp);
			}
			line=br.readLine();
		}


		HashMap<String, Integer>attrIndex = new HashMap<String, Integer>();
		attrIndex.put("race", 0);
		attrIndex.put("gender", 1);
		attrIndex.put("A1Cresult", 2);
		attrIndex.put("metformin", 3);
		attrIndex.put("diabetesMed", 4);
		attrIndex.put("readmitted", 5);


		ArrayList<Double>entropy = new ArrayList<Double>();
		ArrayList<Boolean>visited = new ArrayList<Boolean>();
		visited.add(false);
		visited.add(false);
		visited.add(false);
		visited.add(false);
		visited.add(false);

		TreeNode root= new TreeNode("dummy","dummy",visited,entropy,attrIndex,records,possibleValues);

		BuildTree(root);

		
		//printPaths(root,new ArrayList<String>());
		br = new BufferedReader(new FileReader("C:/Users/evenstar/Desktop/dm/file1.csv"));
		br.readLine();
		line = br.readLine();
		i=0;
		ArrayList<ArrayList<String>> testingrecords = new ArrayList<ArrayList<String>>();
		while(line!=null){
			String split[] = line.split(",");
			ArrayList<String> temp = new ArrayList<String>();
			if(!line.contains("?") && !line.contains("Unknown/Invalid") ){
				for (String string : split) {
					temp.add(string);
				}

				testingrecords.add(temp);
			}
			line=br.readLine();
		}
		Prediction pre = new Prediction();
		int countcorrect=0, countincorrect=0;
		
for (ArrayList<String> arrayList : testingrecords) {
	
String act_lbl=arrayList.get(5);
		pre.predictedlbl=null;
		pre.setRace(arrayList.get(0));
		pre.setGender(arrayList.get(1));
		pre.setA1Cresult(arrayList.get(2));
		pre.setMetformin(arrayList.get(3));
		pre.setDiabetesMed(arrayList.get(4));
		
		pre.predict(root);
		if(pre.predictedlbl==null)
			System.out.println("");
	if(pre.predictedlbl.equalsIgnoreCase(act_lbl)) countcorrect++;
	else countincorrect++;
		
}
System.out.println("Correct labels"+ countcorrect + " Incorrect Labels "+ countincorrect);
System.out.println("Percentage"+ (double)countcorrect/(countcorrect+countincorrect));
	}

	private static void printPaths(TreeNode root, ArrayList<String> arrayList) {
		if(root.children.size()==0)
			System.out.println(arrayList);
		else
		{
			for (TreeNode node : root.children) {
				ArrayList<String>temp = new ArrayList<String>();
				temp.addAll(arrayList);
				temp.add(node.attrValue);
				printPaths(node, temp);
			}
		}
		
	}

	private static void BuildTree(TreeNode root) {

		if(root.attrValue.equalsIgnoreCase("Hispanic"))
			System.out.println();

		if(allVisited(root) || isPureValue(root) ){
			int countlt = 0,countgt = 0,countno=0;
			for (ArrayList<String> arr : root.records) {
				if(arr.get(5).equalsIgnoreCase("no"))
					countno++;
				if(arr.get(5).equalsIgnoreCase(">30"))
					countgt++;
				if(arr.get(5).equalsIgnoreCase("<30"))
					countlt++;
			}
			
			int max = Math.max(countno, Math.max(countlt, countgt));
			ArrayList<Boolean> newVisited = new ArrayList<Boolean>();
			newVisited.addAll(root.visitedAttr);
			String name="";
			if(max==countno)
				name = "No";
			if(max==countlt)
				name = "<30";
			if(max==countgt)
				name = ">30";
			TreeNode node = new TreeNode("readmitted",name,null,root.entropy,root.attrIndex,null,root.possibleValues);
			root.children.add(node);
			
			return;
		}
		else{
			
			String childAttrType = CalculateGain(root);
			int childindex = root.attrIndex.get(childAttrType);
			root.visitedAttr.remove(childindex);
			root.visitedAttr.add(childindex, true);
			
			
			ArrayList<String> attrValuesOfBestAttr = new ArrayList<String>();
			attrValuesOfBestAttr.addAll(root.possibleValues.get(childAttrType));

			for (String string : attrValuesOfBestAttr) {

				ArrayList<ArrayList<String>>childRecords = findRecords(root.records,string,childindex);
				ArrayList<Boolean> newVisited = new ArrayList<Boolean>();
				newVisited.addAll(root.visitedAttr);
				TreeNode node = new TreeNode(childAttrType,string,newVisited,root.entropy,root.attrIndex,childRecords,root.possibleValues);
				root.children.add(node);
			}

			for (TreeNode node : root.children) {
				if(node.attrValue.equals("Hispanic"))
					System.out.println();
				BuildTree(node);
			}


		}

	}

	public static String CalculateGain(TreeNode root){

		double entropyS = CalculateEntropy(root.records);
		//double[] gain = new double[3];
		double maxgain;
		double gain= 0;
		String maxAttri="";

		maxgain = gain;

		for (Entry<String, ArrayList<String>> entry: root.possibleValues.entrySet()) {
			double entropySum=0;
			String currAttr = entry.getKey();
			ArrayList<String>possibleValsOfAttr = entry.getValue();
			int index = root.attrIndex.get(currAttr);
			if(root.visitedAttr.get(index)){
				//	gain[index]=-1;
				continue;
			}
			for (String value : possibleValsOfAttr) {
				ArrayList<ArrayList<String>> sub = subSet(root.records,index,value);
				double entropy = CalculateEntropy(sub);
				entropySum+=entropy*((double)sub.size()/root.records.size());
			}
			gain = entropyS-entropySum;	
			if(gain>=maxgain){
				maxgain = gain;
				maxAttri = currAttr;
			}

		}
		//		
		//		int maxIndex=0;
		//		for (int i=0;i<gain.length;i++) {
		//			if(gain[maxIndex]< gain[i])
		//				maxIndex=i;
		//		}



		return maxAttri;
	}




	private static ArrayList<ArrayList<String>> subSet(ArrayList<ArrayList<String>> records, int index, String value) {

		ArrayList<ArrayList<String>>result = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> arr : records) {
			if(arr.get(index).equals(value))
				result.add(arr);
		}


		return result;
	}

	private static double CalculateEntropy(ArrayList<ArrayList<String>> records) {

		//calculate sample entropy
		
		if(records.size()==0)
			return 0.0;
		int countNO=0,countGT=0;
		double entropyS = 0;
		for (ArrayList<String> arr : records) {
			if(arr.get(5).equals("NO"))
				countNO++;
			if(arr.get(5).equals(">30"))
				countGT++;
		}
		int countLT = records.size()-countNO-countGT;
		
		
		double pNo = (double)countNO/records.size();
		double pGT = (double)countGT/records.size();
		double pLT = (double)countLT/records.size();
		if(pNo==1 || pGT==1 || pLT==1){
			//entropyS = (pYes==0)?((double)-pNo * (Math.log(pNo) / Math.log(2))):((double) -pYes * (Math.log(pYes) / Math.log(2)));
			if(pNo==1){
				entropyS = ((double)-pNo * (Math.log(pNo) / Math.log(2)));	
			}
			else if(pGT==1){
				entropyS = ((double)-pGT * (Math.log(pGT) / Math.log(2)));
			}
			else if(pLT==1){
				entropyS = ((double)-pLT * (Math.log(pLT) / Math.log(2)));
			}
		}
		else if(pNo==0){
			entropyS =  ((double)-pGT * (Math.log(pGT) / Math.log(2))) - ((double) pLT * (Math.log(pLT) / Math.log(2)));
		}
		else if(pGT==0){
			entropyS =  ((double)-pNo * (Math.log(pNo) / Math.log(2))) - ((double) pLT * (Math.log(pLT) / Math.log(2)));
		}
		else if(pLT==0){
			entropyS =  ((double)-pNo * (Math.log(pNo) / Math.log(2))) - ((double) pGT * (Math.log(pGT) / Math.log(2)));
		}	
		
		else 
			entropyS =(double) -pNo * (Math.log(pNo) / Math.log(2)) - pGT * (Math.log(pGT) / Math.log(2)) - pLT * (Math.log(pLT) / Math.log(2));
		return entropyS;

	}

	private static ArrayList<ArrayList<String>> findRecords(ArrayList<ArrayList<String>> records, String string,int index) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<String>>childRecords = new ArrayList<ArrayList<String>>();

		for (ArrayList<String> arrayList : records) {
			if(arrayList.get(index).equals(string))
				childRecords.add(arrayList);
		}


		return childRecords;
	}

	private static boolean isPureValue(TreeNode root) {
		if(root.attrType.equals("dummy"))
			return false;
		int countNo=0,countGT=0,countLT=0;
		for (ArrayList<String> arr: root.records) {
			if(arr.get(5).equals("NO"))
				countNo++;
			if(arr.get(5).equals(">30"))
				countGT++;
			if(arr.get(5).equals("<30"))
				countLT++;
		}
		if(countNo==root.records.size() || countGT==root.records.size() ||  countLT==root.records.size())
			return true;
		else
			return false;
	}

	private static boolean allVisited(TreeNode root) {
		for (Boolean bool : root.visitedAttr) {
			if(bool==false)
				return false;
		}
		return true;
	}

}

