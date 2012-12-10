import java.util.ArrayList;


public class DataMigrationModel {
	
	final static float DATA_TRANSFER_PER_GB_COST = 0.04f; 
	final static float COMPUTE_INSTANCE_PER_HOUR_COST = 0.08f;
	
	static int publicResources [] = new int [5];
	static int privateResourcesUtilization [] = new int [5];
	
	static int privateResourceUtilizationATEachTest = 0;
	
	public static void dataMigration(int noOfServices, int resource [], int dataTransferMatrix[][], int maxResources) {
		
		/**
		 * if input is only data transfer matrix...
		 * 		find a cost for each service, take minimum
		 * 
		 * why can't you schedule a set of services...
		 * 	each service maps to a VM, which means incurs more cost
		 * 
		 * no of MBs per sec
		 */
		/*int dataTransferMatrix [] [] = new int [noOfServices] [noOfServices];
		
		
		for (int i = 0; i < dataTransferMatrix.length; i++) {
			for (int j = 0; j < dataTransferMatrix.length; j++) {
				dataTransferMatrix [i][j] = (int) (Math.random() * 100);
			}
		}
		 */
		
		boolean scheduleInPublic[] = new boolean [noOfServices];
		for (int i = 0; i < noOfServices; i++) {
			scheduleInPublic[i] = false;
		}
		
		
		float [][] cost  = new float [noOfServices][maxResources];
		
		boolean [][] isPublic  = new boolean [noOfServices][maxResources];
		
		int [][] some  = new int [noOfServices][maxResources];
		
		/**
		 * base case:
		 * 
		 * 
		 * if maxResources = 0
		 * 		cost is public cloud
		 * 
		 * 
		 */
		CopyOfCollector.privs= new ArrayList<Integer>();
		CopyOfCollector.pubs= new ArrayList<Integer>();
		for (int i = 0; i < noOfServices; i++) {
			for (int j = 0; j < maxResources; j++) {
				cost [i][j] = Integer.MAX_VALUE;
			}
		}
		
		for (int i = 0; i < noOfServices; i++) {
			
			for (int j = 0; j < maxResources; j++) {
				
				
				// there is no previous state for 0
				if (i == 0) {
					if (j - resource[i] >= 0) {
						cost [i][j] = 0f;
					}
					else {
						cost [i][j] = resource[i] * COMPUTE_INSTANCE_PER_HOUR_COST;
					}
				}
				else {
					
					if (j - resource[i] >= 0 && cost[i][j] > cost[i - 1][j - resource[i]]) {
						cost[i][j] = cost[i - 1][j - resource[i]];
						
						some[i][j] = j - resource[i]; 
					}
					
					float scost = 0;
					for (int k = i - 1; k >= 0; k--) {
						if (scheduleInPublic[k]) {
							scost = scost + dataTransferMatrix[i][k] * DATA_TRANSFER_PER_GB_COST * resource[i];
						}
					}
					//System.out.print( cost[i][j - resource[i]] + "-" + cost[i][j] + "--" + cost[i - 1][j] + scost + costOfComputeInstancePerHour);
					if (cost[i][j] > cost[i - 1][j] + scost + COMPUTE_INSTANCE_PER_HOUR_COST * resource[i]) {
						cost[i][j]  = cost[i - 1][j] + scost + COMPUTE_INSTANCE_PER_HOUR_COST * resource[i];
						scheduleInPublic[i] = true;
						//System.out.println(i);
						isPublic[i][j] = true;
						
						some[i][j] = j;
					}
				}
				
			}
		}
		
		
		/*
		for (int i = 0; i < noOfServices; i++) {
			for (int j = 0; j < maxResources; j++) {
				System.out.format("%.2f\t", cost[i][j]);
			}
			System.out.println();
		}
		*/
		
		System.out.println("\ncost is : " + cost[noOfServices - 1][maxResources - 1]);
		
		privateResourceUtilizationATEachTest = 0;
		printServices(some, resource, noOfServices - 1, maxResources - 1);
		System.out.println("\nprivate resource utilization: " + privateResourceUtilizationATEachTest);
		
	}
	
	public static void printServices(int some[][], int resource[], int i, int j) {
		if (i == 0) {
			return;
		}
		
		if (some[i][j] == j) {
			System.out.print(i + "--" + true + "\t");
			publicResources[i]++;
			CopyOfCollector.pubs.add(i);
			printServices(some, resource, i - 1, j);
		}
		else {
			System.out.println(i + "--" + false + "\t");
			privateResourceUtilizationATEachTest = privateResourceUtilizationATEachTest + resource[i];
			CopyOfCollector.privs.add(i);
			printServices(some, resource, i - 1, j - resource[i]);
		}
		
	}
		
/*	public static void main(String args[]) {
		int noOfServices = 5;
		
		*//**
		 * Fixed VM type
		 * 
		 * increase in no of VMs per day
		 *//*
		int resource [] = new int [noOfServices];
		
		for (int i = 0; i < noOfServices; i++) {
			resource [i] = (int) (Math.random() * 10 + 1);
			System.out.print(resource [i] + "\t");
		}
		
		System.out.println();
		
		*//** http://lcm.csa.iisc.ernet.in/dsa/node170.html --> topological order example
		 * 
		 * topological order: B, A, D, C, E.
		 * 
		 *  
		 *  if B, A, C, D is scheduled first... 
		 *  While scheduling E, communication cost depends on scheduling of C, D 
		 *   
		 *//*
		int dataTransferMatrix [] [] = {
				{0, 	0, 	10, 	15, 	0},
				{12, 	0, 	0,		10, 	0},
				{0,		0,	0,		0,		10},
				{0,		0,	20,		0,		15},
				{0,		0,	0,		0,		0}
		};
		
		
		//type1Model_V3(noOfServices,  resource, dataTransferMatrix, 10);
		
		testCase1();
	}
	
	*//**
	 	resource requirements: 
	 		three services in increasing order
	 		two services more or less same
	 		
	 *//*
	public static void testCase1() {
		
		int noOfServices = 5;
		
		int dataTransferMatrix [] [] = {
				{0, 	0, 	10, 	15, 	0},
				{12, 	0, 	0,		10, 	0},
				{0,		0,	0,		0,		10},
				{0,		0,	20,		0,		15},
				{0,		0,	0,		0,		0}
		};
		
		
		int resource [] = new int [noOfServices];

		// initial resource allocation
		for (int i = 0; i < noOfServices; i++) {
			resource [i] = resource [i] + (int) (Math.random() * 10 + 2);
		}
		
		
		for (int testIteration = 0; testIteration < 100; testIteration++) {
			
			
			System.out.println("testIteration : " + testIteration);
			
			for (int i = 0; i < noOfServices; i++) {
				resource [i] = resource [i] + (int) (Math.random() * 10) % 2;
				System.out.print(resource [i] + "\t");
			}
			
			
			dataMigration(noOfServices,  resource, dataTransferMatrix, 100);
			
			System.out.println();
		}
		
		System.out.println("\n\nfinal public resources: ");
		for (int i = 0; i < noOfServices; i++) {
			System.out.print(publicResources[i] + " ");
		}
		
		
	}*/

}
