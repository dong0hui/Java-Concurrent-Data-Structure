package q3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Garden {
	//DEBUG == 1 in debug mode
	static int DEBUG = 1;
	//If MAX_HOLES is not specified, the program will run forever.
	final static int MAX_HOLES = 30;
	int maxUnfilledHoles;
	public static AtomicInteger holes;
	public static AtomicInteger seeds;
	public static AtomicInteger filled;
	ReentrantLock lock;
	Condition noHole;
	Condition noSeed;
	Condition fast;

	public Garden(int n){
		this.maxUnfilledHoles = n;
		Garden.holes = new AtomicInteger(0);
		Garden.seeds = new AtomicInteger(0);
		Garden.filled = new AtomicInteger(0);
		this.lock = new ReentrantLock();
		this.noHole = lock.newCondition();
		this.noSeed = lock.newCondition();
		this.fast = lock.newCondition();
	}
	
	//Newton needs to wait for Mary before dig()
	public void startDigging() throws InterruptedException{
        lock.lock();
        try {
        	while(holes.get()- filled.get() >= maxUnfilledHoles) {
        		if (DEBUG == 1) {
        			System.out.println(Thread.currentThread().getId()+" Newton needs to wait for Mary to catch up.");
        		}
        		fast.await();
        	}
        } finally {
        	lock.unlock();
        }
	}
	
	//Newton needs to signal Benjamin after dig()
	public void doneDigging(){
		lock.lock();
		try {
			if(holes.get() > seeds.get()) {
				if (DEBUG == 1) {
					System.out.println(Thread.currentThread().getId()+" Newton has digged new hole, Benjamin can seed");
				}
				noHole.signalAll();
			}
		} finally {
			lock.unlock();
		} 
	} 
	
	//Benjamin needs to wait for Newton before seed()
	public void startSeeding() throws InterruptedException{
        lock.lock();
        try {
        	while (holes.get() <= seeds.get()) {
        		if (DEBUG == 1) {
        			System.out.println(Thread.currentThread().getId()+" Benjamin is waiting for Newton to dig new holes.");
        		}
        		noHole.await();
        	}
        } finally {
        	lock.unlock();
        }
	}
	
	//Benjamin needs to notify Mary to fill in the hole
	public void doneSeeding(){
        lock.lock();
        try {
        	if (seeds.get() > filled.get()) {
        		if (DEBUG == 1) {
        			System.out.println(Thread.currentThread().getId()+" Benjamin has seeded. Mary can fill in the hole.");
        		}
        		noSeed.signalAll();
        	}
        } finally {
        	lock.unlock();
        }
	} 
	
	//Mary need to wait for Benjamin to seed
	public void startFilling() throws InterruptedException{
        lock.lock();
        try {
        	while (seeds.get() <= filled.get()) {
        		if (DEBUG == 1) {
        			System.out.println(Thread.currentThread().getId()+" Mary is waiting for Benjamin to seed");
        		}
        		noSeed.await();
        	}
        } finally {
        	lock.unlock();
        }
	}
	
	//Mary need to notify Newton she has catched up
	public void doneFilling(){
        lock.lock();
        try {
        	if (holes.get() - filled.get() < maxUnfilledHoles) {
        		if (DEBUG == 1){
        			System.out.println(Thread.currentThread().getId()+" Mary has catched up. Newton can dig new holes.");
        		}
        		fast.signalAll();
        	}
        } finally {
        	lock.unlock();
        }
	}

// You are free to implements your own Newton, Benjamin and Mary
// classes. They will NOT count to your grade.
	protected static class Newton implements Runnable {
		Garden garden;
		public Newton(Garden garden){
			this.garden = garden;
		}
		
		@Override
		public void run() {
		    while (true) {
		    	if (holes.get() >= MAX_HOLES) return;

                try {
					garden.startDigging();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    dig();

				garden.doneDigging();
			}
		} 
		
		private void dig(){
			holes.incrementAndGet();
			if (DEBUG == 1) {
				System.out.println(Thread.currentThread().getId()+" Hole number is "+holes.get());
			}
		}
	}
	
	protected static class Benjamin implements Runnable {
		Garden garden;
		public Benjamin(Garden garden){
			this.garden = garden;
		}
		@Override
		public void run() {
		    while (true) {
		    	if (seeds.get() >= MAX_HOLES) return;
                try {
					garden.startSeeding();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				plantSeed();
				garden.doneSeeding();
			}
		} 
		
		private void plantSeed(){
			seeds.incrementAndGet();
			if (DEBUG == 1) {
				System.out.println(Thread.currentThread().getId()+" Seed number is "+seeds.get());
			}
		}
	}
	
	protected static class Mary implements Runnable {
		Garden garden;
		public Mary(Garden garden){
            this.garden = garden;
		}
		@Override
		public void run() {
		    while (true) {
		    	if (filled.get() >= MAX_HOLES) return;
                try {
					garden.startFilling();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 	Fill();
			 	garden.doneFilling();
			}
		} 
		
		private void Fill(){
			filled.incrementAndGet();
			if (DEBUG == 1) {
				System.out.println(Thread.currentThread().getId()+" Filled number is "+filled.get());
			}
		}
	}

	/*
	public static void main (String[] args) {
		int MAX = 10;
		Garden garden = new Garden(MAX);
		Newton newton = new Newton(garden);
		Benjamin benjamin = new Benjamin(garden);
		Mary mary = new Mary(garden);
		
		Thread tNewton = new Thread(newton);
		Thread tBenjamin = new Thread(benjamin);
		Thread tMary = new Thread(mary);
		
		tNewton.start();
		tBenjamin.start();
		tMary.start();
	}
	*/
}
