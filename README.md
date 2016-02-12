# Aerogear Helper

##### Java client for the [Aerogear Push](https://aerogear.org/push/) REST Service API.

(see also <https://aerogear.org/docs/specs/aerogear-unifiedpush-rest/index.html>)

Actually working with aerogear unified pushserver 1.1.0

Attention - project in an early alpha ;-)


```java
// create a instance of the PushServer you like to connect
PushServer pushServer = new PushServer("https://aerogear.example-server.at");

// for the most administrative calls you need to authenticate via keycloack
pushServer.setKeycloackCredentials( String username, String password, String clientId )

// create the push administration service
AergearAdminService admin = new DefaultAergearAdminService( pushServer );

// make the api call
List<PushApplicaitonResult> pushAppsOnServer = admin.showPushApplications();
```

### actually the API supports the following calls
```java
- showPushApplications()
- createPushApplication( PushApplication newPushApp );
- healthStatus();
- registerDevice( Installation newInstallation );
```

### some other examples
##### register Device
```
Installation installation = new Installation();
installation.setDeviceToken("95f86df5b64e20a45e0e67782984abeb553a3232fcedf1d75407885798be67");
installation.setAlias("ftec Test Device");

adminService.registerDevice(installation, "your-variant-id", "your-variant-secret");
```

##### create Push Application
```
PushApplication newPushApp = new PushApplication();
newPushApp.setName( "My Test Application" );
newPushApp.setDescription( "created with ftec aerogear lib" );
newPushApp.setDeveloper( "Michi" );

adminService.createPushApplication( newPushApp );
```

##### show health status
```
HealthStatus healthStatus = adminService.showHealthInfo();
String healthSummary = healthStatus.getSummary();
```


### contact information
Michael Fischelmayer

<mailto:michael.fischelmayer@ftec.at>