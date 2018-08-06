# bond_data_processor
Multithreading example using ExecutorService

This project is an attempt to show advantages
of using multithreading for CPU intensive tasks.
It has 5 classes: Bond, BondDAO, BondDAOFactory,
BondDAOProcessor and BondRecordParser.First 3 classes
are almost empty and do not contain any interesting code.
BondDataParser implements Callable and has 2 interesting 
methods: parseLine for SINGLE core example and call for 
MULTI core example. Main method in BondDataProcessor is 
running both SINGLE and MULTI core options and compares
results. You should see improvement when using multithreading e.g. 
I have 4 cores in my laptop so SINGLE core option took 30 seconds
and MULTI core option only 9.

Running instructions:
- import project to Eclipse,
- right click BondDataProcessor.java -> select Run As -> Run Configurations,
- in arguments tab enter below text in program arguments:
"test_data/test.txt"
- click apply,
- it should now run correctly.
