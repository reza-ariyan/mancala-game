package com.bol.game.service;

import com.bol.game.application.services.GameServiceImpl;
import com.bol.game.domain.WellKnown;
import com.bol.game.domain.model.Game;
import com.bol.game.domain.model.Sow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
//NOT involving Spring, so don't need to load unneeded Spring stuff (ES)

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    //Create an instance of the class and injects the mocks
    @InjectMocks
    GameServiceImpl gameServiceImpl;

    @Test
    public void resolveGameIdShouldReturnANewValidGameId_WhenPassingAnInvalidGameIdToResolveGameId() {
        // Arrange
        final String unexpected = "ThisIsInvalidGameId";

        // Act
        var actual = gameServiceImpl.resolveGameId(unexpected).toString();

        // Assert
        assertNotEquals(unexpected, actual);
    }

    @Test
    public void initializeShouldCreateANewGame_WhenItIsInvoked() {
        // Arrange
        final UUID GAME_ID = UUID.randomUUID();

        // Act
        var actual = gameServiceImpl.initialize(GAME_ID);
        var expected = gameServiceImpl.getGame(actual.getGameId());

        // Assert
        assertNotNull(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void getGameShouldReturnTheGame_IfGameIdExistInRepository() throws Exception {
        // Arrange
        final UUID GAME_ID = UUID.randomUUID();
        var game = gameServiceImpl.initialize(GAME_ID);

        // Act
        var actual = gameServiceImpl.getGame(GAME_ID).getGameId();
        var expected = game.getGameId();

        // Assert
        assertEquals(expected, actual);
    }


    @Test
    public void sowShouldChangeTheCurrentPlayerAndAddToLargePit_AfterOneRoundOfSow() throws Exception {
        // Arrange
        final UUID GAME_ID = UUID.randomUUID();
        final int SECOND_PIT_INDEX = 2;
        var sow = Sow.builder().gameId(GAME_ID).player(WellKnown.FIRST_PLAYER_INDEX).pitNumber(SECOND_PIT_INDEX).build();
        var SECOND_PLAYER_INDEX = 1;

        // Act
        Game game = gameServiceImpl.sow(sow);
        var actualCurrentPlayer = game.getCurrentPlayer();
        var expectedCurrentPlayer = SECOND_PLAYER_INDEX;
        var actualLargePit = game.players.get(WellKnown.FIRST_PLAYER_INDEX).getLargePit();
        var expectedLargePit = 1;


        // Assert
        assertFalse(game.finished);
        assertEquals(expectedCurrentPlayer, actualCurrentPlayer);
        assertEquals(expectedLargePit, actualLargePit);
    }

    @Test
    public void currentPlayerShouldGetAnotherTurn_WhenLastStoneLandsInHisOwnLargePit() throws Exception {
        // Arrange
        final UUID GAME_ID = UUID.randomUUID();
        final int FIRST_PIT_INDEX = 0;
        var sow = Sow.builder().gameId(GAME_ID).player(WellKnown.FIRST_PLAYER_INDEX).pitNumber(FIRST_PIT_INDEX).build();

        // Act
        Game game = gameServiceImpl.sow(sow);
        var actualCurrentPlayer = game.getCurrentPlayer();
        var expectedCurrentPlayer = WellKnown.FIRST_PLAYER_INDEX;
        var actualLargePit = game.players.get(WellKnown.FIRST_PLAYER_INDEX).getLargePit();
        var expectedLargePit = 1;


        // Assert
        assertFalse(game.finished);
        assertEquals(expectedCurrentPlayer, actualCurrentPlayer);
        assertEquals(expectedLargePit, actualLargePit);
    }
}
