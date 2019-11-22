public class RingProcess implements Runnable {

	int processId;
	MySignal mySignal;
	private int loc;
	private MessagingSys myMsgSys;
	private double send;
	private double roundsLeft;
	private boolean completed;

	public RingProcess(IDs ids, MySignal mySignal, int loc, MessagingSys myMsgSys) {
		this.processId = ids.getMyId(loc);
		this.mySignal = mySignal;
		this.loc = loc;
		this.myMsgSys = myMsgSys;
		this.send = this.processId;
		this.roundsLeft = Math.pow(2, this.processId);
		this.completed = false;
	}

	@Override
	public void run() {
		// run indefinite loop until I find a leader
		while (!this.completed) {

			// read message from neighbor
			String msgNeighbor = myMsgSys.get(this.loc);
			// if there is a message I process
			if (msgNeighbor.length() > 0) {
				// received id is smaller than my id
				if (Double.parseDouble(msgNeighbor) < send) {
					send = Double.parseDouble(msgNeighbor);
					roundsLeft = Math.pow(2, send);
				}
				// received id my id which means I'm the leader
				else if (Double.parseDouble(msgNeighbor) == this.processId) {
					System.out.println("My process id is " + processId + ". I AM THE LEADER!");
					roundsLeft = Math.pow(2, send);
					this.completed = true;
					myMsgSys.put(this.loc, Double.toString(send));
				}
				// leader id traverses the ring, every process registers it, forwards
				// and terminates
				else if (Double.parseDouble(msgNeighbor) == send) {
					System.out.println("My process id is " + processId + ". My leader is " + ((int) send));
					roundsLeft = Math.pow(2, send);
					this.completed = true;
					myMsgSys.put(this.loc, Double.toString(send));
				}
			}

			// Reduce 1 from the roundsLeft to pass on the message in each round
			roundsLeft--;

			// rounds Elapsed is equal to 2^send, I send the id
			if (roundsLeft == 0) {
				myMsgSys.put(this.loc, Double.toString(send));
			}

			// Work of current round completed, so reducing the number of working threads
			mySignal.sub();

			// If I don't know the leader in this round wait for the signal to begin
			// next round
			if (!this.completed) {
				synchronized (mySignal) {
					try {
						mySignal.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		// I have found the leader so joining parent thread.
		System.out.println("Joining parent thread. My id is " + this.processId);
	}
}