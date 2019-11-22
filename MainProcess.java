import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainProcess implements Runnable {

	private static int[] processes;
	private static Set<Thread> processSet;
	public MySignal mySignal = new MySignal();
	public MessagingSys myMessageSys;
	public Iterator<Thread> itr;
	private boolean completed;
	private IDs ids;

	public MainProcess(int[] processes) {
		MainProcess.processes = processes;
		myMessageSys = new MessagingSys(processes.length);
		this.completed = false;
		this.ids = new IDs(processes);
	}

	public void spawnProcesses() throws InterruptedException {
		processSet = new HashSet<>();
		// spawn n number of threads, store them in a set
		for (int i = 0; i < processes.length; i++) {
			Thread process = new Thread(new RingProcess(this.ids, mySignal, i, myMessageSys));
			process.setName(Integer.toString(processes[i]));
			//Keeping count of how many threads have been started by me in the current round
			mySignal.add();
			process.start();
			processSet.add(process);
		}
		// run the algorithm till leader is found and everyone knows it
		while (!this.completed) {
			// sleep for 10ms if the round is in progress
			while (!mySignal.roundCompleted()) {
				Thread.sleep(10);
			}
			
			// round has completed signal all waiting threads
			synchronized (mySignal) {
				itr = processSet.iterator();
				while (itr.hasNext()) {
					Thread thread = itr.next();
					// System.out.println(thread.getName()+":"+thread.getState());
					//Keeping track for how many threads have found the leader
					if (thread.getState() != Thread.State.TERMINATED)
						//Keeping track of how many threads have been signalled to begin the next round
						mySignal.add();
				}
				
				//If all the threads know the leader then set the completed flag to true
				if (this.mySignal.roundCompleted()) {
					this.completed = true;
				}
				// notifying all processes to start the next round
				mySignal.notifyAll();
			}
		}

		//If completed flag is true join all child threads to the main thread
		itr = processSet.iterator();
		// all the processes know the leader, terminating them
		while (itr.hasNext()) {
			itr.next().join();
		}
		System.out.println("Parent thread exits");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			spawnProcesses();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}