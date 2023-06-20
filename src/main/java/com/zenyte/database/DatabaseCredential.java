package com.zenyte.database;

import lombok.Getter;

public enum DatabaseCredential {

	LOCAL("localhost", "zenyte", "xx01$xz$Zenyte!Discord"),
	BETA("172.50.1.4", "zenyte", "cde#Zenyte!xsw@Database!zaq!Password");
	
	@Getter
	private final String host, user, pass;
	
	DatabaseCredential(String host, String user, String pass) {
		this.host = host;
		this.user = user;
		this.pass = pass;
	}
	
}
