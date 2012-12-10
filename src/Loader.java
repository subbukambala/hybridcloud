/**
 * @description Implements a LoaderInterface to start a service
 *
 * @authors Bala Subrahmanyam Kambala
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;


public class Loader extends UnicastRemoteObject implements LoaderInterface {

    private static Logger lg;

    private static ServiceManagerInterface serviceMgr;
    
    Integer myId;

    /**
     * The Loader constructor.
     */
    Loader(String serviceMgrAddr) throws Exception {
        serviceMgr = (ServiceManagerInterface) 
            Naming.lookup("//" + serviceMgrAddr + "/ServiceManager");
        
        lg = new Logger("Loader");
        lg.log(Level.FINER, "Loader started.");
        
        myId = serviceMgr.registerLoader();
        lg.log(Level.INFO, "my id is: " + myId);
    }
    
    @Override
    synchronized public boolean startService(String className) 
    		throws RemoteException {
        
    	try {
            ClassLoader myClassLoader = ClassLoader.getSystemClassLoader();
             
             // Step 2: Define a class to be loaded.
            String classNameToBeLoaded = className;
 
             
             // Step 3: Load the class
            Class myClass = myClassLoader.loadClass(classNameToBeLoaded);
            
            Object ob1 = myClass.newInstance();
            
            return true;
    	} catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    	
    	return false;
    }

    public static void main(String[] argv) {
        String serviceMgrAddr = "localhost";
        
        ArgumentHandler cli = new ArgumentHandler
            (
             "Loader [-h] [-serviceMgr address] "
             ,""
             ,"Bala Subrahmanyam Kambala"
             );

        cli.addOption("h", "help", false, "Print this usage information.");
        

        // parse command line
        CommandLine commandLine = cli.parse(argv);
        if (commandLine.hasOption('h')) {
            cli.usage("");
            System.exit(0);
        }

        if (commandLine.getArgs().length == 1)
        	serviceMgrAddr = commandLine.getArgs()[0];
        else {
            cli.usage("Loader only accepts one argument!\n\n");
            System.exit(1);
        }

        if (serviceMgrAddr == null) {
            cli.usage("serviceMgr address is required!\n\n");
            System.exit(1);
        }
        
        Loader loader = null;
        try {
            // Binding loader
        	loader = new Loader(serviceMgrAddr);
            Naming.rebind("Loader", loader);
            
            String name = serviceMgr.getServiceName();
            
            loader.startService(name);
            
        } catch (Exception e) {
            System.out.println("Loader failed: ");
            e.printStackTrace();
        }
    }

}
