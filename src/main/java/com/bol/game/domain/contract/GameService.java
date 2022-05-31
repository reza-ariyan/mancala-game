package com.bol.game.domain.contract;

import com.bol.game.domain.model.Game;
import com.bol.game.domain.model.Sow;

import java.util.UUID;

public interface GameService {

    /**
     * This method returns a valid game Id if the passed game Id is not correct UUID
     *
     * @param gameId a string that can be null or empty or a valid UUID
     * @return UUID A valid UUID for creating or resolving a game
     */
    UUID resolveGameId(String gameId);

    /**
     * This method looks for a game by its Id, if the game is not found, it initializes a new game and returns an instance from it
     *
     * @param gameId a valid UUID that's an identifier for the game
     * @return Game, an instance from a game
     */
    Game getGame(UUID gameId);

    /**
     * Sow or an action of sow, user's movement
     *
     * @param sow contains the pit number that stones are going to be moved from and also the player name
     * @return Game, Updated game details
     */
    Game sow(Sow sow);

}