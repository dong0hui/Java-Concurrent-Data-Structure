package stack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//MyStack is a pre-defined Interface
public class LockStack implements MyStack {
	private Node top;
	ReentrantLock lock;
	Condition empty;
	
	public LockStack() {
		this.top = new Node(null);
		this.lock = new ReentrantLock();
		this.empty = lock.newCondition();
	}
  
	public boolean push(Integer value) {
		if (value == null) return false;
		Node newNode = new Node(value);
		lock.lock();
		try {
			newNode.next = top;
			top = newNode;
			empty.signalAll();
		} finally {
			lock.unlock();
		}
		return true;
	}
  
	public Integer pop() throws EmptyStack {
		Integer res;
		lock.lock();
		try {
			while (this.size() == 0)
				try {
					empty.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			res = this.top.value;
			top = top.next;
		} finally {
			lock.unlock();
		}
		return res;
	}
  
	protected class Node {
		public Integer value;
		public Node next;	    
		public Node(Integer x) {
			value = x;
			next = null;
		}
	}
	
	public int size() {
		AtomicInteger count = new AtomicInteger(0);
		for (Node node = top; node != null; node = node.next)
			count.getAndIncrement();
		return count.decrementAndGet();
	}
	
	/*
	// Comment out when submit
	public static void main(String[] args) throws EmptyStack {
		LockStack list1 = new LockStack();
		Integer value = new Integer(3);
		list1.push(value);
		System.out.println(value+" is added. The size is "+list1.size());
		int value2 = 4;
		list1.push(value2);
		System.out.println(value2+" is added. The size is "+list1.size());
		Integer res = list1.pop();
		System.out.println("The top is "+res);
		res = list1.pop();
		System.out.println("The top is "+res);
		//will wait forever.
		//res = list1.pop();
		//System.out.println("The top is "+res);
	}
	*/
}
