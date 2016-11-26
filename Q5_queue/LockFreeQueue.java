package queue;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {
	//different from "private Node head"
	//These objects can only be operated atomically
	//Why? We need compareAndSet later.
	private AtomicReference<Node> head;
	private AtomicReference<Node> tail;
	
	public LockFreeQueue() {
		Node initNode = new Node(null);
		this.head = new AtomicReference<Node>(initNode);
		this.tail = new AtomicReference<Node>(initNode);
	}

	//Anyone could resolve AtomicReference[] target and (AtomicReference<Integer>[]) target?
	@SuppressWarnings("unchecked")
	public boolean enq(Integer value) {
		if (value == null) throw new NullPointerException();
		
		Node node = new Node(value);
		
		while(true) { //Keep trying until successfully add a node
			//Note Node is defined using AtomicReference, so that one can use method get()
			//If AtomicReference is not used in Node class, just use Node last = tail, Node next = last.next;
			Node last = tail.get();
			Node next = last.next.get(); 
			if (last == tail.get()) { //tail has not been modified by other threads
				@SuppressWarnings("rawtypes")
				AtomicReference[] target = {last.next, tail};
				Node[] expect = {next, last}; //expect values-"currently should-be values before compareAndSet"
				Node[] update = {node, node}; //update values-"to-be values after compareAndSet"
				if(multiCompareAndSet(
						target,
						expect, 
						update)) {
					return true;
				}
			}
		}
	}
  
	
	public Integer deq() {
		while (true) {
			Node first = head.get();
			Node last = tail.get();
			Node next = first.next.get();
			if(first == head.get()) {
				if (first == last) {
					if (next == null) {
						throw new NoSuchElementException();
					}
					tail.compareAndSet(last, next);
				} else {
					Integer value = next.value;
					if(head.compareAndSet(first, next))
						return value;
				}
			}
		}
	}
  
	protected class Node {
		public Integer value;
		public AtomicReference<Node> next;
		
		public Node(Integer value2) {
			value = value2;
			next = new AtomicReference<Node>(null);
		}
	}
	
	private static boolean multiCompareAndSet (
			AtomicReference<Node>[] target,
			Node[] expect,
			Node[] update) {
		if (target[0].compareAndSet(expect[0], update[0])) {
			if(target[1].compareAndSet(expect[1], update[1])) {
				return true;
			} else {
				target[0].compareAndSet(update[0], expect[0]); //backfire: restore target[0]
				return false;
			}
		} else {
			return false;
		}
	}
	
	/*
	public static void main(String[] args) {
		LockFreeQueue list = new LockFreeQueue();
		Integer value = 25;
		list.enq(value);
		System.out.println(value+" is added to queue.");
		value = 31;
		list.enq(value);
		System.out.println(value+" is added to queue.");
		Integer res = list.deq();
		System.out.println(res+" is dequeued.");
		res = list.deq();
		System.out.println(res+" is dequeued.");	
	}
	*/
}
