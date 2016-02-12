package at.ftec.aerogear.util;

import at.ftec.aerogear.model.PushServer;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

/**
 * @author Michael Fischelmayer
 */
public class KeycloakHelper {

    private static final String AUTH_CONTEXT = "/auth/realms/aerogear/tokens/grants/access";

    /**
     * get an access token from the keycloack (push) server
     *
     * @param pushServer
     * @return
     * @throws UnirestException
     */
    public static String getAccessToken(PushServer pushServer) throws UnirestException {

        String authUrl = pushServer.getPushUrl() + AUTH_CONTEXT;

        HttpResponse<JsonNode> jsonResponse = Unirest.post( authUrl )
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("username", pushServer.getUsername() )
                .field("password", pushServer.getPassword() )
                .field("client_id", pushServer.getClientId() )
                .asJson();

        JSONObject authContent = jsonResponse.getBody().getObject();
        String accessToken = authContent.getString( "access_token" );

        return accessToken;
    }
}
