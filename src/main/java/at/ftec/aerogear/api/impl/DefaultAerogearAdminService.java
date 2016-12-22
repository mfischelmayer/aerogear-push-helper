package at.ftec.aerogear.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jboss.aerogear.unifiedpush.api.*;
import org.json.JSONArray;
import org.json.JSONObject;

import at.ftec.aerogear.api.AerogearAdminService;
import at.ftec.aerogear.deserializer.PushApplicationDeserializer;
import at.ftec.aerogear.exception.AerogearHelperException;
import at.ftec.aerogear.model.PushServer;
import at.ftec.aerogear.model.result.HealthStatus;
import at.ftec.aerogear.model.result.PushApplicationsResult;
import at.ftec.aerogear.util.KeycloakHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

/**
 * TODO use acces token for multiple calls (logic for refresh token)
 * TODO create URLBuilder or something like that
 * TODO calls einheiltiches "checkRespone" oder der gleichen
 *
 * @author Michael Fischelmayer
 */
public class DefaultAerogearAdminService implements AerogearAdminService {

	private static final String PUSH_REST_CONTEXT = "/ag-push/rest";

	private final PushServer pushServer;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	private Validator validator = validatorFactory.getValidator();

	private String keycloakAccessToken;



	public DefaultAerogearAdminService( PushServer pushServer ) {
		if(pushServer == null || pushServer.getPushUrl().length() < 1 ) {
			throw new IllegalArgumentException("push server must not be null or empty");
		}
		this.pushServer = pushServer;
		SimpleModule module = new SimpleModule();
		module.addDeserializer(VariantType.class, new VariantTypeDeserializer());
		objectMapper.registerModule(module);
	}

