package com.bol.game.domain.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Game {
    public UUID gameId;

    public List<Player> players;
    public int currentPlayer;

    public boolean finished;
    public int winner;
}
