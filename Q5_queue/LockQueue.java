package queue;
//Unbounded means there is no size limit on linked list
//I use Reentrant lock
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
	//Define parameters that Class LockQueue should implement on
	//private means only methods of LockQueue can implement on head and tail
	//We need a reentrant lock and a condition when to dequeue
	private Node head;
	private Node tail;
	ReentrantLock deqLock;
	Condition notEmpty;

	//Constructor: Initialize all the parameters when you define ...
	//... LockQueue objects in main program
	public LockQueue() {
		this.head = new Node(null);
		this.tail = head;
		this.deqLock = new ReentrantLock();
		this.notEmpty = deqLock.newCondition();
	}
  
	//If the linked list is empty, we need to define head = new node
	//If the linked list already has nodes, we need to add the new node to the tail
	//of the linked list
	public boolean enq(Integer value) {
		if (value == null) {
			return false;
		}
		Node newNode = new Node(value);
		tail.next = newNode;
		tail = newNode;
		//There is a node at least, should notify deq()
		deqLock.lock();
		try {
			notEmpty.signalAll();
		} finally {
			deqLock.unlock();
		}
		return true;
	}
  
	//If the linked list is empty, the thread has to wait until there is any nodes
	public Integer deq() {
		Integer res;
		deqLock.lock();
		try {
			while (this.size() == 0) {
				try {
					notEmpty.await();
				} catch (InterruptedException e) {}
			}
			res = head.next.value;
			head = head.next;
		} finally {
			deqLock.unlock();
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
		for (Node node = head; node != null; node = node.next)
			count.getAndIncrement();
		return count.decrementAndGet();
	}
	
	/* Comment out when submit
	public static void main(String[] args) {
		LockQueue list1 = new LockQueue();
		Integer value = new Integer(3);
		list1.enq(value);
		System.out.println(value+" is added. The size is "+list1.size());
		int value2 = 4;
		list1.enq(value2);
		System.out.println(value2+" is added. The size is "+list1.size());
		Integer res = list1.deq();
		System.out.println("The head is "+res);
		res = list1.deq();
		System.out.println("The head is "+res);
	}
	*/
}
