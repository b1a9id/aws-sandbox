package com.coiney.awssandbox.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable {

	private String userName;
	private String password;
}
