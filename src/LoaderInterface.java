/**
 * @description Remote Interface for a Loader. 
 * 
 * @authors Bala Subrahmanyam Kambala
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoaderInterface extends Remote {

    /**
     * This method is called by ServiceManager to start a service
     */
    public boolean startService(String className) 
        throws RemoteException;
}
