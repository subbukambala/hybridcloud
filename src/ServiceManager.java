/**
 * @description 
 *
 * @authors Bala Subrahmanyam Kambala
 */

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;

public class ServiceManager extends UnicastRemoteObject implements ServiceManagerInterface {

    private Logger lg;
    
    private Integer loaderId;
    private List<Pair<Integer, String> > myLoaderList;
    
    private Integer serviceId;
    private List<Pair<Integer, String> > myServiceList;

    public ServiceManager() throws Exception {
        lg = new Logger("ServiceManager");
        
        lg.log(Level.FINER, "Service manager started.");
        
        loaderId = 0;
        myLoaderList = new Vector<Pair<Integer,String>> ();
        
        
        serviceId = 0;
        myServiceList = new Vector<Pair<Integer,String>> ();
        
    }

    @Override
    synchronized public Integer registerService() throws Exception {
        
    	serviceId++;
        
        lg.log(Level.INFO, "Service " + serviceId + " joined.");
        
        myServiceList.add(new Pair<Integer, String>(serviceId, getClientHost()));
        return serviceId;
    }
    
    @Override
    synchronized public Integer registerLoader() throws Exception {
        
    	loaderId++;
        
        lg.log(Level.INFO, "Loader " + loaderId + " joined.");
        
        myLoaderList.add(new Pair<Integer, String>(loaderId, getClientHost()));
        return loaderId;
    }
    
    @Override
    synchronized public String getServiceName() throws Exception {
       return "PublicService";
    }

    /**
     * The good stuff.
     */
    public static void main(String[] argv) {
      
        ArgumentHandler cli = new ArgumentHandler
            (
             "Server [-h] "
             ,
             "TBD."
             ,
             "Bala Subrahmanyam Kambala"
             );
        cli.addOption("h", "help", false, "Print this usage information.");
        
        CommandLine commandLine = cli.parse(argv);
        if (commandLine.hasOption('h')) {
            cli.usage("");
            System.exit(0);
        }
        
        ServiceManager serviceMgr;
        try {
        	serviceMgr =  new ServiceManager();
            
            Naming.rebind("ServiceManager", serviceMgr);
            
        } catch (Exception e) {
            System.out.println("ServiceManager failed: ");
            e.printStackTrace();
        }
    }
}