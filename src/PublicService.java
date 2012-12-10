/**
 * @description Implements a Service interface
 *
 * @authors Bala Subrahmanyam Kambala
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;


public class PublicService extends UnicastRemoteObject implements Service {

    private static Logger lg;

    private static ServiceManagerInterface serviceMgr;
    
    Integer myId;

    /**
     * The PublicService constructor.
     */
    PublicService() throws Exception {
        serviceMgr = (ServiceManagerInterface) 
            Naming.lookup("//" + "localhost" + "/ServiceManager");
        
        lg = new Logger("PublicService");
        lg.log(Level.FINER, "PublicService started.");
        
        myId = serviceMgr.registerService();
        lg.log(Level.INFO, "my id is: " + myId);
    }
    
    public static void main(String[] argv) {
        String serviceMgrAddr = "localhost";
        
        ArgumentHandler cli = new ArgumentHandler
            (
             "PublicService [-h] [-serviceMgr address] "
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
        
        PublicService pubService = null;
        try {
            // Binding loader
        	pubService = new PublicService();
            Naming.rebind("PublicService", pubService);
            
        } catch (Exception e) {
            System.out.println("PublicService failed: ");
            e.printStackTrace();
        }
    }

}
