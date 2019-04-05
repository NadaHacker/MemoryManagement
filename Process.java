/*
File: <Process.java>
Class: CS 470
Assignment: Memory Management Project
Programmers: Oti Oritsejafor, Jared Holzmeyer
*/

import java.util.*;
import java.lang.*;

public class Process {
    protected int pid;
    protected int pageRef;
    protected int OptRef;

    public Process(int pid, int pageRef) {
	this.pid = pid;
	this.pageRef = pageRef;
    }

    public int getPID(){ return this.pid;  }
    public int getPageRef(){ return this.pageRef; }
    public int getOptRef(){ return this.OptRef; }
    public void setOptRef(int nextOccurrence){ OptRef = nextOccurrence; } 
}
