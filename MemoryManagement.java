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
    public static int numFrames;
    
    public static void main(String [] args) throws java.io.IOException {
	if (args.length != 2){
	    System.out.printf("USAGE: %s <input_file> <frames>\n",args[0]);
	    System.exit(0);
	}
        numFrames = Integer.parseInt(args[1]);
	// ArrayList to load input into from file
	ArrayList<Page> inputPages = new ArrayList<Page>();
	File inputFile = new File(args[0]);
	BufferedReader br = new BufferedReader(new FileReader(inputFile));
	if (inputFile.isFile() && inputFile.canRead()){
	    inputPages = loadInput(br);
	}
	int result = 0;
	result = runFIFO(inputPages, numFrames);
	System.out.printf("FIFO(w/ %d frames): # of page faults: %d\n", numFrames, result); 
	result = runLRU(inputPages, numFrames);
	System.out.printf("LRU(w/ %d frames): # of page faults: %d\n", numFrames, result); 
	result = runOPT(inputPages, numFrames);
	System.out.printf("OPT(w/ %d frames): # of page faults: %d\n", numFrames, result); 
    }

    // Function that returns the index of the next occurence of a chosen process, or return -1
    public static int getNextOccurrence(ArrayList<Page> input, Page p, int index){
	for (int i = index; i < input.size(); i++){
	    if ((p.getPID() == input.get(i).getPID()) && (p.getPageRef() == input.get(i).getPageRef())) { 
		return i;
	    }
	}
	return -1;
    }

    // Function to simulate the OPT memory management algorithm with a specified number of frames
    public static int runOPT(ArrayList<Page> input, int frames){
     	int numPageFaults = 0;
	ArrayList<Page> OPTlist = new ArrayList<>();
	for (int i = 0; i < input.size(); i++) {
	    if (OPTlist.size() < frames) {
		if (checkIfExists(OPTlist, input.get(i)) == -1) { //doesn't exist
		    Page current = new Page(input.get(i).getPID(), input.get(i).getPageRef());
		    current.setOptRef(getNextOccurrence(input, current, i+1));
		    OPTlist.add(current);
		    //System.out.printf("Adding page (%d,%d)\n", current.getPID(), current.getPageRef());
    		    numPageFaults++;
		}
		else {
		    OPTlist.get(checkIfExists(OPTlist, input.get(i))).setOptRef(getNextOccurrence(input, input.get(i), i+1));
		}
	    }
	    else if (checkIfExists(OPTlist, input.get(i)) > -1){
		OPTlist.get(checkIfExists(OPTlist, input.get(i))).setOptRef(getNextOccurrence(input, input.get(i), i+1));
	    }
	    else {
	        removeMaxOPTRef(OPTlist);
		Page current = new Page(input.get(i).getPID(), input.get(i).getPageRef());
		current.setOptRef(getNextOccurrence(input, current, i+1));
		OPTlist.add(current);
		//System.out.printf("Adding page (%d,%d)\n", current.getPID(), current.getPageRef());
		numPageFaults++;
	    }
	}
	return numPageFaults;
    }

    // Function that removes the element in the ArrayList that is used furthest in the future
    public static void removeMaxOPTRef(ArrayList<Page> list){
	Page max = list.get(0);
	//System.out.printf("max uptsize: %d \n", max.getOptRef());
	for (int i = 0; i < list.size(); i++){
	    if (list.get(i).getOptRef() == -1){
		//System.out.printf("       Removing page (%d,%d)\n", list.get(i).getPID(), list.get(i).getPageRef());
		list.remove(i);
		return;
	    }
	    if (list.get(i).getOptRef() > max.getOptRef()){
		//System.out.printf("max is now: pid=%d, with wtfsize=%d\n", list.get(i).getPID(), list.get(i).getOptRef());
		max = list.get(i);
	    }
	}
	//System.out.printf("Removing page (%d,%d)\n", list.get(list.indexOf(max)).getPID(), list.get(list.indexOf(max)).getPageRef());
	list.remove(list.indexOf(max));	
    }
    
    // public static int runOPT(ArrayList<Process> input, int frames){
    // 	int numPageFaults = 0;
    // 	ArrayList<Process> OPTlist = new ArrayList<>();
    //     ArrayList<Process> crystalBall = new ArrayList<>();
    // 	int i;
    // 	for (i = 0; i < input.size(); i++) {	   
    // 	    if (OPTlist.size() < frames) {
    // 		if (checkIfExists(OPTlist, input.get(i)) == -1) { 
    // 		    OPTlist.add(input.get(i));
    // 		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
    // 		    numPageFaults++;
    // 		}
    // 		else {
    // 		    int x = checkIfExists(OPTlist, input.get(i));
    // 		    System.out.printf("Removing page (%d,%d)\n", OPTlist.get(x).getPID(), OPTlist.get(x).getPageRef());
    // 		    OPTlist.remove(checkIfExists(LRUlist, input.get(i)));
    // 		    OPTlist.add(input.get(i));
    // 		    System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
    // 		}
    // 	    }
    // 	    else {
    // 		for (int j = 0; j < input.size(); j++){
    // 		    if ((input.get(i+1).getPID() == OPTlist.get(j).getPID()) && (input.get(i+1).getPageRef() == OPTlist.get(j).getpageRef())) {
    // 			crystalBall.add(OPTlist.get(j));
    // 		    }
    // 		    if (crystalBall.size() == OPTlist.size()){
    // 			OPTlist.remove(crystalBall.get(crystalBall.size()-1));
    // 			OPTlist.add(input.get(i));
    // 			numPageFaults++;
    // 		    }
    // 		    else {
    // 			for (int k = 0; k < OPTlist.size(); k++){
    // 			    for (int c = 0; c < crystalBall.size(); c++){
    // 				if ((crystalBall.get(i+1).getPID() == OPTlist.get(k).getPID()) && (crystallBall.get(i+1).getPageRef() == OPTlist.get(k).getpageRef())) {
    // 				    continue;
    // 				}
    // 			    }
    // 			}
    // 		    }
    // 		}
    // 	    }
    // 	}
    // }
    // public static int lookAhead(ArrayList<Process> input, ArrayList<Process> OPTlist, int index){
    // 	int result = -1;
    // 	int farthest = index;
    // 	for (int i = 0; i < OPTlist.size(); i++) {
    // 	    int j;
    // 	    for (j = index; j < input.size(); j++) {
    // 		if ((OPTlist.get(i).getPID() == input.get(j).getPID()) &&
    // 		    (OPTlist.get(i).getPageRef() == input.get(j).getPageRef())){
    // 		    if (j > farthest) {
    // 			farthest = j;
    // 			result = i;
    // 		    }
    // 		    break;
    // 		}
    // 	    }
    // 	    if (j == input.size()) {
    // 		return i;
    // 	    }
    // 	}
    // 	if (result == -1) {
    // 	    return 0;
    // 	}
    // 	else {
    // 	    return result;
    // 	}
    // }
    
    // public static int runOPT(ArrayList<Process> input, int frames){
    // 	int numPageFaults = 0;
    // 	ArrayList<Process> OPTlist = new ArrayList<>();
    // 	for (int i = 0; i < input.size(); i++){
    // 	    if (checkIfExists(OPTlist, input.get(i)) > -1) {   // Check that it doesn't exist
    // 		numPageFaults++;
    // 		continue;
    // 	    }

    // 	    if (OPTlist.size() < frames) {
    // 		//System.out.printf("adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
    // 		OPTlist.add(input.get(i));
    // 	    }
    // 	    else {
    // 		int j = lookAhead(input, OPTlist, i+1);
    // 		//System.out.printf("    removing page (%d,%d)\n", OPTlist.get(j).getPID(), OPTlist.get(j).getPageRef());
    // 		OPTlist.remove(j);
    // 		//System.out.printf("adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
    // 		OPTlist.add(input.get(i));
    // 	    }
    // 	}
    // 	return numPageFaults;
    // }

    // Function to simulate the LRU memory management algorithm with a specified number of frames
    public static int runLRU(ArrayList<Page> input, int frames){
	int numPageFaults = 0;
	ArrayList<Page> LRUlist = new ArrayList<>();
	for (int i = 0; i < input.size(); i++) {	   
	    if (LRUlist.size() < frames) {
		if (checkIfExists(LRUlist, input.get(i)) == -1) {   // Check that it doesn't exist
		    LRUlist.add(input.get(i));
		    //System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		} else {
		    int x = checkIfExists(LRUlist, input.get(i));
		    //System.out.printf("Removing page (%d,%d)\n", LRUlist.get(x).getPID(), LRUlist.get(x).getPageRef());
		    LRUlist.remove(checkIfExists(LRUlist, input.get(i)));
		    LRUlist.add(input.get(i));
		    //System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		}
	    } else {
		
		if (checkIfExists(LRUlist, input.get(i)) > -1) {
		    int x = checkIfExists(LRUlist, input.get(i));
		    //System.out.printf("Removing page (%d,%d)\n", LRUlist.get(x).getPID(), LRUlist.get(x).getPageRef());
		    //System.out.printf("Removing page (%d,%d)\n", LRUlist.get(checkIfExists(LRUlist, input.get(i))).getPID(), LRUlist.get(checkIfExists(LRUlist, input.get(i)).getPageRef()));
		    LRUlist.remove(checkIfExists(LRUlist, input.get(i)));
		    LRUlist.add(input.get(i));
		    //System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		} else {
		    //System.out.printf("Removing page (%d,%d)\n", LRUlist.get(0).getPID(), LRUlist.get(0).getPageRef());
		    LRUlist.remove(0);
		    LRUlist.add(input.get(i));
		    //System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
	    }
	}
	return numPageFaults;
    }

    // Function to simulate the FIFO memory management algorithm with a specified number of frames
    public static int runFIFO(ArrayList<Page> input, int frames){
	int numPageFaults = 0;
	Queue<Page> FIFOqueue = new LinkedList<>();
	for (int i = 0; i < input.size(); i++){
	    if (FIFOqueue.size() < frames) {
		if (!checkIfExists(FIFOqueue, input.get(i))) {
		    FIFOqueue.add(input.get(i));
		    //System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
	    }
	    else {
		if (!checkIfExists(FIFOqueue, input.get(i))) {
		    FIFOqueue.remove();
		    FIFOqueue.add(input.get(i));
		    //System.out.printf("Adding page (%d,%d)\n", input.get(i).getPID(), input.get(i).getPageRef());
		    numPageFaults++;
		}
	    }
	}
	return numPageFaults;
    }

    // Function to return the index of an element that exists in an ArrayList, or return -1 if doesn't exist
    public static int checkIfExists(ArrayList<Page> queue, Page p) {
	int counter = 0;
	for (Page item: queue){
	    if ((p.getPID() == item.getPID()) && (p.getPageRef() == item.getPageRef())) {
		return counter;
	    }
	    counter++;
	}
	return -1;
    }

    // Function to tell whether a process alredy exists in a queue
    public static boolean checkIfExists(Queue<Page> queue, Page p) {
	for (Page item: queue){
	    if ((p.getPID() == item.getPID()) && (p.getPageRef() == item.getPageRef())) { 
		return true;
	    }  
	}
	return false;
    }

    // Function to load input from a file to be store/return in an ArrayList
    public static ArrayList<Page> loadInput(BufferedReader br) throws java.io.IOException {
	ArrayList<Page> pageLoader = new ArrayList<Page>();
	String lineInput;
	while((lineInput = br.readLine()) != null){
	    pageLoader.add(new Page(getDigit(lineInput, "first"), getDigit(lineInput, "second")));
	}
	return pageLoader;
    }

    // Function to return the digit requested from the string input
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
