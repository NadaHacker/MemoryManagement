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
        numFrames = 15;
	ArrayList<Process> inputProcesses = new ArrayList<Process>();
	File inputFile = new File(args[0]);
	BufferedReader br = new BufferedReader(new FileReader(inputFile));
	if (inputFile.isFile() && inputFile.canRead()){
	    inputProcesses = loadInput(br);
	}
	int result = runLRU(inputProcesses, numFrames);
	//int result = runFIFO(inputProcesses, numFrames);
	System.out.printf("Number page faults with %d frames: %d\n", numFrames, result); 
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
