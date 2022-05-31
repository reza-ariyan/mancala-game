package com.bol.game.application.services;

import com.bol.game.domain.contract.GameService;
import com.bol.game.domain.model.Game;
import com.bol.game.domain.model.Player;
import com.bol.game.domain.model.Sow;
import com.bol.game.infrastructure.Repository.GameHashMap;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

import static com.bol.game.domain.WellKnown.*;

@Service
public class GameServiceImpl implements GameService {
    private final GameHashMap games = new GameHashMap();

    /**
     * This method returns a valid game Id if the passed game Id is not correct UUID
     *
     * @param gameId a string that can be null or empty or a valid UUID
     * @return UUID A valid UUID for creating or resolving a game
     */
    @Override
    public UUID resolveGameId(String gameId) {
        try {
            return UUID.fromString(gameId);
        } catch (IllegalArgumentException exception) {
            return UUID.randomUUID();
        }
    }

    /**
     * This method looks for a game by its Id, if the game is not found, it initializes a new game and returns an instance from it
     *
     * @param gameId a valid UUID that's an identifier for the game
     * @return Game, an instance of a game
     */
    @Override
    public Game getGame(UUID gameId) {
        return games.containsKey(gameId) ? games.get(gameId) : this.initialize(gameId);
    }

    /**
     * This method initializes a game
     *
     * @param gameId the game id for the game that need to be initialized
     * @return Game, an instance from a game that is newly initialized
     */
    public Game initialize(UUID gameId) {
        return games.initialize(gameId);
    }

    /**
     * Sow or an action of sow, user's movement
     *
     * @param sow contains the pit number that stones are going to be moved from and also the player name
     * @return Game, Updated game details
     */
    @Override
    public Game sow(Sow sow) {
        var game = this.getGame(sow.getGameId());
        if (game.getCurrentPlayer() != sow.getPlayer()) return game;
        int pitNumber = sow.getPitNumber();

        var getAnotherTurn = move(game, pitNumber);

        if (!getAnotherTurn) {
            switchToNextPlayer(game);
        }
        game.setFinished(gameIsFinished(game));
        return game;
    }

    /**
     * This method moves the stones from the provided pit number
     *
     * @param game      The game that is being played
     * @param pitNumber The pit number that stones should be moved from
     * @return boolean If user gets another turn this action will return true
     */
    private boolean move(Game game, int pitNumber) {
        Player player = game.players.get(game.currentPlayer);
        Player opponent = game.players.get(getNextPlayer(game));

        int[] pits = player.getSmallPits();
        int stones = pits[pitNumber];
        boolean getAnotherTurn = false;
        //Start moving stones from here
        pits[pitNumber] = Empty;
        if (stones == Empty) return true;
        while (stones != Empty) {
            //Fills the stones in players own pits
            int i = pitNumber + 1;
            while (i < TOTAL_PITS) {
                //Adds one stone to the next pit
                pits[i++]++;
                //Removes one stone from hand (picked up stones)
                stones--;
                //Capturing Stones
                //When the last stone lands in an own empty pit, the player captures
                //his own stone including all the stones in the opposite pit and puts them in his own large pit.
                if (stones == Empty) {
                    var lastLandedPitIndex = i - 1;
                    var lastLandedPitValue = pits[lastLandedPitIndex];
                    var oppositePitIndex = TOTAL_PITS - lastLandedPitIndex - 1;
                    var oppositePitValue = opponent.smallPits[oppositePitIndex];
                    if (lastLandedPitValue == 1 && oppositePitValue > Empty) {
                        player.largePit += lastLandedPitValue + oppositePitValue;
                        opponent.smallPits[oppositePitIndex] = Empty;
                        pits[lastLandedPitIndex] = Empty;
                    }
                    break;
                }
            }

            //This part puts a stone into the large pit
            //If the player's last stone lands in his own large pit, he gets another turn
            getAnotherTurn = stones != Empty;
            if (getAnotherTurn) {
                player.largePit++;
                stones--;
            }

            if (stones != Empty) {
                getAnotherTurn = false;
                var opponentPits = opponent.getSmallPits();
                var from = 0;
                var to = Math.min(stones, TOTAL_PITS - 1);
                while (from < to) {
                    //When all stones in hand (stones that were picked up) are spent
                    if (stones == Empty) break;
                    opponentPits[from++]++;
                    stones--;
                }
                opponent.setSmallPits(opponentPits);
            }
            player.setSmallPits(pits);
        }
        return getAnotherTurn;
    }

    /**
     * Checks if the game is finished
     *
     * @param game The game that is being played
     * @return boolean If the game is finished this will return true
     */
    private boolean gameIsFinished(Game game) {
        boolean pitsAreEmpty = false;
        //When one of players' all pits are empty, the game is finished
        for (var player : game.getPlayers()) {
            pitsAreEmpty = pitsAreEmpty || player.pitsAreEmpty();
        }
        if (pitsAreEmpty) updateWinnerStatus(game);
        return pitsAreEmpty;
    }


    /**
     * Updates the winner status, sets the winner with appropriate winner by checking all players
     *
     * @param game The game that is being played
     */
    private void updateWinnerStatus(Game game) {
        var players = game.players;
        Player winner = players.get(FIRST_PLAYER_INDEX);
        var winnerStonesCount = Empty;
        for (var player : players) {
            int stonesCount = player.largePit + Arrays.stream(player.getSmallPits()).sum();
            if (stonesCount > winnerStonesCount) {
                winner = player;
                winnerStonesCount = stonesCount;
            }
        }
        game.setWinner(players.indexOf(winner));
    }

    /**
     * Changes the current player, turns game to other player
     *
     * @param game The game that is being played
     */
    private void switchToNextPlayer(Game game) {
        game.setCurrentPlayer(getNextPlayer(game));
    }

    /**
     * Returns the next player that should play the game
     *
     * @param game The game that is being played
     * @return int, the index of the next player
     */
    private static int getNextPlayer(final Game game) {
        var nextPlayerIndex = game.currentPlayer + 1;
        var lastPlayerIndex = PLAYERS_COUNT - 1;
        if (nextPlayerIndex > lastPlayerIndex) {
            //Returns the game to the first player
            nextPlayerIndex = FIRST_PLAYER_INDEX;
        }
        return nextPlayerIndex;
    }
}
