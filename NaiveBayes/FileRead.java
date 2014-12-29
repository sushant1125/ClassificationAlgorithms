package hw4;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileRead {
	public static ArrayList<String[]> readFile(String fileName) throws IOException{
		//String[][] file = new String[100][100];
		ArrayList <String []> file= new ArrayList();
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		br.readLine();
		String line = br.readLine();
		int i=0;
		while(line!=null){
			String split[] = line.split(",");
			file.add(split);
			line=br.readLine();
		}
		return file;
	}
}
