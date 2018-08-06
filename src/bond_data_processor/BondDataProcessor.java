package bond_data_processor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BondDataProcessor{
	static BondDAO bondDAO;
	BondRecordParser bondRecordParser;
	
	public static void main(String[] args) {
		try {
			// one reader for SINGLE core option
			BufferedReader firstBufferedReader =
					new BufferedReader(new FileReader(args[0]));
			
			// second for MULTI core option
			BufferedReader secondBufferReader =
					new BufferedReader(new FileReader(args[0]));
			
			// DAO object to store Bond in DB
			bondDAO = BondDAOFactory.createBondDAO();
			
			// Data processor object to process lines
			BondDataProcessor dataProcessor = new BondDataProcessor();
			String line; // single line
			
			// SINGLE CORE OPTION
			System.out.println("SINGLE CORE PROCESSING...");
			long startTime = System.currentTimeMillis();
			
			// process each line seperatelly
			while ((line = firstBufferedReader.readLine()) != null) {
				dataProcessor.processLine(line);
			}
			long endTime = System.currentTimeMillis();
			long timeTakenSec = (endTime - startTime) / 1000; // execution time measurement in seconds
			System.out.println("It took " + timeTakenSec + 
					" seconds to complete task on SINGLE core");
			
			// MULTIPLE CORE OPTION
			System.out.println("MUTLI CORE PROCESSING...");
			startTime = System.currentTimeMillis();
			dataProcessor.processFileMultithread(secondBufferReader);
			endTime = System.currentTimeMillis();
			timeTakenSec = (endTime - startTime) / 1000; // execution time measurement in seconds
			System.out.println("It took " + timeTakenSec + 
					" seconds to complete task on MULTI core");
		
			
			
		}catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// No arguments constructor
	public BondDataProcessor() {	
		bondRecordParser = new BondRecordParser();
	}
	
	// process line and store Bond in DB (SINGLE CORE OPTION)
	private void processLine(String line) throws InterruptedException {
		final Bond bond = bondRecordParser.parseBond(line);
		bondDAO.store(bond);
	}

	// process all lines and store Bonds in DB (MULTI CORE OPTION)
	private void processFileMultithread(BufferedReader reader) 
			throws InterruptedException, ExecutionException, IOException {
		
		// create executor service with pool equal to available processors
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService =  Executors.newFixedThreadPool(cores);
		
		// synchronized list of future objects (returning Bond object)
		List<Future<Bond>> bondList = Collections.synchronizedList(new ArrayList<>());
		
		String line; //single line
		// process each line
		while ((line = reader.readLine()) != null) {
			//submit each record parser to executor's service
			final BondRecordParser recordParser = new BondRecordParser(line);
			Future<Bond> fut = executorService.submit(recordParser);
			// store each future object in synchronized list
			bondList.add(fut);
		}
		
		// get bond from each future object and store it in DB
		for (Future<Bond> f : bondList) {
			final Bond bond = f.get();
			bondDAO.store(bond);
		}
		
		// shutdown executor service
		executorService.shutdown();
		
		// check if executor's service shutdown correctly
   	 	try {
   	 		// wait 2 seconds for running tasks to finish
   	 		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
   	 	}catch (InterruptedException ex1) { 
   	 		// did not wait the full 2 seconds
   	 	}finally {
    		if(!executorService.isTerminated()) {
    			System.out.println("some task have not finished");
    		}
    	}
	}
	
}