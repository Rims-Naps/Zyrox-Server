package com.zenyte.game;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.event.EventTicketDispenser;
import com.zenyte.game.content.tog.juna.JunaEnterDialogue;
import com.zenyte.game.content.tog.juna.JunaOutsideOptionDialogue;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.plugins.dialogue.BertD;
import com.zenyte.plugins.dialogue.WiseOldManD;
import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;
import lombok.val;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Polar | 7. mai 2018 : 02:05:06
 */
public final class RuneDate {

	private static final Calendar CALENDAR = Calendar.getInstance();

	private static final ZoneId UTC = ZoneId.of("UTC");

	/**
	 * Checks the last login by the player, used to reset daily activities.
	 * 
	 * @param player
	 *            the player who to check.
	 */
	@Listener(type = ListenerType.LOBBY_CLOSE)
	public static final void checkDate(final Player player) {
		final int lastLogin = player.getVariables().getLastLogin();
        final int currentDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        player.getVariables().setLastLogin(currentDate);
		if (currentDate != lastLogin) {
			if (player.getVariables().getRaidAdvertsQuota() < 15) {
				player.getVariables().setRaidAdvertsQuota(15);
			}
			player.getVariables().setFountainOfRuneTeleports(0);
			player.getVariables().setArdougneFarmTeleports(0);
			player.getVariables().setFishingColonyTeleports(0);
			player.getVariables().setSherlockTeleports(0);
			player.getVariables().setFaladorPrayerRecharges(0);
			player.getVariables().setRellekkaTeleports(0);
			player.getVariables().setRunReplenishments(0);
			player.getVariables().setFreeAlchemyCasts(0);
			player.getVariables().setCabbageFieldTeleports(0);
			player.getVariables().setGodwarsGhommalHiltTeleports(0);
			player.getVariables().setMorulrekGhommalHiltTeleports(0);
			player.getVariables().setNardahTeleports(0);
			player.getVariables().setKourendWoodlandTeleports(0);
			player.getVariables().setMountKaruulmTeleports(0);
			player.getVariables().setSpellbookSwaps(0);
			player.getVariables().setZulrahResurrections(0);
			player.getVariables().setGrappleAndCrossbowSearches(0);
			player.getVariables().setTeletabPurchases(0);
			player.getVariables().setClaimedBattlestaves(false);
			player.getAttributes().remove("DAILY_PURE_ESSENCE");
			player.getAttributes().remove("DAILY_LUNDAIL_RUNES");
			player.getAttributes().remove("DAILY_SAND");
			player.getAttributes().remove("DAILY_FLAX");
			if (DiaryReward.ARDOUGNE_CLOAK4.eligibleFor(player) && player.getAttributes().containsKey("WANTS_SAND")) {
				if (!player.getAttributes().containsKey("DAILY_SAND") && (player.getBank().hasFreeSlots() || (player.getBank().containsItem(new Item(ItemId.BUCKET_OF_SAND)) && player.getBank().getAmountOf(1783) < 2147000000))) {
					for (int i = 84; i > 0; --i) {
						player.getBank().add(new Item(1784));
					}
					player.getAttributes().put("DAILY_SAND", 1);
				} else if (!(player.getBank().hasFreeSlots())) {
					player.sendMessage(Colour.RED.wrap("Your bank was full so you did not receive your daily buckets of sand."));
				}
			}
			player.addAttribute(EventTicketDispenser.ATTR_DAILY_GIVEN_AMOUNT, 0);
			VarCollection.DAILY_BATTLESTAVES_COLLECTED.update(player);
			val challenge = player.getDailyChallengeManager().getRandomChallenge();
			if (challenge != null) {
				player.getDailyChallengeManager().assignChallenge(challenge);
			}
			player.sendMessage(Colour.RS_PINK.wrap("Your daily limits have been reset."));
			if (JunaEnterDialogue.hasTimePassed(player) && player.getBooleanAttribute(JunaOutsideOptionDialogue.TOG_REMINDER_ENABLED_ATTR)) {
				player.sendMessage(Colour.RED.wrap("You are eligible to drink from the Tears of Guthix."));
			}
			player.addAttribute(WiseOldManD.ALREADY_VOTED_TODAY, 0);
			if(player.getNumericAttribute(WiseOldManD.BOOSTER_END).longValue() > 0)
			{
				long endTime = player.getNumericAttribute(WiseOldManD.BOOSTER_END).longValue();
				if(System.currentTimeMillis() > endTime)
				{
					player.sendMessage("Your 7 day booster privileges have ended. Vote for 7 more days or use a Discord Server boost to gain your perks back.");
					if(player.getPrivilege().eligibleTo(Privilege.BOOSTER) && !player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR))
					{
						player.setPrivilege(Privilege.PLAYER);
					}
					player.addAttribute(WiseOldManD.BOOSTER_END, 0L);
				}
			}
		}
	}

	/**
	 * Returns the current date in minutes.
	 *
	 * @return The current date converted into minutes.
	 */
	public static int getMinutes() {
		return getMinutes(currentTimeMillis());
	}

	/**
	 * Gets the date in minutes for {@code timestamp}.
	 *
	 * @param timestamp
	 *            The time.
	 * @return The date in minutes.
	 */
	public static int getMinutes(final long timestamp) {
		return (int) (timestamp / 60000L);
	}

	/**
	 * Returns the current year.
	 *
	 * @return The current year.
	 */
	public static int getYear() {
		return getYear(currentTimeMillis());
	}

	/**
	 * Gets the year based on {@code timestamp}.
	 *
	 * @param timestamp
	 *            The timestamp.
	 * @return The year the timestamp is in.
	 */
	public static int getYear(final long timestamp) {
		CALENDAR.clear();
		CALENDAR.setTime(new Date(timestamp));

		return CALENDAR.get(Calendar.YEAR);
	}

	/**
	 * Returns the current date in RuneDays.
	 *
	 * @return The current date converted into RuneDays.
	 */
	public static int getDate() {
		return (int) (currentTimeMillis() / 86400000L - 11745);
	}

	/**
	 * Converts {@code date} to the number of RuneDays.
	 *
	 * @param date
	 *            The date to convert.
	 * @return The converted amount of RuneDays.
	 */
	public static int fromDate(final LocalDate date) {
		CALENDAR.clear();
		CALENDAR.setTimeZone(TimeZone.getTimeZone(UTC));
		CALENDAR.set(Calendar.HOUR_OF_DAY, 12);
		CALENDAR.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

		return (int) (CALENDAR.getTime().getTime() / 86400000L) - 11745;
	}

	/**
	 * @param runeDate
	 * @return
	 */
	public static LocalDate toDate(final int runeDate) {
		final long timestamp = 86400000L * (11745 + runeDate);

		CALENDAR.clear();
		CALENDAR.setTimeInMillis(timestamp);

		return CALENDAR.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Determines if the given {@code year} is a leap year.
	 *
	 * @param year
	 *            The year.
	 * @return {@code true} if the year is a leap year, otherwise {@code false}.
	 */
	public static boolean isLeapYear(final int year) {
		if (year < 0) {
			return (year + 1) % 4 == 0;
		} else if (year < 1582) {
			return year % 4 == 0;
		} else if (year % 4 != 0) {
			return false;
		} else if (year % 100 != 0) {
			return true;
		} else if (year % 400 != 0) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the current time in milliseconds based on UTC timezone.
	 *
	 * @return The timestamp.
	 */
	public static long currentTimeMillis() {
		return Instant.now().atZone(UTC).toInstant().toEpochMilli();
	}

	/**
	 * Converts the given {@code timestamp} to a {@link LocalDate}.
	 *
	 * @param timestamp
	 *            The timestamp to convert.
	 * @return The local date instance.
	 */
	public static LocalDate localDateFromTimeStamp(final long timestamp) {
		return Instant.ofEpochMilli(timestamp).atZone(UTC).toLocalDate();
	}

	/**
	 * Get the time until the next {@code x} minute interval. Example usage is
	 * to find the time until the next :05 interval for delaying a task on
	 * startup.
	 *
	 * @param x
	 *            The interval to use.
	 * @return The time in milliseconds until the next interval.
	 */
	public static long getOffsetForNextInterval(final int x) {
		final LocalDateTime now = LocalDateTime.now();

		final int nextInterval = now.getMinute() + (x - now.getMinute() % x);
		final int minuteOffset = nextInterval - now.getMinute() - 1;
		final int secondOffset = 60 - now.getSecond();

		return ((minuteOffset * 60) + secondOffset) * 1_000;
	}

}