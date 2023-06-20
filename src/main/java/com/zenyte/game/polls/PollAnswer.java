package com.zenyte.game.polls;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 26. march 2018 : 3:35.22
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class PollAnswer {

	/**
	 * The name of the choice in the poll.
	 */
	@Expose @Getter @Setter private String choice;
	
	/**
	 * The number of votes this choice has received.
	 */
	@Expose @Getter @Setter private int votes;
	
}
