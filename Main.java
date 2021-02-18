

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	static String[] tempalphabet = new String[10];
	static String[] tempstates = new String[10];
	static String[] temptrans = new String[30];
	static String[] tempacceptstates = new String[10];
	static String[] alphabet;
	static String[] states;
	static String[] trans;
	static String[] acceptstates;
	static String[][] transfunc;
	static String startstate = "";
	static int alphabetlength = 0;
	static int statelength = 0;
	static int translength = 0;

	public static void main(String[] args) {
		String file[] = new String[20];
		fileread(file);
		filltemps(file);
		alphabetlength = findlength(tempalphabet); 
		tempalphabet[alphabetlength] = "£"; //add empty string
		statelength = findlength(tempstates) + 2; //adding GNFA states
		alphabetlength = findlength(tempalphabet);
		translength = findlength(temptrans);
		addtrans();
		alphabet = new String[alphabetlength];
		states = new String[statelength];
		trans = new String[translength];
		fillarrays();
		transfunc = new String[statelength][statelength];
		filltransfunction();
		steps();
		regex(transfunc);
	}

	public static void fileread(String file[]) {
		try {
			File myObj = new File("DFA.txt");
			Scanner myReader = new Scanner(myObj);
			int i = 0;
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				file[i] = data;
				i++;
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void filltemps(String[] file) {
		String[] temp;
		int a = 0;
		for (int i = 0; i < file.length; i++) {
			if (file[i] != null) {
				String val = file[i];
				char definer = val.charAt(0);
				if (definer == 'S') {
					temp = val.split("=");
					startstate = temp[1];
				}
				else if (definer == 'A') {
					temp = val.split("=");
					String[] temparr = temp[1].split(",");
					for (int j = 0; j < temparr.length; j++) {
						tempacceptstates[j] = temparr[j];
					}
				}
				else if (definer == 'E') {
					temp = val.split("=");
					String[] temparr = temp[1].split(",");
					for (int j = 0; j < temparr.length; j++) {
						tempalphabet[j] = temparr[j];
					}
				}
				else if (definer == 'Q') {
					temp = val.split("=");
					String[] temparr = temp[1].split(",");
					for (int j = 0; j < temparr.length; j++) {
						tempstates[j] = temparr[j];
					}
				}
				else {
					temptrans[a] = val;
					a++;
				}
			}
		}
	}

	public static int findlength(String[] arr) {
		int length = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				length++;
			}
		}
		return length;
	}

	static public void addtrans() {
		String starttrans = "qs" + ",£=" + startstate;
		temptrans[translength] = starttrans;
		translength++;
		String accepttrans = "";
		for (int i = 0; i < tempacceptstates.length; i++) {
			if (tempacceptstates[i] != null) {
				accepttrans = tempacceptstates[i] + ",£=qa";
				temptrans[translength] = accepttrans;
				translength++;
			}
		}
	}

	public static void fillarrays() {
		for (int i = 0; i < alphabetlength; i++) {
			alphabet[i] = tempalphabet[i];
		}
		states[0] = "qs";
		for (int i = 1; i < statelength; i++) {
			states[i] = tempstates[i-1];
		}
		states[statelength-1] = "qa";
		for (int i = 0; i < translength; i++) {
			trans[i] = temptrans[i];
		}
	}

	public static void filltransfunction()
	{
		int i = 0;
		int j = 0;
		for(i=0; i< states.length;i++)
		{
			for(j=0; j< states.length;j++)
			{
				transfunc[i][j]= null;
				for(int ed=0;ed<alphabet.length;ed++)
				{
					if(trans(states[i]+","+alphabet[ed]+"="+states[j])==true) 
					{
						if(transfunc[i][j] == null)
							transfunc[i][j] = alphabet[ed];
						else
							transfunc[i][j] = transfunc[i][j] + "U" + alphabet[ed];
					}
				}
			}
		}
	}

	public static boolean trans(String transaction)
	{
		boolean flag = false;
		for(int i=0;i<trans.length;i++)
		{
			if(trans[i].equals(transaction))
				flag = true;
		}
		return flag;
	}

	public static void regex(String[][] array)
	{
		String star = null;
		for(int i = 1; i < array.length - 1; i++) { 
			if(array[i][i] != null){
				star = array[i][i];
				array[i][i] = null;
			}
			for(int j = 0; j < array.length; j++){ 
				if(array[i][j] != null){
					if(star != null && i != j){
						array[i][j] = "( " + star + " )* " + array[i][j];
					}
					for(int k = 0; k < array.length; k++){
						if(array[k][i] != null && k != i){
							if(array[k][j] == null){
								array[k][j] = array[k][i] + " " + array[i][j];
							}
							else{
								array[k][j] = array[k][j] + " U " + array[k][i] + " " + array[i][j] + "";
							}
						}
					}
				}
				array[i][j] = null;
			}
			for(int k = 0; k < array.length; k++){
				array[k][i] = null;
			} 
			steps();
			star = null;
		}
		System.out.println(array[0][statelength-1]); //print result
	}

	public static void  steps()
	{
		System.out.println();
		for (int i = 0; i < states.length; i++) 
		{
			for (int j = 0; j < states.length; j++) 
			{
				if(transfunc[i][j] != null) {
					System.out.println("q" + i + "," + transfunc[i][j] + "=q" + j);
				}
			}
		}
		System.out.println();
	}
}