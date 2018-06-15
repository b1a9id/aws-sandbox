package com.coiney.awssandbox.helper;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class CredentialProvider {

	private static final String PROFILE = "develop-ruchitate";

	public static AWSCredentialsProvider createAwsCredentialProvider() {
		return new ProfileCredentialsProvider(PROFILE);
	}
}
