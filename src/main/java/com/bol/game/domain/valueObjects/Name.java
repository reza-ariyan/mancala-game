package com.bol.game.domain.valueObjects;

import com.bol.game.domain.WellKnown;

import javax.validation.constraints.NotEmpty;

public record Name(@NotEmpty String value) {
    public static String DEFAULT = WellKnown.PLAYER_NAME_SUFFIX;

}
