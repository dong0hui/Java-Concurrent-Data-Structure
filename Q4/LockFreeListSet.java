package q4;

import java.util.concurrent.atomic.AtomicReference;


public class LockFreeListSet implements ListSet {
    // you are free to add members
//	HashSet<Integer> nodeSet;
	AtomicReference<Node> head;
	AtomicReference<Node> tail;
	
    public LockFreeListSet() {
        // implement your constructor here
    	head = new AtomicReference<Node>(new Node(Integer.MIN_VALUE));
//  	    tail = head;
    }
	  
    public boolean add(int value) {
	    // implement your add method here
    	System.out.println("add " + value + "  ");
    	Node newNode = new Node(value);
    	while (true) {
    		Node pre = head.get();
    		Node cur = head.get().next.get();
    		while (cur != null && cur.value <= value) {
    			if (cur.value == value) {
					return false;
				}
  	  	    	pre = cur;
  	  	    	cur = cur.next.get();
  	  	    }
    		
    		if (pre.value == value) {
    			return false;
    		}
    		
    		//if ((pre != null && cur != null && pre.value < value && cur.value > value) ||
    			//	(cur == null)) {
    			if (pre.next.compareAndSet(cur, newNode) && 
    					newNode.next.compareAndSet(null, cur)){
    				return true;
    			}
    		//}
    	}
    }
	  
    public boolean remove(int value) {
    	// implement your remove method here
    	System.out.println("remove " + value + "  ");
    	while (true) {
    		Node pre = head.get();
    		Node cur = head.get().next.get();
    		while (cur != null && cur.value < value) {
  	  	    	pre = cur;
  	  	    	cur = cur.next.get();
  	  	    }
    		
    		if (cur == null || cur.value > value) {
    			return false;
    		}
    		
    		if ((pre != null && cur != null && cur.value == value)) {
    			if (pre.next.compareAndSet(cur, cur.next.get())){
    				return true;
    			}
    		}
    	}
    }
	  
    public boolean contains(int value) {
    	// implement your contains method here
    	System.out.println("contains " + value + "  ");
    	while (true) {
    		Node pre = head.get();
    		Node cur = head.get().next.get();
    		while (cur != null && cur.value < value) {
  	  	    	pre = cur;
  	  	    	cur = cur.next.get();
  	  	    }
    		
    		if (cur == null || cur.value > value) {
    			return false;
    		}
    		
    		if ((pre != null && cur != null && cur.value == value)) {
    			if (pre.next.compareAndSet(cur, cur)){
    				return true;
    			}
    		}
    	}
    }
	  
    protected class Node {
	    public Integer value;
	    public AtomicReference<Node> next;
			    
	    public Node(Integer x) {
		    value = x;
		    next = new AtomicReference<Node>(null);
	    }
    }
    
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        Node p = head.get();
    	while (p.next.get() != null) {
    		sb.append(p.next.get().value);
    		sb.append(" ");
    		p = p.next.get();
    	}
    	  
    	return sb.toString();
    }
}
