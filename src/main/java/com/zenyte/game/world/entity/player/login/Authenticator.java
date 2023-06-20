package com.zenyte.game.world.entity.player.login;

import com.zenyte.game.util.Utils;
import com.zenyte.network.ClientResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Kris | 10. apr 2018 : 18:58.06
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Getter
public final class Authenticator {

    private final Map<Integer, Long> trustedPCs = new HashMap<>();
    @Setter private transient boolean enabled;
	private transient boolean trusted;
	private transient final int randomUID = Utils.random(Integer.MAX_VALUE - 1);

	public final ClientResponse validate(final int identifier) {
		val date = trustedPCs.get(identifier);
		if (date == null || date < System.currentTimeMillis()) {
		    return ClientResponse.AUTHENTICATOR;
		}
		return ClientResponse.LOGIN_OK;
	}

	final void trust() {
        val date = trustedPCs.get(randomUID);
        if (date == null || date < Utils.currentTimeMillis()) {
            //Only refresh the trusted pc if the old one has expired.
            trusted = true;
            trustedPCs.put(randomUID, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
        }
    }
}
