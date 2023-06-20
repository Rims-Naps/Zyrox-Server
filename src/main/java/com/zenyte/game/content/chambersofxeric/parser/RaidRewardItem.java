package com.zenyte.game.content.chambersofxeric.parser;

public class RaidRewardItem {
    private String itemName;
    private int itemAmount;

    public String getItemName() {
        return itemName;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public RaidRewardItem(String itemName, int itemAmount) {
        this.itemName = itemName;
        this.itemAmount = itemAmount;
    }
}
