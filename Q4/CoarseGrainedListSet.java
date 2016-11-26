package q4;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;


public class CoarseGrainedListSet implements ListSet {
    // you are free to add members
	HashSet<Integer> nodeSet;
	Node head;
	ReentrantLock lock = new ReentrantLock();
	
  public CoarseGrainedListSet() {
	  // implement your constructor here
	  nodeSet = new HashSet<Integer>();
	  head = new Node(0);
  }
  
  public boolean add(int value) {
	  // implement your add method here
	  lock.lock();
	  System.out.print("add " + value + "  ");
	  try {
		  if (nodeSet.contains(value)) {
			  System.out.println(false);
			  return false;
		  } else {
		  
			  Node p = head;
			  while (p.next != null && p.next.value < value) {
				  p = p.next;
			  }
			  
			  Node newNode = new Node(value);
			  newNode.next = p.next;
			  p.next = newNode;
			  
			  nodeSet.add(value);
			  
			  System.out.println(true);
			  return true;
		  }
	  } finally {
		  lock.unlock();
	  }
  }
  
  public boolean remove(int value) {
	  // implement your remove method here	
	  lock.lock();
	  System.out.print("remove " + value + "  ");
	  try {
		  if (!nodeSet.contains(value)) {
			  System.out.println(false);
			  return false;
		  } else {
		  
			  Node p = head;
			  while (p.next != null && p.next.value != value) {
				  p = p.next;
			  }
			  
			  Node tmp = p.next.next;
			  p.next = tmp;
			  nodeSet.remove(value);
			  System.out.println(true);
			  return true;
		  }
	  } finally {
		  lock.unlock();
	  }
  }
  
  public boolean contains(int value) {
	  // implement your contains method here
	  lock.lock();
	  System.out.print("contains " + value + "?  ");
	  boolean result = false;
	  try {
		  if (nodeSet.contains(value)) {
			  result = true;
		  }
		  System.out.println(result);
		  return result;
	  } finally {
		  lock.unlock();
	  }
  }
  
  protected class Node {
	  public Integer value;
	  public Node next;
		    
	  public Node(Integer x) {
		  value = x;
		  next = null;
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
