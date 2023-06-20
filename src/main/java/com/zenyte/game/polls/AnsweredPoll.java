package com.zenyte.game.polls;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 26. march 2018 : 23:51.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class AnsweredPoll {

	/**
	 * The id of this poll.
	 */
	@Expose @Getter @Setter private int pollId;

	/**
	 * The title of the poll.
	 */
	@Expose @Getter @Setter private String title;
	
	/**
	 * The array of poll questions.
	 */
	@Expose @Getter @Setter private AnsweredPollQuestion[] questions;
	
	/**
	 * The date and time when the player submitted their answered to this poll.
	 */
	@Expose @Getter @Setter private LocalDateTime submitDate;
	
	
	public static final class AnsweredPollQuestion {
		
		/**
		 * The question in the poll.
		 */
		@Expose @Getter @Setter private String question;
		
		/**
		 * The answer the player chose.
		 */
		@Expose @Getter @Setter private String answer;
	}
	
}
