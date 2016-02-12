package at.ftec.aerogear.api;

import at.ftec.aerogear.exception.AerogearHelperException;
import at.ftec.aerogear.model.HealthStatus;
import at.ftec.aerogear.model.PushApplicationsResult;
import org.jboss.aerogear.unifiedpush.api.Installation;
import org.jboss.aerogear.unifiedpush.api.PushApplication;

import java.util.List;

/**
 * @author Michael Fischelmayer
 */
public interface AerogearAdminService {

    /**
     * create a new application
     *
     * @param newPushApplication
     * @return
     * @throws Exception
     */
    PushApplication createPushApplication( PushApplication newPushApplication ) throws AerogearHelperException;


    /**
     * show paged applications
     *
     * @param pageNumber
     * @param perPage
     * @return
     * @throws Exception
     */
    PushApplicationsResult showPushApplications(int pageNumber, int perPage ) throws AerogearHelperException;


    /**
     * get a detailed health status from the push server
     *
     * @return
     * @throws Exception
     */
    HealthStatus showHealthInfo() throws AerogearHelperException;


    /**
     * register new installation (=device) for an variant
     * No keycloack authentication necessary
     * Http Basic Authentication with variantId and variantPassword
     *
     * @param installation the new installation
     * @param variantId on which the installation will be registred
     * @param variantSecret
     * @return the new installed installation
     * @throws Exception
     */
    Installation registerDevice(Installation installation, String variantId, String variantSecret ) throws AerogearHelperException;


    /**
     * delete (unregister) device from variant
     * No keycloack authentication necessary
     * Http Basic Authentication with variantId and variantPassword
     *
     * @param deviceToken the deviceToken of the Installation
     * @param variantId on which the installation is registred
     * @param variantSecret
     * @throws AerogearHelperException
     */
    void deleteDevice(String deviceToken, String variantId, String variantSecret ) throws AerogearHelperException;


    /**
     * show all installations (devices) from an variant
     *
     * @param variantId
     * @return
     * @throws AerogearHelperException
     */
    List<Installation> showAllInstallations(String variantId) throws AerogearHelperException;
}
