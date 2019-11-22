import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RingAlgo {

	private static int numberOfProcesses;
	private static int[] processes;

	//Read the input file and store all process Ids in an array
	public static void populateData() throws NumberFormatException, IOException {
		// read from file: n, array of size n
		File file = new File("input.dat");
		BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
		numberOfProcesses = Integer.valueOf(reader.readLine());
		processes = new int[numberOfProcesses];
		for (int i = 0; i < numberOfProcesses; i++) {
			processes[i] = Integer.valueOf(reader.readLine());

		}
		reader.close();

	}

	public static void main(String[] args) throws Throwable {
		// boot the algorithm
		populateData();
		//Create the master/parent process
		Thread process = new Thread(new MainProcess(processes));
		process.start();
	}

}