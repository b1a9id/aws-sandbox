package com.coiney.awssandbox.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.coiney.awssandbox.helper.AwsUserHelper;
import com.coiney.awssandbox.model.AwsUser;
import com.coiney.awssandbox.model.User;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class CognitoService {

	public AdminCreateUserResult createUser(String userPoolId, String userName, String tmpPassword) {
		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();

		AdminCreateUserRequest request = new AdminCreateUserRequest();
		request.withUserPoolId(userPoolId)
				.withUsername(userName)
				.withTemporaryPassword(tmpPassword);
		return client.adminCreateUser(request);
	}

	public AdminInitiateAuthResult authenticate(String userPoolId, String clientId, User user) {
		Map<String, String> authParams = new HashMap<>();
		authParams.put("USERNAME", user.getUserName());
		authParams.put("PASSWORD", user.getPassword());

		AdminInitiateAuthRequest request = new AdminInitiateAuthRequest();
		request.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
				.withUserPoolId(userPoolId)
				.withClientId(clientId)
				.withAuthParameters(authParams);

		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminInitiateAuth(request);
	}

	public AdminRespondToAuthChallengeResult changeTmpPassword(AdminInitiateAuthResult result, String userPoolId, String clientId, User user) {
		Map<String, String> challengeResponses = new HashMap<>();
		challengeResponses.put("USERNAME", user.getUserName());
		challengeResponses.put("NEW_PASSWORD", user.getPassword());

		AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
		request.withChallengeName(result.getChallengeName())
				.withUserPoolId(userPoolId)
				.withClientId(clientId)
				.withSession(result.getSession())
				.withChallengeResponses(challengeResponses);

		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminRespondToAuthChallenge(request);
	}

	public AdminResetUserPasswordResult resetPassword(String userName, String userPoolId) {
		AdminResetUserPasswordRequest request = new AdminResetUserPasswordRequest();
		request.withUsername(userName)
				.withUserPoolId(userPoolId);

		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminResetUserPassword(request);
	}

	public AdminDeleteUserResult deleteUser(String username, String userPoolId) {
		AdminDeleteUserRequest request = new AdminDeleteUserRequest();
		request.withUsername(username)
				.withUserPoolId(userPoolId);

		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminDeleteUser(request);
	}

	public ListUsersResult getUsers(String userPoolId) {
		ListUsersRequest request = new ListUsersRequest();
		request.withUserPoolId(userPoolId);
		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.listUsers(request);
	}

	public AdminGetUserResult getUser(String userPoolId, String userName) {
		AdminGetUserRequest request = new AdminGetUserRequest();
		request.withUserPoolId(userPoolId)
				.withUsername(userName);
		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminGetUser(request);
	}

	public AdminSetUserSettingsResult updateUsername(String userPoolId, String username) {
		AdminSetUserSettingsRequest request = new AdminSetUserSettingsRequest();
		request.withUserPoolId(userPoolId)
				.withUsername(username);
		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminSetUserSettings(request);
	}

	public AdminEnableUserResult enableUser(String userPoolId, String username) {
		AdminEnableUserRequest request = new AdminEnableUserRequest();
		request.withUserPoolId(userPoolId)
				.withUsername(username);
		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		return client.adminEnableUser(request);
	}

	public boolean verifyToken(String accessToken, String kid, String issuer) {
		DecodedJWT decodedJWT;
		try {
			decodedJWT = JWT.decode(accessToken);
		} catch (JWTDecodeException e) {
			return false;
		}

		// ローカルのキーID(kid)とパブリックのkidの比較
		if (!ObjectUtils.nullSafeEquals(kid, decodedJWT.getKeyId())) {
			return false;
		}

		// 有効期限が切れていない
		LocalDateTime expiredAt = decodedJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(expiredAt)) {
			return false;
		}

		// 発行者(iss)のクレームがユーザプールと一致する
		if (!ObjectUtils.nullSafeEquals(issuer, decodedJWT.getIssuer())) {
			return false;
		}
		return true;
	}

	public String refreshToken(String userPoolId, String clientId, String refreshToken) {
		AdminInitiateAuthRequest request = new AdminInitiateAuthRequest();
		request.withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
				.withUserPoolId(userPoolId)
				.withClientId(clientId)
				.addAuthParametersEntry("REFRESH_TOKEN", refreshToken);

		AWSCognitoIdentityProvider client = getCognitoIdentityProvider();
		AdminInitiateAuthResult result = client.adminInitiateAuth(request);
		return result.getAuthenticationResult().getAccessToken();
	}

	private AWSCognitoIdentityProvider getCognitoIdentityProvider() {
		AwsUser awsUser = AwsUserHelper.getAwsUser();
		return AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(awsUser.getCredentialsProvider())
				.withRegion(awsUser.getRegion())
				.build();
	}
}
