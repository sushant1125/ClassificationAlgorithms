
import java.util.ArrayList;
import java.util.HashMap;

public class TreeNode {
	String attrType;
	String attrValue;
	ArrayList<Boolean>visitedAttr = new ArrayList<Boolean>();
	ArrayList<Double>entropy = new ArrayList<Double>();
	ArrayList<TreeNode>children = new ArrayList<TreeNode>();
	ArrayList<ArrayList<String>> records = new ArrayList<>();
	HashMap<String, ArrayList<String>> possibleValues = new HashMap<String, ArrayList<String>>();
	
	
	HashMap<String, Integer>attrIndex = new HashMap<String, Integer>();
	public TreeNode(String attrType,String attrValue,ArrayList<Boolean>visitedAttr,ArrayList<Double>entropy,HashMap<String, Integer>attrIndex,ArrayList<ArrayList<String>>records,HashMap<String, ArrayList<String>> possibleValues) {
		// TODO Auto-generated constructor stub
		this.attrType = attrType;
		this.attrValue =attrValue;
		this.visitedAttr = visitedAttr;
		this.entropy=entropy;
		this.attrIndex = attrIndex;
		this.records = records;
		this.possibleValues=possibleValues;
	}
}
