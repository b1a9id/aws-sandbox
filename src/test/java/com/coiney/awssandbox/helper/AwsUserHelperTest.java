package com.coiney.awssandbox.helper;

import com.amazonaws.regions.Regions;
import com.coiney.awssandbox.model.AwsUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AwsUserHelperTest {

	@Test
	void getAwsUser() {
		AwsUser awsUser = AwsUserHelper.getAwsUser();
		assertAll(
				() -> assertEquals("develop-ruchitate", awsUser.getProfile()),
				() -> assertEquals(Regions.AP_NORTHEAST_1, awsUser.getRegion())
		);
	}
}