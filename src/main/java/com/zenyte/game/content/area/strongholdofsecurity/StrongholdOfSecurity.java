package com.zenyte.game.content.area.strongholdofsecurity;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.util.Utils;

import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 4. sept 2018 : 00:50:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class StrongholdOfSecurity implements ScheduledExternalizable {

	private static final List<Question> QUESTIONS = new ArrayList<Question>(35);
	
	public static final Question getRandomQuestion() {
		if (QUESTIONS.isEmpty()) {
			throw new RuntimeException("SoS Questions haven't been initialized yet.");
		}
		return QUESTIONS.get(Utils.random(QUESTIONS.size() - 1));
	}

	public static final class QuestionMessage {
		@Getter
		private final String[] strings;

		public QuestionMessage(final String... question) {
			strings = question;
		}
	}

	public static final class AnswerMessage {
		@Getter
		private final String option;
		@Getter
		private final String[] message;

		public AnswerMessage(final String option, final String... message) {
			this.option = option;
			this.message = message;
		}
	}

	public static class Question {

		@Getter
		private final QuestionMessage question;
		@Getter
		private final AnswerMessage[] answers;

		private Question(final QuestionMessage question, final AnswerMessage... answers) {
			this.question = question;
			this.answers = answers;
		}
	}

	@Override
	public int writeInterval() {
		return 0;
	}

	@Override
	public void read(final BufferedReader reader) {
		val questions = gson.fromJson(reader, Question[].class);
		for (val question : questions) {
			QUESTIONS.add(question);
		}
	}

	@Override
	public void write() {

	}

	@Override
	public String path() {
		return "data/stronghold of security questions.json";
	}

}
