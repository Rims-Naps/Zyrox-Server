package com.zenyte.database;

import lombok.Getter;
import lombok.Setter;

public class SQLQuery {

	@Getter @Setter
	private SQLRunnable query;
	@Getter
	private DatabaseCredential database;
	
	public SQLQuery(final DatabaseCredential database, final SQLRunnable query) {
		this.query = query;
		this.database = database;
	}
	
}
