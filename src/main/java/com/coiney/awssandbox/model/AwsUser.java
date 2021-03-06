package com.coiney.awssandbox.model;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class AwsUser implements Serializable {

	private final String profile;

	private final Regions region;

	private final ProfileCredentialsProvider credentialsProvider;
}
