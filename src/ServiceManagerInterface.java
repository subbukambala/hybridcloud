
/**
 * @description 
 * 
 * @authors kambala
 */
import java.rmi.*;

public interface ServiceManagerInterface extends Remote {
    
    /**
     * Service joins in network to serve requests
     */
    public Integer registerService() throws Exception;
    
    /**
     * Loader joins in network to load services
     */
    public Integer registerLoader() throws Exception;
    
    /**
     * Loader joins in network to load services
     */
    public String getServiceName() throws Exception;
    

}
