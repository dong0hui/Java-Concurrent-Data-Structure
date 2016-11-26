package stack;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack implements MyStack {
	AtomicReference<Node> top;
	
	public LockFreeStack() {
		top = new AtomicReference<Node>(null);
	}
	
	//For Backoff class, let the thread sleep for some time
	static final int MIN_DELAY = 1;
	static final int MAX_DELAY = 100;
	Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
	
	//The core is pushed(newNode)--compareAndSet
	public boolean push(Integer value) {
		Node newNode = new Node(value);
		while (true) {
			if (pushed(newNode)) {
				return true;
			} else {
				try {
					backoff.backoff(); //let the thread to sleep for certain amount of time
										//between minDelay and maxDelay
				} catch (InterruptedException e) {
					//do nothing here
				}
			}
		}
	}
  
	public Integer pop() throws EmptyStack {
		while (true) { //keep trying to pop
			Node res = poped(); //res could be null or a node
			if (res != null) { //successfully pop
				return res.value;
			} else {
				try {
					backoff.backoff(); //thread sleep for a while
				} catch (InterruptedException e) {
					//do nothing
				}
			}
		}
	}
  
	//below 4 methods are ingredients
	protected class Node {
		public Integer value;
		public Node next;
		public Node(Integer x) {
			value = x;
			next = null;
		}
	}
	
	protected boolean pushed(Node newNode) {
		Node oldTop = top.get(); //oldTop is thread private
		newNode.next = oldTop; //at this step, top.get() may have been updated by other thread(s)
		return(top.compareAndSet(oldTop, newNode)); //if top.get() is oldTop, then update to newNode
	}
	
	protected Node poped() throws NoSuchElementException {
		Node oldTop = top.get();
		if (oldTop == null) throw new NoSuchElementException();
		Node newTop = oldTop.next; //move new top to second to the toppest node
		if(top.compareAndSet(oldTop, newTop)) { //if top.get() is still oldTop, you can pop and move down newTop
			return oldTop;
		} else {
			return null;
		}
	}
	
	public class Backoff {
		final int minDelay, maxDelay;
		int limit;
		final Random random;
		public Backoff(int min, int max) {
			minDelay = min;
			maxDelay = max;
			limit = minDelay;
			random = new Random();
		}
		public void backoff() throws InterruptedException {
			int delay = random.nextInt(limit);
			limit = Math.min(maxDelay,  2*limit);
			Thread.sleep(delay);
		}
	}
	
	/*
	public static void main (String[] args) throws EmptyStack {
		LockFreeStack stack = new LockFreeStack();
		Integer value = 37;
		stack.push(value);
		System.out.println(value+" is pushed on top of the stack.");
		value = 131;
		stack.push(value);
		System.out.println(value+" is pushed on top of the stack.");
		Integer res;
		res = stack.pop();
		System.out.println(res + " is popped from top of the stack.");
		res = stack.pop();
		System.out.println(res + " is popped from top of the stack.");
	}
	*/
}
