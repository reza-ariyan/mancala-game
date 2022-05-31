package com.bol.game.domain.valueObjects;

import com.bol.game.domain.WellKnown;

public record Pit(int value) {
    public static int Empty = WellKnown.Empty;
    public static int DEFAULT_STONES_IN_EACH_PIT = WellKnown.STONES_IN_EACH_PIT;

    public Pit {
        if (value < WellKnown.Empty)
            throw new IllegalArgumentException("Stones in pit couldn't be less than " + WellKnown.Empty);
    }
}
