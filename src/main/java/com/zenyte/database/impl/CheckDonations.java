package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import com.zenyte.game.content.donation.DonationHandler;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


 @Slf4j
 public class CheckDonations extends SQLRunnable {

    private Player player;

    public CheckDonations(final Player player) {
         this.player = player;
    }

    @Override
    public void execute(final DatabaseCredential auth) {
        final List<Item> rewards = new ArrayList<>();

        try(final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
            final PreparedStatement stmt = con.prepareStatement("SELECT * FROM store_purchases WHERE claimed = 0 AND userid = ?");
            final PreparedStatement stmt2 = con.prepareStatement("UPDATE store_purchases SET claimed = 1 WHERE userid = ? and claimed = 0")) {
            stmt.setInt(1, player.getPlayerInformation().getUserIdentifier());

            try(final ResultSet set = stmt.executeQuery()) {

                while (set.next()) {
                    final int id = set.getInt(set.findColumn("item_id"));
                    final int amount = set.getInt(set.findColumn("quantity"));

                    player.sendMessage("Item: " + id + " amount: " + amount);

                    if (id == 0 || amount == 0)
                        continue;

                    rewards.add(new Item(id, amount));
                }

                stmt2.setInt(1, player.getPlayerInformation().getUserIdentifier());
                stmt2.execute();}

            DonationHandler.claim(player, rewards);

        } catch (final Exception e) {
             log.error(Strings.EMPTY, e);
        }
    }

    @Override
   public void prepare() {
       DatabasePool.submit(this);
    }
 }
