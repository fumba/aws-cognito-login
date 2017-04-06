package modteam.login.api;

/**
 * Federated identities - Provides identity for users logged in using a provider. 
 * 
 * @author Fumba Chibaka, Pramod Jakkannavar
 */
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;

import modteam.login.common.model.LoginResponse;

@Controller
@EnableAutoConfiguration
@RequestMapping("/api")
public class LoginController {

	@Autowired
	private Environment env;
	private final String GOOGLE_AUTH_PROVIDER_NAME = "accounts.google.com";
	private final String FACEBOOK_AUTH_PROVIDER_NAME = "graph.facebook.com";
	private final String GOOGLE = "google";
	private final String FACEBOOK = "facebook";
	private Map<String, String> accessCache = new HashMap<String, String>();
	private LoginResponse response;
	private GetIdResult idResp;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody LoginResponse authenticateUser(@RequestParam String token, @RequestParam String provider) {

		response = new LoginResponse();
		response.setProviderName(provider);
		response.setErrorMessage("");

		if (accessCache.containsKey(token)) {
			response.setStatus(true);
		} else {
			AWSCredentials awsCredentials = new BasicAWSCredentials(env.getProperty("ACCESS_KEY"),
					env.getProperty("SECRET_KEY"));

			Regions region = Regions.US_WEST_2;
			// initialize the cognito identity client
			AmazonCognitoIdentity identityClient = AmazonCognitoIdentityClientBuilder.standard().withRegion(region)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
			// send a get id request. This only needs to be executed the first
			// time and the result should be cached.
			GetIdRequest idRequest = new GetIdRequest();
			idRequest.setAccountId(env.getProperty("AWS_ACCOUNT_ID"));
			idRequest.setIdentityPoolId(env.getProperty("AWS_COGNITO_IDENTITY_POOL_ID"));

			// google- identity provider
			HashMap<String, String> providerTokens = new HashMap<String, String>();
			if (provider.equals(GOOGLE)) {
				providerTokens.put(GOOGLE_AUTH_PROVIDER_NAME, token);
			} else if (provider.equals(FACEBOOK)) {
				providerTokens.put(FACEBOOK_AUTH_PROVIDER_NAME, token);
			} else {
				response.setErrorMessage("Provider name needs to be specified.");
				return response;
			}
			idRequest.setLogins(providerTokens);

			try {
				idResp = identityClient.getId(idRequest);
				// save identifier to cache
				String identityId = idResp.getIdentityId();
				accessCache.put(token, identityId);
				response.setStatus(true);
			} catch (Exception e) {
				response.setStatus(false);
				response.setErrorMessage(e.getMessage());
			}
		}
		return response;
	}

}