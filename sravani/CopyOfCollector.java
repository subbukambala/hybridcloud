import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class CopyOfCollector{
   private static CopyOfCollector collector = new CopyOfCollector( );

   //stores information of services running in private cloud
   private static Hashtable<Integer, ServiceInfo> privateList = new Hashtable<Integer, ServiceInfo>();

   //stores information of services running in public cloud
   private static Hashtable<Integer, ServiceInfo> publicList = new Hashtable<Integer, ServiceInfo>();
   
   public static List<Integer> privs;
   public static List<Integer> pubs;
   //matrix maintaining communication between services
   private static int commMatrix[][];
   private static int resourceSum[];
   private static boolean isReplicated;
   
   private CopyOfCollector(){ 
	   isReplicated=false;
   }
   
   static void updateServiceInfo(int sv_id, boolean isPriv, boolean isReplicated) throws InterruptedException, IOException{
	   if(isReplicated){
		   if(isPriv && privateList.get(sv_id)==null){
			   ServiceInfo si = new ServiceInfo();
			   si.isPrivate=isPriv;
			   si.name = Integer.toString(sv_id);
			   
			   EucaClient eu = new EucaClient();
			   String image_id = "emi-123123123";
			   eu.switchToEucalyptus();
			   eu.runInstances(image_id/*image id*/);
			   String ip = eu.getIP(image_id);
			   si.ip = ip;
			   privateList.put(sv_id, si);
		   } else if(!isPriv && publicList.get(sv_id)==null){
			   ServiceInfo si = new ServiceInfo();
			   si.isPrivate=isPriv;
			   si.name = Integer.toString(sv_id);
			   publicList.put(sv_id, si);
			   EucaClient eu = new EucaClient();
			   String image_id = "emi-123123123";
			   eu.switchToAmazon();
			   eu.runInstances(image_id/*image id*/);
			   String ip = eu.getIP(image_id);
			   si.ip = ip;
			   publicList.put(sv_id, si);
		   }
	   } else{
		   if(!isPriv){
			   if(publicList.get(sv_id)==null || privateList.get(sv_id)!=null){
				   ServiceInfo si = new ServiceInfo();
				   si.isPrivate=isPriv;
				   si.name = Integer.toString(sv_id);
				   publicList.put(sv_id, si);
				   EucaClient eu = new EucaClient();
				   String image_id = "emi-123123123";
				   eu.switchToAmazon();
				   eu.runInstances(image_id/*image id*/);
				   String ip = eu.getIP(image_id);
				   si.ip = ip;
				   publicList.put(sv_id, si);
				   eu.switchToEucalyptus();
				   eu.terminateInstance(image_id);
				   privateList.remove(sv_id);
			   } 
		   } else if(publicList.get(sv_id)!=null || privateList.get(sv_id)==null){
				   ServiceInfo si = new ServiceInfo();
				   si.isPrivate=isPriv;
				   si.name = Integer.toString(sv_id);
				   privateList.put(sv_id, si);
				   EucaClient eu = new EucaClient();
				   String image_id = "emi-123123123";
				   eu.switchToEucalyptus();
				   eu.runInstances(image_id/*image id*/);
				   String ip = eu.getIP(image_id);
				   si.ip = ip;
				   privateList.put(sv_id, si);
				   eu.switchToAmazon();
				   eu.terminateInstance(image_id);
				   publicList.remove(sv_id);
		   }
	   }
   }
   
   static void runPartitioner(int[] resourceReq, int resourceAvail, boolean isReplication){
	   if(isReplication){
		   updateResourceReq(resourceReq, resourceAvail);
	   } else{
		   DataMigrationModel.dataMigration(resourceReq.length, resourceReq, commMatrix, resourceAvail);
	   }
	   for(int i =0; i<privs.size();i++)
		try {
			updateServiceInfo(privs.get(i), true, isReplicated);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	   for(int i =0; i<pubs.size();i++)
			try {
				updateServiceInfo(pubs.get(i), false, isReplicated);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
   }
   
   private static int binSearch(int val, int i, int j){
	   	if(i==j)
	   		return j;
	   	if(i+1 == j)
	   		return (val>resourceSum[i])?j:i;
	   int ind = (i+j)/2;
	   
	   	   
		   if(val<resourceSum[ind])
			   return binSearch(val, i, ind);
		   else if(val>resourceSum[ind])
			   return binSearch(val, ind, j);
		   else
			   return ind;
	   
	   //return ind;
  }

   private static void updateResourceReq(int[] resourceReq, int resourceAvail) {
	   int rsum=0;
	   for (int i = 0; i < resourceReq.length; i++) {
		   resourceReq [i] = resourceReq [i] + (int) (Math.random() * 10) % 2;	
		   rsum += resourceReq[i]; 
	   }
	   int[][] repCommMatrix = new int[rsum][rsum];
	   
	   resourceSum = new int[resourceReq.length];
	   int currSum=0;
	   for(int i=0;i<resourceSum.length ; i++){ 
		   currSum = currSum+resourceReq[i];
	   	 	resourceSum[i]=currSum;
	   }   
	   
	   //build comm matrix
	   for(int i=0;i<rsum;i++){
		   int tempi= binSearch(i,0,resourceSum.length);
		   for(int j=0;j<rsum;j++){
			  int tempj = binSearch(j,0, resourceSum.length);
			  repCommMatrix[i][j]=commMatrix[tempi][tempj];
		   }
		}
	   DataMigrationModel.dataMigration(resourceReq.length, resourceReq, repCommMatrix, resourceAvail);
   }
   
}