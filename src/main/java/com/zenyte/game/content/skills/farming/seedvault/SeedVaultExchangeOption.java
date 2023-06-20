package com.zenyte.game.content.skills.farming.seedvault;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SeedVaultExchangeOption {
    SELECTED(1, 1),
    ONE(2, 1),
    FIVE(3, 5),
    TEN(4, 10),
    X(5, -1),
    ALL(6, Integer.MAX_VALUE),
    FAVORITE(7, -1),
    NOTE_OR_REMOVE_PLACE(8, -1),
    REMOVE_ALL_PLACE(9, -1),
    EXAMINE(10, -1);

    private static final SeedVaultExchangeOption[] options = values();
    private final int optionId;
    private final int amount;

    public static SeedVaultExchangeOption of(final int optionId) {
        for (SeedVaultExchangeOption option : options) {
            if (option.getOptionId() == optionId) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown item option for seed vault [" + optionId + "]");
    }
}