package bond_data_processor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BondRecordParser implements Callable<Bond> {
	String line;
	
	// constructors
	// no argument constructor
	public BondRecordParser() {
	}
	
	// one argument constructor that gets line details
	public BondRecordParser(String line) {
		this.line = line;
	}
	
	// Old method for single thread
	public Bond parseBond(String line) throws InterruptedException {
		// Do stuff
		System.out.println("Processing " + line);
		// add a bit of delay (processing time)
		TimeUnit.SECONDS.sleep(3);
		return new Bond();
	}

	
	// New method for multiple threads
	@Override
	public Bond call() throws Exception {
		// we get line details form constructor
		return parseBond(line);
	}
}