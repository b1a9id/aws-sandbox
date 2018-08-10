package com.coiney.awssandbox;

import com.amazonaws.services.cognitoidp.model.*;
import com.coiney.awssandbox.model.User;
import com.coiney.awssandbox.service.CognitoService;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CognitoServiceTest {

	private CognitoService cognitoService;

	private static final String USER_POOL_ID = "";
	private static final String APP_CLIENT_ID = "";

	@BeforeEach
	void beforeEach() {
		cognitoService = new CognitoService();
	}

	@Disabled
	@Nested
	class CreateUser {

		private User user;
		private String userName = "";
		private String tmpPassword = "";

		@BeforeEach
		void beforeEach() {
			user = new User();
		}

		@Test
		void success() {
			user.setUserName(userName);
			user.setPassword(tmpPassword);

			AdminCreateUserResult result =
					cognitoService.createUser(USER_POOL_ID, user.getUserName(), user.getPassword());
			assertAll(() -> {
				assertEquals(UserStatusType.FORCE_CHANGE_PASSWORD.toString(), result.getUser().getUserStatus());
				assertEquals(userName, result.getUser().getUsername());
			});
		}

		@Test
		void duplicatedUserName() {
			user.setUserName(userName);
			user.setPassword(tmpPassword);

			UsernameExistsException exception =
					assertThrows(UsernameExistsException.class, () -> cognitoService.createUser(USER_POOL_ID, user.getUserName(), user.getPassword()));
			assertTrue(exception.getMessage().contains("User account already exists"));
		}

		@Test
		void invalidPassword() {
			user.setUserName(userName);
			user.setPassword(tmpPassword);

			InvalidParameterException exception =
					assertThrows(InvalidParameterException.class, () -> cognitoService.createUser(USER_POOL_ID, user.getUserName(), user.getPassword()));
			System.out.println(exception.getMessage());
		}
	}

	@Nested
	class Authenticate {

		private User user;
		private String userName = "";
		private String password = "";

		@BeforeEach
		void beforeEach() {
			user = new User();
		}

		@Test
		void success() {
			user.setUserName(userName);
			user.setPassword(password);
			AdminInitiateAuthResult result = cognitoService.authenticate(USER_POOL_ID, APP_CLIENT_ID, user);
			assertNotNull(result.getAuthenticationResult().getAccessToken());
		}

		@Test
		void invalidPassword() {
			user.setUserName(userName);
			user.setPassword("abcd1234");
			NotAuthorizedException exception =
					assertThrows(NotAuthorizedException.class, () -> cognitoService.authenticate(USER_POOL_ID, APP_CLIENT_ID, user));
			assertTrue(exception.getMessage().contains("Incorrect username or password."));
		}
	}

	@Nested
	class ChangeTmpPassword {

		private User user;
		private String userName = "";
		private String tmpPassword = "";
		private String newPassword = "";

		@BeforeEach
		void beforeEach() {
			user = new User();
		}

		@Test
		void success() {
			user.setUserName(userName);
			user.setPassword(tmpPassword);
			AdminInitiateAuthResult adminInitiateAuthResult = cognitoService.authenticate(USER_POOL_ID, APP_CLIENT_ID, user);

			user.setPassword(newPassword);
			AdminRespondToAuthChallengeResult result =
					cognitoService.changeTmpPassword(adminInitiateAuthResult, USER_POOL_ID, APP_CLIENT_ID, user);
			assertNotNull(result.getAuthenticationResult().getAccessToken());
		}
	}

	@Nested
	class ResetPassword {

		private String userName = "";

		@Test
		void successPassword() {
			InvalidParameterException exception =
					assertThrows(InvalidParameterException.class, () -> cognitoService.resetPassword(userName, USER_POOL_ID));
			assertAll(() -> {
				assertTrue(
						exception.getMessage().contains("Cannot reset password for the user as there is no registered/verified email or phone_number"));
				assertEquals(HttpStatus.SC_BAD_REQUEST, exception.getStatusCode());
			});

		}
	}

	@Nested
	class GetUsers {

		@Test
		void success() {
			ListUsersResult result = cognitoService.getUsers(USER_POOL_ID);
			assertAll(() -> {
				assertEquals(1, result.getUsers().size());
				assertEquals("uchitate", result.getUsers().get(0).getUsername());
			});
		}
	}

	@Nested
	class GetUser {

		@Test
		void success() {
			AdminGetUserResult result = cognitoService.getUser(USER_POOL_ID, "uchitate");
			assertEquals("uchitate", result.getUsername());
		}

		@Test
		void userNotFound() {
			UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> cognitoService.getUser(USER_POOL_ID, "uchi"));
			assertTrue(exception.getMessage().contains("User does not exist."));
		}
	}

	@Nested
	class DeleteUser {

		@Test
		void success() {
			AdminDeleteUserResult result = cognitoService.deleteUser("uchitate", USER_POOL_ID);
			UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> cognitoService.getUser(USER_POOL_ID, "uchitate"));
			assertTrue(exception.getMessage().contains("User does not exist."));
		}
	}

	@Nested
	class UpdateUsername {

		@Test
		void success() {
			AdminSetUserSettingsResult result = cognitoService.updateUsername(USER_POOL_ID, "uchiuchi");
			assertEquals(200, result.getSdkHttpMetadata().getHttpStatusCode());
		}
	}

	@Nested
	class EnableUser {

		@Test
		void success() {
			AdminEnableUserResult result = cognitoService.enableUser(USER_POOL_ID, "test");
			assertEquals(200, result.getSdkHttpMetadata().getHttpStatusCode());
		}

		@Test
		void userNotFound() {
			UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> cognitoService.enableUser(USER_POOL_ID, "uchi"));
			assertTrue(exception.getMessage().contains("User does not exist."));
		}
	}

}