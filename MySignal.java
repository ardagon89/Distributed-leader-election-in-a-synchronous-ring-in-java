import java.util.concurrent.Semaphore;

public class MySignal {
	private static int counter = 0;
	
	//Mutex to ensure mutual exclusion
	public Semaphore semaphore = new Semaphore(1);

	//Increase the counter for each thread that is awakened
	public void add() {
		try {
			semaphore.acquire();
			counter++;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Decrease the counter for each thread that has finished the current round
	public void sub() {
		try {
			semaphore.acquire();
			counter--;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public int getCount() {
//		return counter;
//	}

	//Return true if all threads have finished the current round
	public boolean roundCompleted() {
		boolean result = false;
		try {
			semaphore.acquire();
			result = (counter == 0);
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
