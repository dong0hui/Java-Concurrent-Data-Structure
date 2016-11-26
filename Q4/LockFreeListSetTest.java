package q4;

//import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class LockFreeListSetTest {
	static LockFreeListSet list = new LockFreeListSet();

	@Test
	public void test() throws InterruptedException {
		MyThread[] threadArray = new MyThread[6];
		
		
		for (int i = 0; i < 2; i++) {
			threadArray[i] = new AddThread();
		}
		
		for (int j = 2; j < 4; j++) {
		    threadArray[j] = new RemoveThread();
		}
		
		for (int k = 4; k < 6; k++) {
			threadArray[k] = new ContainsThread();
		}
		
		Thread[] t = new Thread[6];
		for (int l = 0; l < 6; l++) {
			t[l] = new Thread(threadArray[l]);
		}
		
		for (int n = 0; n < 6; n++) {
			t[n].start();
		}
		
		for (int m = 0; m < 6; m++) {
			t[m].join();
		}
		
		System.out.println(list.toString());
	}

	public class MyThread implements Runnable{
		public void run() {
			System.out.println("MyThread");
		}
	}
	
	public class AddThread extends MyThread implements Runnable {
		public void run() {
			Random r = new Random();
			for (int i = 0; i < 5; i++) {
				int value = r.nextInt(10);
				list.add(value);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class RemoveThread extends MyThread implements Runnable {
		public void run() {
			Random r = new Random();
			for (int i = 0; i < 5; i++) {
				int value = r.nextInt(10);
				list.remove(value);
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class ContainsThread extends MyThread implements Runnable {
		public void run() {
			Random r = new Random();
			for (int i = 0; i < 5; i++) {
				int value = r.nextInt(10);
				list.contains(value);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
