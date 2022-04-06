import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class minotaur {
	static Random rand = new Random();
	static AtomicBoolean cakeIsAvailable = new AtomicBoolean(true);
	static AtomicBoolean finished = new AtomicBoolean(false);
	static int numGuests = 0;
	
	public static void main(String[] args) throws InterruptedException {
		numGuests = 10;
		Thread threads[] = new Thread[numGuests];
		Guest guests[] = new Guest[numGuests];
		
		guests[0] = new LeaderGuest(0);
		for (int i = 1; i < numGuests; i++)
			guests[i] = new Guest(i);

		
		while (finished.get() == false) {
			int i = rand.nextInt(numGuests);
			
			
			Thread randomGuestThread = threads[i];
			if (randomGuestThread != null)
				randomGuestThread.join();
			
			
			threads[i] = new Thread(guests[i]);
			threads[i].start();
		}

		System.out.println("The leader claims everyone has been through the maze!");
	}

}

class Guest implements Runnable {
	int id = 0;
	boolean hasEatenCake = false;
	Random rand = new Random();
	public Guest(int id) {
		id = this.id;
	}
	public void run() {
		int patience = 10;
		if (!hasEatenCake) {
			try {
				waitABit();
				do {
					Thread.sleep(100);
					hasEatenCake = minotaur.cakeIsAvailable.compareAndSet(true, false);
					patience--;
				} while (hasEatenCake == false &&
						rand.nextDouble() < 0.8 && 
						patience > 0);
				if (hasEatenCake)
					System.out.println("Another guest ate!");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("done!");
	}
	
	void waitABit() throws InterruptedException {
		do {
			Thread.sleep(200);
		} while (rand.nextDouble() < 0.8);
	}
}

class LeaderGuest extends Guest {
	static int count = 0;
	
	public LeaderGuest(int id) {
		super(id);
	}
	
	public void run() {
		try {
			super.waitABit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (minotaur.cakeIsAvailable.compareAndSet(false, true)) {
			count++;
			System.out.println("Leader refilled the cake");
		}
		if (count >= minotaur.numGuests - 1)
			minotaur.finished.set(true);
		
		System.out.println("I'm the LEADER");
	}
}




















