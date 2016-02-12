package at.ftec.aerogear.api.impl;

import java.util.List;

import org.jboss.aerogear.unifiedpush.api.Installation;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.junit.Before;
import org.junit.Test;

import at.ftec.aerogear.api.AerogearAdminService;
import at.ftec.aerogear.exception.AerogearHelperException;
import at.ftec.aerogear.model.result.HealthStatus;
import at.ftec.aerogear.model.result.PushApplicationsResult;
import at.ftec.aerogear.model.PushServer;

/**
 * real world tests
 *
 */
//@Ignore
public class DefaultAerogearAdminServiceIT2 {

    AerogearAdminService adminService;

    @Before
    public void setup() {

        PushServer pushServer = new PushServer( "https://yourpushserver.com" );
        pushServer.setKeycloakCredentials("username", "password", "clientId");

        adminService = new DefaultAerogearAdminService( pushServer );

    }

    @Test
    public void testCreatePushApplication() throws Exception {
        PushApplication newPushApp = new PushApplication();
        newPushApp.setName( "My Test Application" );
        newPushApp.setDescription( "created with ftec aerogear lib" );
        newPushApp.setDeveloper( "mf" );
        PushApplication returnedPushApp = adminService.createPushApplication(newPushApp);
    }

    @Test
    public void testShowPushApplications() throws Exception {
        PushApplicationsResult pushApplicationsResult = adminService.showPushApplications(1, 10);
    }

    @Test
    public void testShowHealthInfo() throws Exception {
        HealthStatus healthStatus = adminService.showHealthInfo();
    }

    @Test
    public void registerDeviceTest() throws AerogearHelperException {
        Installation installation = new Installation();
        installation.setDeviceToken("95f86df5b64e20a45e0e677834563aabeb553a3232fcedf1d75407885798be67");
        installation.setAlias("ftec Test Device");

        Installation returnedInstallation = adminService.registerDevice(installation, "42a7468e-ca2f-4342-88cb-086853d5672d", "c49f277d-d0d7-4461-a3cf-631c72b5a97f");
    }

    @Test
    public void deleteDeviceTest() throws AerogearHelperException {
        adminService.deleteDevice("95f86df5b64e20a45e0e677834563aabeb553a3232fcedf1d75407885798be67", "42a7468e-ca2f-4342-88cb-086853d5672d", "c49f277d-d0d7-4461-a3cf-631c72b5a97f");

    }

    @Test
    public void showAllInstallationsTest() throws AerogearHelperException {
        List<Installation> installations = adminService.showInstallations("42a7468e-ca2f-4342-88cb-086853d5672d");
    }
}