package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.DatabaseUtil;
import com.zenyte.database.SQLRunnable;
import com.zenyte.database.structs.SkillInformation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerInformation;
import com.zenyte.game.world.entity.player.Skills;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Slf4j
public class SkillHiscores extends SQLRunnable {

	private Player player;
	
	public SkillHiscores(final Player player) {
		this.player = player;
	}
	
	@Override
	public void execute(final DatabaseCredential auth) {
		final String query = DatabaseUtil.buildBatch("INSERT INTO skill_hiscores (userid, username, mode, skill_id, skill_name, level, experience) "+
				"VALUES (?, ?, ?, ?, ?, ?, ?)", 23, 7);

		final PlayerInformation info = player.getPlayerInformation();
		final Skills skills = player.getSkills();

		if(info.getUserIdentifier() == -1)
			return;

		try(final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
			final PreparedStatement del = con.prepareStatement("DELETE FROM skill_hiscores WHERE userid=?");
			final PreparedStatement pst = con.prepareStatement(query)) {

				// delete previous entries
				del.setInt(1, info.getUserIdentifier());
				del.execute();

				int index = 0;
				for(int skill = 0; skill < 23; skill++) {
					pst.setInt(++index, info.getUserIdentifier());
					pst.setString(++index, info.getDisplayname());
					pst.setInt(++index, player.getGameMode().ordinal());
					pst.setInt(++index, skill);
					pst.setString(++index, SkillInformation.all.get(skill).toString().toLowerCase());
					pst.setInt(++index, skills.getLevel(skill));
					pst.setInt(++index, (int) skills.getExperience(skill));
				}

				pst.setInt(++index, info.getUserIdentifier());
				pst.setString(++index, info.getDisplayname());
				pst.setInt(++index, 0);
				pst.setInt(++index, -1);
				pst.setString(++index, "total");
				pst.setInt(++index, skills.getTotalLevel());
				pst.setInt(++index, skills.getTotalXp());

				pst.execute();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}
}