	@Override
	public PushApplication createPushApplication( PushApplication newPushApplication ) throws AerogearHelperException {
		String createPushAppUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/applications";
		HttpResponse httpResponse = postRequest(createPushAppUrl, true, newPushApplication, true);
		try {
			return objectMapper.readValue( httpResponse.getBody().toString(), PushApplication.class );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}

	@Override
	public PushApplicationsResult showPushApplications(int pageNumber, int perPage ) throws AerogearHelperException {
		try {

			String showPushAppUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/applications";

			HttpResponse httpResponse = getRequest( showPushAppUrl, true);

			JSONArray applicationArray = new JSONArray(httpResponse.getBody().toString());

			List<PushApplication> pushApplications = new ArrayList<PushApplication>();

			for (int i = 0; i < applicationArray.length(); i++) {

				JSONObject currApp = applicationArray.getJSONObject( i );

				SimpleModule module = new SimpleModule();
				module.addDeserializer( PushApplication.class, new PushApplicationDeserializer() );
				objectMapper.registerModule( module );

				PushApplication pushApp = objectMapper.readValue( currApp.toString(), PushApplication.class );
				pushApplications.add( pushApp );

			}

			// Total count of items
			int itemCount = Integer.parseInt( httpResponse.getHeaders().getFirst( "total" ) );

			PushApplicationsResult pushApplicationsResult = new PushApplicationsResult();
			pushApplicationsResult.setTotalCount( itemCount );
			pushApplicationsResult.setPushApplications( pushApplications );
			return pushApplicationsResult;

		} catch (JsonParseException e) {
			throw new AerogearHelperException(e);
		} catch (JsonMappingException e) {
			throw new AerogearHelperException(e);
		} catch (IOException e) {
			throw new AerogearHelperException(e);
		}
	}

	@Override
	public HealthStatus showHealthInfo() throws AerogearHelperException {
		String healthUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/sys/info/health";
		HttpResponse httpResponse = getRequest(healthUrl, true);

		try {
			return objectMapper.readValue( httpResponse.getBody().toString(), HealthStatus.class );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}

	}

	@Override
	public Installation registerDevice(Installation installation, String variantId, String variantSecret ) throws AerogearHelperException {

		if (variantId == null || variantId.length() < 1 || variantSecret == null || variantSecret.length() < 1) {
			throw new IllegalArgumentException("variantId and variantSecret must not be null or empty");
		}

		String deviceRegistrationUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/registry/device";

		HttpResponse httpResponse = postRequest(deviceRegistrationUrl, false, installation, false, variantId, variantSecret);

		try {
			return objectMapper.readValue( httpResponse.getBody().toString(), Installation.class );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}

	@Override
	public void deleteDevice(String deviceToken, String variantId, String variantSecret) throws AerogearHelperException {
		if(deviceToken == null || deviceToken.length() < 1 ) {
			throw new IllegalArgumentException("deviceToken must not be empty");
		}
		String deviceDeletionUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/registry/device/" + deviceToken;
		deleteRequest(deviceDeletionUrl, false, variantId, variantSecret);
	}

	@Override
	public List<Installation> showInstallations(String variantId ) throws AerogearHelperException {
		String installationsOfVariantUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/export/" + variantId + "/installations";
		HttpResponse response = getRequest( installationsOfVariantUrl, true );
		try {
			List<Installation> installations = objectMapper.readValue( response.getBody().toString(),
			                                                           new TypeReference<List<Installation>>() {
			                                                           } );
			return installations;
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}

	@Override
	public List<iOSVariant> showIOSVariants( String appId ) throws AerogearHelperException {
		if (appId == null || appId.length() < 1) {
			throw new IllegalArgumentException( "appId must not be null or empty" );
		}
		String showVariantsUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/applications/" + appId + "/ios";
		HttpResponse response = getRequest( showVariantsUrl, true );
		try {
			// TODO Custom Deserializer? ( -> Can not construct instance of org.jboss.aerogear.unifiedpush.api.VariantType from String value ("ios"): value not one of declared Enum instance names: [WINDOWS_WNS, WINDOWS_MPNS, SIMPLE_PUSH, ADM, IOS, ANDROID])
			return objectMapper.readValue( response.getBody().toString(), new TypeReference<List<iOSVariant>>() {
			} );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}


	private String getCreateVariantEndpoint(final String appId, final Variant variant) {
		String createVariantsUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/applications/" + appId;

		switch (variant.getType()) {
			case ADM: return createVariantsUrl + "/adm";
			case ANDROID: return createVariantsUrl + "/android";
			case SIMPLE_PUSH: return createVariantsUrl + "/simplePush";
			case IOS: return createVariantsUrl + "/ios";
			case WINDOWS_MPNS: return createVariantsUrl + "/windowsMPNS";
			case WINDOWS_WNS: return createVariantsUrl + "/windowsWNS";
		}

		throw new IllegalStateException("Unknown variant type : " + variant.getType());
	}

	@Override
	public Variant createVariant(Variant variant, String appId) throws AerogearHelperException {
		if (appId == null || appId.length() < 1) {
			throw new IllegalArgumentException( "appId must not be null or empty" );
		}
		String createVariantsUrl = getCreateVariantEndpoint(appId, variant);

		HttpResponse response = postRequest( createVariantsUrl, true, variant, true );
		try {
			return objectMapper.readValue( response.getBody().toString(), variant.getClass() );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}

	@Override
	public iOSVariant createIOSVariant( iOSVariant iosVar, String appId ) throws AerogearHelperException {
		if (appId == null || appId.length() < 1) {
			throw new IllegalArgumentException( "appId must not be null or empty" );
		}
		String createVariantsUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/applications/" + appId + "/ios";

		HttpResponse response = postRequest( createVariantsUrl, true, iosVar, true );
		try {
			return objectMapper.readValue( response.getBody().toString(), iOSVariant.class );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}

	@Override
	public iOSVariant updateIOSVariant( iOSVariant iOSVariant, String appId, String variantId ) throws AerogearHelperException {
		if (appId == null || appId.length() < 1 || variantId == null || variantId.length() < 1) {
			throw new IllegalArgumentException( "appId and variantId must not be null or empty" );
		}
		String updateVariantsUrl = pushServer.getPushUrl() + PUSH_REST_CONTEXT + "/applications/" + appId + "/ios/" + variantId;
		HttpResponse response = putRequest( updateVariantsUrl, true, iOSVariant, true );
		try {
			return objectMapper.readValue( response.getBody().toString(), iOSVariant.class );
		} catch (IOException e) {
			throw new AerogearHelperException( e );
		}
	}


	/**
	 * check the response.
	 * throw an Exception for http status not 2xx (eg. 400, 401, 300, ...)
	 *
	 * @param response
	 * @throws AerogearHelperException if the response is not a status code '2XX'
     */
	private void checkRespone( HttpResponse response ) throws AerogearHelperException {
		if (response == null || !String.valueOf( response.getStatus() ).startsWith( "2" )) {
			throw new AerogearHelperException( "aerogear remote error. Status: " + response.getStatus() + " - "
			        + response.getStatusText() );
		}
	}

	/**
	 * validate the given bean
	 *
	 * @param bean
	 * @param <T>
	 * @throws AerogearHelperException if an ConstraintViolation happen
     */
	private <T> void validateBean( T bean ) throws AerogearHelperException {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate( bean );

		if (constraintViolations != null && constraintViolations.size() > 0) {
			for (ConstraintViolation cv : constraintViolations) {
				throw new AerogearHelperException( cv.getMessage() );
			}
		}
	}

	/*
		convinience methods for http calls
	 */


	private <T> HttpResponse getRequest( String url, boolean isKeycloak, String basicAuthUsername, String basicAuthPassword )
	        throws AerogearHelperException {
		return callPushServer( url, isKeycloak, HttpMethod.GET, null, false, basicAuthUsername, basicAuthPassword );
	}

	private <T> HttpResponse getRequest( String url, boolean isKeycloak ) throws AerogearHelperException {
		return callPushServer( url, isKeycloak, HttpMethod.GET, null, false, null, null );
	}

	private <T> HttpResponse postRequest( String url, boolean isKeycloak, T bean, boolean validateBean ) throws AerogearHelperException {
		return callPushServer( url, isKeycloak, HttpMethod.POST, bean, validateBean, null, null );
	}

	private <T> HttpResponse postRequest( String url, boolean isKeycloak, T bean, boolean validateBean, String basicAuthUsername,
	                                      String basicAuthPassword ) throws AerogearHelperException {
		return callPushServer( url, isKeycloak, HttpMethod.POST, bean, validateBean, basicAuthUsername, basicAuthPassword );
	}

	private <T> HttpResponse deleteRequest( String url, boolean isKeycloak, String basicAuthUsername, String basicAuthPassword )
			throws AerogearHelperException {
		return callPushServer( url, isKeycloak, HttpMethod.DELETE, null, false, basicAuthUsername, basicAuthPassword );
	}

	private <T> HttpResponse putRequest( String url, boolean isKeycloak, T bean, boolean validateBean ) throws AerogearHelperException {
		return callPushServer( url, isKeycloak, HttpMethod.PUT, bean, validateBean, null, null );
	}



	/**
	 *
	 * @param bean the EntityBody Bean for post requests
	 * @param validateBean javax validation for the {@code bean}
	 * @param url the reuquest url
	 * @param isKeycloakAuth use keycloack authentication
	 * @param httpMethod e.g. GET, POST, PUT, DELETE, ...
     * @param <T>
     * @return json response string
     */
	private <T> HttpResponse callPushServer(String url, boolean isKeycloakAuth, HttpMethod httpMethod, T bean, boolean validateBean, String basicAuthUsername, String basicAuthPassword) throws AerogearHelperException {

		try{
			if( url == null || url.length() < 1 ) {
				throw new IllegalArgumentException("url must not be null or empty");
			}

			// validate bean locally before sending
			if(validateBean) {
				validateBean( bean );
			}

			HttpRequest httpRequest = null;

			switch (httpMethod) {
				case POST:
					String beanString = objectMapper.writeValueAsString(bean);

					// mmhh Can't find an ObjectMapper implementation when use bean directly
					httpRequest = Unirest.post(url).body( beanString ).getHttpRequest();
					break;
				case GET:
					httpRequest = Unirest.get(url);
					break;
				case DELETE:
					httpRequest = Unirest.delete(url);
					break;
				case PUT:
					httpRequest = Unirest.put( url ).body( objectMapper.writeValueAsString( bean ) ).getHttpRequest();	//TODO variable (see POST)
					break;
			}

			// use keycloak Authentication
			if (isKeycloakAuth) {
				if (keycloakAccessToken == null || keycloakAccessToken.length() < 1) {
					keycloakAccessToken = KeycloakHelper.getAccessToken( pushServer );
				}
				httpRequest.header( "Authorization", "Bearer " + keycloakAccessToken );
			}

			// check for HTTP Basic Authentication
			if (basicAuthUsername != null && basicAuthPassword != null && basicAuthUsername.length() > 0
			        && basicAuthPassword.length() > 0) {
				httpRequest.basicAuth(basicAuthUsername, basicAuthPassword);
			}

			// make the call
			HttpResponse<JsonNode> jsonResponse = httpRequest
					.header( "Content-Type", "application/json" )
					.asJson();

			// check if 2xx response
			checkRespone( jsonResponse );

			return jsonResponse;

		} catch (UnirestException e) {
			throw new AerogearHelperException( "error on remote aerogear server", e );
		} catch (AerogearHelperException e) {
			throw new AerogearHelperException( e );
		} catch (JsonProcessingException e) {
			throw new AerogearHelperException( e );
		}
	}
}
