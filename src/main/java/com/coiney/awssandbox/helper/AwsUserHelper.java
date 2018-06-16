package com.coiney.awssandbox.helper;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.regions.Regions;
import com.coiney.awssandbox.model.AwsUser;

public class AwsUserHelper {

	private static final String PROFILE = "develop-ruchitate";

	public static AwsUser getAwsUser() {
		AwsProfileRegionProvider regionProvider = new AwsProfileRegionProvider("profile " + PROFILE);
		Regions region = Regions.fromName(regionProvider.getRegion());
		ProfileCredentialsProvider credentialsProvider =  new ProfileCredentialsProvider(PROFILE);
		return new AwsUser(
				PROFILE, region,
				credentialsProvider.getCredentials().getAWSAccessKeyId(), credentialsProvider.getCredentials().getAWSSecretKey());
	}
}
