// All methods ensure mutual exclusion.
// Based on my interpretation, #1 and #2 are nearly equivalent. 
// They both have lock freedom but not starvation freedom. 
// They both busy wait on the door to the room. If the people 
// trying to enter the room in #1 believe that the room may become 
// available at any time, they would be continuously openning the door
// before immediately closing it out of embarassment, so it would truly 
// be a busy wait. There could be contention of hands at the door knob as
// well. In method #2, we again have to interpret how the guests would
// respond to their environment. Does watching the sign require a busy
// wait or can they mingle and get loose while keeping eyes on the door?
// If we had to translate them into familiar machine instructions, the 
// eager guests in #1 would most resemble a Test And Set approach, 
// due to the heavy traffic (though not because of cache invalidation).
// The patient guests in #2 somewhat resemble Compare And Swap. 
// 
// Method #3 seems to be the clear winner providing starvation freedom
// and allowing guests to sleep in line and be woken by the previous guest
// as they leave. The last person in line would have to wear a designated 
// cool hat. As people joined the line, they would have to find the person
// with the cool hat (or the last person in line after them) and atomically
// hold their hand before anyone else, then take the hat from them. 
// That is to say, we would need a linked list of threads which, for
// performance reasons, might have a tail pointer. However, such a
// systematic approach would be unnecessary and inefficient if the 
// contention was very low. With very low contention, #2 and #1 
// would probably be equally good. 

// In this program, I attempted to cause maximum contention to cause 
// contention by using the simple Test And Set approach of #1.


import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.Duration;
import java.util.Scanner;



public class minotaur2 {
	static AtomicBoolean roomIsAvailable = new AtomicBoolean(true);
	static int numGuests = 0;
	static Random rand = new Random();
	
	public static void main(String[] args) throws InterruptedException {
		
		System.out.print("Enter a number of threads: ");

		
		// In my experimentation, the "contention slowdown" was highly variant and was 
		// negative up until about 10000. This seems too high, so there are likely other
		// factors influencing the results.
		Scanner scanner = new Scanner(System.in);
		numGuests = scanner.nextInt();
		
		Thread threads[] = new Thread[numGuests];
		

		// start timer
		Instant start = Instant.now();


		for (int i = 0; i < numGuests; i++) {
			threads[i] = new Thread(new Guest(i));
			threads[i].start();
			threads[i].join();
		}
		
		// end timer
	    Instant finish = Instant.now();

	    long sequentialProcessingDuration = Duration.between(start, finish).toMillis();

		
		// start timer
	    start = Instant.now();

		for (int i = 0; i < numGuests; i++)
			threads[i] = new Thread(new Guest(i));
		
		for (int i = 0; i < numGuests; i++)
			threads[i].start();
		
		for (int i = 0; i < numGuests; i++) {
			threads[i].join();
		}
		
		// end timer
		finish = Instant.now();
		
	    long concurrentProcessingDuration = Duration.between(start, finish).toMillis();
	    
	    
	    double contentionFactor = concurrentProcessingDuration / (double)sequentialProcessingDuration;
	    double diff = (concurrentProcessingDuration - sequentialProcessingDuration) / 1000.0;
	    
		System.out.printf("The contention made the concurrent version take %3fs longer, a factor %f slower.\n", diff, contentionFactor);

	}

}

class Guest implements Runnable {
	int id = 0;
	Random rand = new Random();
	public Guest(int id) {
		id = this.id;
	}
	public void run() {
		while (!minotaur2.roomIsAvailable.getAndSet(false)) {
			// experience contention
		}
		
//		Thread.sleep(10);
		minotaur2.roomIsAvailable.getAndSet(true);
		
//		System.out.println("guest " + id + " beheld the vase!");
	}
}





















