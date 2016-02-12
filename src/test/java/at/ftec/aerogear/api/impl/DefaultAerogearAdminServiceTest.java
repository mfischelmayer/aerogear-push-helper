package at.ftec.aerogear.api.impl;

import at.ftec.aerogear.api.AerogearAdminService;
import at.ftec.aerogear.exception.AerogearHelperException;
import at.ftec.aerogear.model.PushServer;
import org.jboss.aerogear.unifiedpush.api.Installation;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michael Fischelmayer
 */
public class DefaultAerogearAdminServiceTest {

    AerogearAdminService adminService;

    @Test(expected = IllegalArgumentException.class)
    public void testServiceWithIllegalServer() throws AerogearHelperException {
        adminService = new DefaultAerogearAdminService( null );
    }

    @Before
    public void initServer() {
        PushServer pushServer = new PushServer("http://testme.url");
        adminService = new DefaultAerogearAdminService(pushServer);
    }

    @Test(expected = AerogearHelperException.class)
    public void testCreateEmptyPushApplication() throws Exception {

        // validation exception
        adminService.createPushApplication( new PushApplication() );
    }


    @Test
    public void testShowPushApplications() throws Exception {

    }

    @Test
    public void testShowHealthInfo() throws Exception {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterDevice() throws Exception {
        adminService.registerDevice(new Installation(), null, null);
    }
}