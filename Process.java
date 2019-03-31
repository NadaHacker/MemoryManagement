import java.util.*;
import java.lang.*;

public class Process {
    protected int pid;
    protected int pageRef;

    public Process(int pid, int pageRef) {
	this.pid = pid;
	this.pageRef = pageRef;
    }

    public int getPID(){ return this.pid;  }
    public int getPageRef(){ return this.pageRef; }
}
