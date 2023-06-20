/**
 * 
 */
package com.zenyte.database;

import lombok.Getter;

/**
 * @author Noele | Jun 19, 2018 : 12:14:15 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public enum DatabaseTopology {

	LOCAL(DatabaseCredential.LOCAL),
	BETA(DatabaseCredential.BETA)
	;
	
	@Getter
	private DatabaseCredential[] nodes;
		
	DatabaseTopology(final DatabaseCredential... nodes) {
		this.nodes = nodes;
	}
	
}
