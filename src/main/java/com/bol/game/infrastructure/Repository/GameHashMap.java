package com.bol.game.infrastructure.Repository;

import com.bol.game.domain.WellKnown;
import com.bol.game.domain.model.Game;
import com.bol.game.domain.model.Player;
import com.bol.game.domain.valueObjects.Name;

import java.util.*;

public class GameHashMap extends HashMap<UUID, Game> {

    public Game initialize(UUID gameId) {
        var game = this.initiate(gameId);
        return put(gameId, game);
    }

    private Game initiate(UUID gameId) {
        List<Player> players = new ArrayList<>();
        for (var i = WellKnown.FIRST_PLAYER_INDEX; i < WellKnown.PLAYERS_COUNT; i++) {
            var pits = new int[WellKnown.TOTAL_PITS];
            //Fill pits with stones
            Arrays.fill(pits, WellKnown.STONES_IN_EACH_PIT);
            var playerName = new Name(Name.DEFAULT + i);
            var player = Player.builder().smallPits(pits).name(playerName).build();
            players.add(player);
        }

        return Game.builder()
                .gameId(gameId)
                .currentPlayer(WellKnown.FIRST_PLAYER_INDEX)
                .players(players)
                .build();
    }
}