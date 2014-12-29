import java.util.HashMap;


public class Prediction {

	String race;
	String gender;
	String A1Cresult;
	String metformin;
	String diabetesMed;
	String predictedlbl;
	HashMap<String, String> map = new HashMap<String, String>();
	
	
	public void setRace(String race) {
		this.race = race;
		this.map.put("race", this.race);
	}
	public void setGender(String gender) {
		this.gender = gender;
		this.map.put("gender", this.gender);

	}
	public void setA1Cresult(String a1Cresult) {
		A1Cresult = a1Cresult;
		this.map.put("A1Cresult", this.A1Cresult);

	}
	public void setMetformin(String metformin) {
		this.metformin = metformin;
		this.map.put("metformin", this.metformin);

	}
	public void setDiabetesMed(String diabetesMed) {
		this.diabetesMed = diabetesMed;
		this.map.put("diabetesMed", this.diabetesMed);

	}
	
	public void predict(TreeNode root){
		if(root == null){
			return;
		}
		if(root.children.size()==0){
			System.out.println("PANIC!!");
		}
		String attribute = root.children.get(0).attrType;
		//check termination:
		if(attribute.equalsIgnoreCase("readmitted")){
			//System.out.println("PREDICTION IS "+root.children.get(0).attrValue);
			predictedlbl=root.children.get(0).attrValue;
			return;
		}
		
		String attributeValue = map.get(attribute);
		for(TreeNode n:root.children){
			if(n.attrValue.equals(attributeValue)){
				predict(n);
				return;
			}
		}
		
	}
	
	
	
	
	}
