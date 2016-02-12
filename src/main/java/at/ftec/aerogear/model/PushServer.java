package at.ftec.aerogear.model;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * represents a AeroGear Push Server to connect
 *
 * @author Michael Fischelmayer
 */
public class PushServer {

    private URI pushURI;

    private String username;
    private String password;
    private String clientId;

    public PushServer( URI pushURI ) {
        if( pushURI == null || pushURI.toString().length() < 5 ) {
            throw new IllegalArgumentException( "URI must not be empty or is too short (eg. 'http://aerogearserver.com')" );
        }
        
        // if the last char is an slash ("/") -> cut it
        if(pushURI.toString().endsWith("/")) {
            String urlString = pushURI.toString();
			pushURI = URI.create( urlString.substring( 0, urlString.length() - 1 ) );
        }

        this.pushURI = pushURI;
    }

    public PushServer( String url ) {
		this( URI.create( url ) );
    }

    public URI getPushURI() {
        return pushURI;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return the push url or {@code null} if an error occurs
     */
    public String getPushUrl() {
        try {
            return pushURI.toURL().toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * for the most api calls you need to be authenticated with keycloack.
     *
     * @param username
     * @param password
     * @param clientId
     */
    public void setKeycloackCredentials( String username, String password, String clientId ) {
        setUsername(username);
        setPassword(password);
        setClientId(clientId);
    }
}
