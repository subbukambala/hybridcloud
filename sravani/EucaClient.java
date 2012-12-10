import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


public class EucaClient {

	Hashtable<String, VMType> availability = new Hashtable<String, VMType>();
	Hashtable<String,InstanceInfo> instances = new Hashtable<String,InstanceInfo>();
	boolean euca_amazon;
	static String switching_error="";
	String outFile = "/home/sasank/workspace/EucaParse/console_out.txt";
	String eucarc="/root/eucalyptus/eucarc";
	String[] envp=new String[3];
	
	
	public boolean switchToAmazon() throws InterruptedException, IOException{
	
		String access_key= "AKIAJE6FMIQWL4KR3P2A";
		String secret_key="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";
		
		
		
		String EC2_SECRET_KEY = "EC2_SECRET_KEY="+secret_key;
		String EC2_ACCESS_KEY="EC2_ACCESS_KEY="+access_key;
		
	
		String EC2_URL="EC2_URL=http://ec2.amazonaws.com";
		envp[0]=EC2_URL;
		envp[1]=EC2_ACCESS_KEY;
		envp[2]=EC2_SECRET_KEY;
	 return true;
	}
	
	public boolean switchToEucalyptus() throws IOException, InterruptedException{
		BufferedReader buf = new BufferedReader(new FileReader(eucarc));
		int i=0;
		String line="";
		while((line=buf.readLine())!=null)
		{
			if(line.contains("EC2_URL"))
			{ StringTokenizer st = new StringTokenizer(line," ");
				st.nextToken();
				envp[0]=st.nextToken();
			}
			if(line.contains("EC2_ACCESS_KEY"))
			{ StringTokenizer st = new StringTokenizer(line," ");
				st.nextToken();
				envp[1]=st.nextToken();
			}
			if(line.contains("EC2_SECRET_KEY"))
			{ StringTokenizer st = new StringTokenizer(line," ");
				st.nextToken();
				envp[2]=st.nextToken();
				break;
			}
		}
			return true;
	}
	
	public BufferedReader runEucaCommand(String cmd) throws InterruptedException, IOException {

		String[] cmds = {"/bin/sh", "-c",cmd, " > "+this.outFile};
		Runtime run = Runtime.getRuntime();
		//Process pr2 = run.exec("touch availability");
		
		Process pr = run.exec(cmds, envp);
		//pr2.waitFor();
		pr.waitFor();
		//System.out.println( pr.getInputStream().toString());
		//return null;
		return new BufferedReader(new FileReader(outFile));
	}
	
	public String getIP(String instance) throws InterruptedException, IOException
	{
		BufferedReader buf = runEucaCommand("euca-describe-instances  | grep "+instance+"  | cut -f 4");
		String IP = buf.readLine();
		if(IP!=null)
		return IP;
		else
			return "";
	}
    
	
	public boolean  terminateInstance(String instance) throws InterruptedException, IOException{
		runEucaCommand("euca-terminate-instances "+instance);
		return true;
		
	}
	
	public boolean runInstances(String instanceType) throws InterruptedException, IOException {
	
	BufferedReader br =	runEucaCommand("euca-run-instances "+instanceType);
	String line=br.readLine();
	String temp="";
	InstanceInfo ins = new InstanceInfo();
	ins.emi = instanceType;
	
	if(line.contains("RESERVATION"))
	{
		StringTokenizer st = new StringTokenizer(line, " ");
		st.nextToken();
		if((temp=st.nextToken()).contains("r-"))
		ins.reservation = temp;
		
	}	
	line=br.readLine();
	
	if(line.contains("INSTANCE"))
	{
		StringTokenizer st = new StringTokenizer(line, " ");
		while(st.hasMoreTokens()) {
		temp=st.nextToken();
		if((temp).contains("i-"))
		ins.instance=temp;
		if(temp.contains("pending")||temp.contains("running")||temp.contains("terminated")||temp.contains("shutting-down"))
		ins.state = temp;
		
		if(temp.matches("ec2*amazon")||temp.matches("*.*.*.*"))
			ins.IP = temp;
		}
		
	}
	Thread.sleep(10000);
	
	ins.IP = getIP(ins.instance);

	instances.put(ins.instance, ins);
		return true;
	}
	
	public void  describeInstances() throws InterruptedException, IOException 
	{
		
		/*String cmd = "euca-describe-instances";
		
		BufferedReader buf= runEucaCommand(cmd);
		String line="";
		 InstanceInfo ins = new InstanceInfo();
		while ((line=buf.readLine())!=null) { 
		      	if(line.contains("RESERVATION"))
		      		continue;
				StringTokenizer st = new StringTokenizer(line, " ");
				st.nextToken();
				ins.instance = st.nextToken();ins.emi=st.nextToken();
				ins.IP = st.nextToken();
				st.nextToken(); st.nextToken();
				ins.state = st.nextToken();
				st.nextToken();
				st.nextToken();
				ins.vmtype = st.nextToken();
				 instances.add(ins);
		    	   
		       }
				System.out.println(line);
				*/
	}
		
		
	
	
	public void describeAvailability() throws IOException, InterruptedException{
		
		String cmd = "euca-describe-availability-zones";
		
		BufferedReader buf= runEucaCommand(cmd);
		String line = "";
		
		String clustername ="";
		boolean isClusterLine = true;
		while ((line=buf.readLine())!=null) {
	       StringTokenizer st = new StringTokenizer(line, " ");	
	       if(isClusterLine) {
	    	   st.nextToken(); clustername = st.nextToken();
	    	   isClusterLine = false;
	    	   line=buf.readLine();
	    	   continue;
	       }
	       VMType vm = new VMType();
	       st.nextToken(); st.nextToken();//
	       String vmtype = st.nextToken();
	       vm.free = Integer.parseInt(st.nextToken());
	       st.nextToken();
	       vm.max = Integer.parseInt(st.nextToken());
	       vm.cpu = Integer.parseInt(st.nextToken());
	       vm.ram = Integer.parseInt(st.nextToken());
	       vm.disk = Integer.parseInt(st.nextToken());
	       availability.put(vmtype, vm);
			System.out.println(line);
		}
	}
	
	
	
}
