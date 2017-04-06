package modteam.login.common.model;

/**
 * Response object for calls made to AWS Cognito
 * 
 * @author Fumba Chibaka, Pramod Jakkannavar
 */
public class LoginResponse {

	private boolean status;
	private String providerName;
	private String errorMessage;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
