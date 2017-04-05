package modteam.login.api;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.regions.Regions;
//import the required packages from the AWS SDK for Java
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;

import modteam.login.common.model.LoginResponse;

@Controller
@EnableAutoConfiguration
@RequestMapping("/api")
public class LoginController {

	private static final String AWS_ACCOUNT_ID = "AWS_ACCOUNT_ID";
	private static final String AWS_COGNITO_IDENTITY_POOL_ID = "AWS_COGNITO_IDENTITY_POOL_ID";

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody LoginResponse authenticateUser(@RequestBody String request) {

		AWSCredentials awsCredentials = new AnonymousAWSCredentials();
		Regions region = Regions.US_WEST_2;
		// initialize the Cognito identity client
		AmazonCognitoIdentity identityClient = AmazonCognitoIdentityClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();

		// send a get id request. This only needs to be executed the first time
		// and the result should be cached.
		GetIdRequest idRequest = new GetIdRequest();
		idRequest.setAccountId(System.getenv(AWS_ACCOUNT_ID));
		idRequest.setIdentityPoolId(System.getenv(AWS_COGNITO_IDENTITY_POOL_ID));
		// If you are authenticating your users through an identity provider
		// then you can set the Map of tokens in the request
		HashMap<String, String> providerTokens = new HashMap<String, String>();
		providerTokens.put("graph.facebook.com", "facebook session key");
		idRequest.setLogins(providerTokens);

		GetIdResult idResp = identityClient.getId(idRequest);

		String identityId = idResp.getIdentityId();
		System.out.println(identityId);

		// TODO: At this point you should save this identifier so you won't
		// have to make this call the next time a user connects

		return new LoginResponse();
	}

}