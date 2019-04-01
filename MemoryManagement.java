/*
File: <MemoryManagement.java>
Class: CS 470
Assignment: Memory Management Project
Programmers: Oti Oritsejafor, Jared Holzmeyer
*/

import java.util.*;
import java.lang.*;
import java.io.*;


public class MemoryManagement {
    public static String MemManType = "FIFO";
    public static int numFrames;
    
    public static void main(String [] args) throws java.io.IOException {
	if (args.length != 1){
	    System.out.printf("USAGE: %s <input_file> <quantum_size>\n",args[0]);
	    System.exit(0);
	}
        numFrames = 3;
	ArrayList<Process> inputProcesses = new ArrayList<Process>();
	File inputFile = new File(args[0]);
	BufferedReader br = new BufferedReader(new FileReader(inputFile));
	if (inputFile.isFile() && inputFile.canRead()){
	    inputProcesses = loadInput(br);
	}
	//int result = runLRU(inputProcesses, numFrames);
	//int result = runFIFO(inputProcesses, numFrames);
	int result = runOPT(inputProcesses, numFrames);
	System.out.printf("Number page faults with %d frames: %d\n", numFrames, result); 
    }
    public static int runOPT(ArrayList<Process> input, int frames){
	int numPageFaults = 0;
	ArrayList<Process> OPTlist = new ArrayList<>();
        ArrayList<Process> crystalBall = new ArrayList<>();
	int i;
	for (i = 0; i < input.size(); i++) {	   
	    if (OPTlist.size() < frames) {
		if (checkIfExists(OPTlist, input.get(i)) == -1) { 
		    OPTlist.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
		else {
		    int x = checkIfExists(OPTlist, input.get(i));
		    System.out.printf("Removing page (%d,%d)\n", OPTlist.get(x).getPID(), OPTlist.get(x).getPageRef());
		    OPTlist.remove(checkIfExists(LRUlist, input.get(i)));
		    OPTlist.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		}
	    }
	    else {
		for (int j = 0; j < input.size(); j++){
		    if ((input.get(i+1).getPID() == OPTlist.get(j).getPID()) && (input.get(i+1).getPageRef() == OPTlist.get(j).getpageRef())) {
			crystalBall.add(OPTlist.get(j));
		    }
		    if (crystalBall.size() == OPTlist.size()){
			OPTlist.remove(crystalBall.get(crystalBall.size()-1));
			OPTlist.add(input.get(i));
			numPageFaults++;
		    }
		    else {
			for (int k = 0; k < OPTlist.size(); k++){
			    for (int c = 0; c < crystalBall.size(); c++){
				if ((crystalBall.get(i+1).getPID() == OPTlist.get(k).getPID()) && (crystallBall.get(i+1).getPageRef() == OPTlist.get(k).getpageRef())) {
				    continue;
				}
			    }
		    }
		}
	    }
	}
    
    public static int lookAhead(ArrayList<Process> input, ArrayList<Process> OPTlist, int index){
	int result = -1;
	int farthest = index;
	for (int i = 0; i < OPTlist.size(); i++) {
	    int j;
	    for (j = index; j < input.size(); j++) {
		if ((OPTlist.get(i).getPID() == input.get(j).getPID()) && (OPTlist.get(i).getPageRef() == input.get(j).getPageRef())){
		    if (j > farthest) {
			farthest = j;
			result = i;
		    }
		    break;
		}
	    }
	    if (j == input.size()) {
		return i;
	    }
	}
	if (result == -1) {
	    return 0;
	}
	else {
	    return result;
	}
    }

    public static int runOPT(ArrayList<Process> input, int frames){
	int numPageFaults = 0;
	ArrayList<Process> OPTlist = new ArrayList<>();
	for (int i = 0; i < input.size(); i++){
	    if (checkIfExists(OPTlist, input.get(i)) > -1) {   // Check that it doesn't exist
		numPageFaults++;
		continue;
	    }

	    if (OPTlist.size() < frames) {
		System.out.printf("adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		OPTlist.add(input.get(i));
	    }
	    else {
		int j = lookAhead(input, OPTlist, i+1);
		System.out.printf("    removing page (%d,%d)\n", OPTlist.get(j).getPID(), OPTlist.get(j).getPageRef());
		OPTlist.remove(j);
		System.out.printf("adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		OPTlist.add(input.get(i));
	    }
	}
	return numPageFaults;
    }
    
    public static int runLRU(ArrayList<Process> input, int frames){
	int numPageFaults = 0;
	ArrayList<Process> LRUlist = new ArrayList<>();
	for (int i = 0; i < input.size(); i++) {	   
	    if (LRUlist.size() < frames) {
		if (checkIfExists(LRUlist, input.get(i)) == -1) {   // Check that it doesn't exist
		    LRUlist.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		} else {
		    int x = checkIfExists(LRUlist, input.get(i));
		    System.out.printf("Removing page (%d,%d)\n", LRUlist.get(x).getPID(), LRUlist.get(x).getPageRef());
		    LRUlist.remove(checkIfExists(LRUlist, input.get(i)));
		    LRUlist.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		}
	    } else {
		
		if (checkIfExists(LRUlist, input.get(i)) > -1) {
		    int x = checkIfExists(LRUlist, input.get(i));
		    System.out.printf("Removing page (%d,%d)\n", LRUlist.get(x).getPID(), LRUlist.get(x).getPageRef());
		    //System.out.printf("Removing page (%d,%d)\n", LRUlist.get(checkIfExists(LRUlist, input.get(i))).getPID(), LRUlist.get(checkIfExists(LRUlist, input.get(i)).getPageRef()));
		    LRUlist.remove(checkIfExists(LRUlist, input.get(i)));
		    LRUlist.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		} else {
		    System.out.printf("Removing page (%d,%d)\n", LRUlist.get(0).getPID(), LRUlist.get(0).getPageRef());
		    LRUlist.remove(0);
		    LRUlist.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
	    }
	}
	return numPageFaults;
    }
    
    public static int runFIFO(ArrayList<Process> input, int frames){
	int numPageFaults = 0;
	Queue<Process> FIFOqueue = new LinkedList<>();
	for (int i = 0; i < input.size(); i++){
	    if (FIFOqueue.size() < frames) {
		if (!checkIfExists(FIFOqueue, input.get(i))) {
		    FIFOqueue.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
	    }
	    else {
		if (!checkIfExists(FIFOqueue, input.get(i))) {
		    FIFOqueue.remove();
		    FIFOqueue.add(input.get(i));
		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
	    }
	}
	return numPageFaults;
    }
    
    public static int checkIfExists(ArrayList<Process> queue, Process p) {
	int counter = 0;
	for (Process item: queue){
	    if ((p.getPID() == item.getPID()) && (p.getPageRef() == item.getPageRef())) { 
		return counter;
	    }
	    counter        ++;
	}
	return -1;
    }
    
    public static boolean checkIfExists(Queue<Process> queue, Process p) {
	for (Process item: queue){
	    if ((p.getPID() == item.getPID()) && (p.getPageRef() == item.getPageRef())) { 
		return true;
	    }  
	}
	return false;
    }
    
    public static ArrayList<Process> loadInput(BufferedReader br) throws java.io.IOException {
	ArrayList<Process> processLoader = new ArrayList<Process>();
	String lineInput;
	while((lineInput = br.readLine()) != null){
	    processLoader.add(new Process(getDigit(lineInput, "first"), getDigit(lineInput, "second")));
	}
	return processLoader;
    }

    public static int getDigit(String command, String index){
	int res = 0;
        Scanner sc = new Scanner(command);
	res = sc.useDelimiter("\\D+").nextInt();
	if (index.equals("second")){
	    res = sc.useDelimiter("\\D+").nextInt();
	}
	return res;
    }
}
