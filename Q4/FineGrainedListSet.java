package q4;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;



public class FineGrainedListSet implements ListSet {
    // you are free to add members
	HashSet<Integer> nodeSet;
	Node head;
	Node tail;
	ReentrantLock lock = new ReentrantLock();
	
    public FineGrainedListSet() {
        // implement your constructor here
    	nodeSet = new HashSet<Integer>();
  	    head = new Node(0);
  	    tail = head;
    }
	  
    public boolean add(int value) {
        // implement your add method here
    	lock.lock();
  	    System.out.println("add " + value + "  ");
  	    try {
  	    	if (nodeSet.contains(value)) {
  			    return false;
  		    }
  	    } finally {
  	    	lock.unlock();
  	    }

  	    
  	    Node pre = head;
  	    Node cur = head.next;
  	    Node newNode = new Node(value);
  	    pre.nodeLock.lock();
  	    if (cur == null) {
  	    	try {
  	    		pre.next = newNode;
  	  	    	nodeSet.add(value);
  	  	    	return true;
  	    	} finally {
  	    		pre.nodeLock.unlock();
  	    	}
  	    	
  	    } else {
  	    	cur.nodeLock.lock();
  	    }
  	    
  	    try {
  	    	while (cur != null && cur.value < value) {
  	  	    	Node oldPre = pre;
  	  	    	pre = cur;
  	  	    	cur = cur.next;
  	  	    	oldPre.nodeLock.unlock();
  	  	    	if (cur != null)
  	  	    		cur.nodeLock.lock();
  	  	    }
  	    	
  	    	if (cur == null) {
  	    		try {
  	  	    		pre.next = newNode;
  	  	  	    	nodeSet.add(value);
  	  	  	    	return true;
  	  	    	} finally {
  	  	    		pre.nodeLock.unlock();
  	  	    	}
  	    	}
  	  	    
  	    	if (!nodeSet.contains(value)) {
  	    		pre.next = newNode;
  	  	  	    newNode.next = cur;
  	  	  	    nodeSet.add(value);
  	  	  	    return true;
  	    	} else {
  	    		return false;
  	    	}
  	  	    
  	    } finally {
  	    	if (cur != null && cur.nodeLock.isLocked())
  	    		cur.nodeLock.unlock();
  	    	if (pre != null && pre.nodeLock.isLocked())
  	    		pre.nodeLock.unlock();
  	    }
    }
  	    
    public boolean remove(int value) {
        // implement your remove method here	
    	lock.lock();
  	    System.out.println("remove " + value + "  ");
  	    try {
  	    	if (!nodeSet.contains(value)) {
  			    return false;
  		    }
  	    } finally {
  	    	lock.unlock();
  	    }
  	    
  	    Node pre = head;
	    Node cur = head.next;
	    pre.nodeLock.lock();
	    cur.nodeLock.lock();
	    
	    try {
	    	while (cur != null && cur.value != value) {
		    	Node oldPre = pre;
	  	    	pre = cur;
	  	    	cur = cur.next;
	  	    	oldPre.nodeLock.unlock();
	  	    	if (cur != null)
	  	    		cur.nodeLock.lock();
		    }
	    	
	    	if (cur == null) {
	    		return false;
	    	}
		    Node next = cur.next;
		    if (next != null) {
		    	if (cur.value == value) {
		    		next.nodeLock.lock();
				    pre.next = next;
				    next.nodeLock.unlock();
		    	}
		    } else {
		    	if (cur.value == value) {
		    		pre.next = null;
		    	}
		    }
		    
		    nodeSet.remove(value);
		    return true;
	    } finally {
	    	if (cur != null && cur.nodeLock.isLocked())
	    		cur.nodeLock.unlock();
	    	if (pre != null && pre.nodeLock.isLocked())
	    		pre.nodeLock.unlock();
	    }
    }
	  
    public boolean contains(int value) {
        // implement your contains method here	
    	lock.lock();
  	    System.out.println("contains " + value + "?  ");
  	    boolean result = false;
  	    try {
  		    if (nodeSet.contains(value)) {
  			    result = true;
  		    }
  		    return result;
  	    } finally {
  	  	  lock.unlock();
  	    }
    }
	  
    protected class Node {
        public Integer value;
	    public Node next;
	    ReentrantLock nodeLock;
			    
	    public Node(Integer x) {
		    value = x;
		    next = null;
		    nodeLock = new ReentrantLock();
	    }
    }
    
    public String toString() {
  	  StringBuilder sb = new StringBuilder();
  	  Node p = head;
  	  while (p.next != null) {
  		  sb.append(p.next.value);
  		  sb.append(" ");
  		  p = p.next;
  	  }
  	  
  	  return sb.toString();
    }
}
