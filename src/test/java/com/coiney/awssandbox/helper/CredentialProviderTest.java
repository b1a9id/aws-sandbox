package com.coiney.awssandbox.helper;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CredentialProviderTest {

	@Disabled
	@Test
	void createAwsCredentialProvider() {
		AWSCredentialsProvider provider = CredentialProvider.createAwsCredentialProvider();
		Assertions.assertEquals("accessKey", provider.getCredentials().getAWSAccessKeyId());
		Assertions.assertEquals("secretKey", provider.getCredentials().getAWSSecretKey());
	}

}