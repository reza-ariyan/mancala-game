package com.bol.game.integration;

import com.bol.game.api.controller.GameController;
import com.bol.game.application.services.GameServiceImpl;
import com.bol.game.domain.WellKnown;
import com.bol.game.domain.model.Game;
import com.bol.game.domain.model.Player;
import com.bol.game.domain.model.Sow;
import com.bol.game.domain.valueObjects.Name;
import com.bol.game.infrastructure.Repository.GameHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class IntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private GameServiceImpl gameServiceImpl;

    @InjectMocks
    private GameController gameController;

    private final static UUID GAME_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        //Build a MockMvc instance
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        var games = new GameHashMap();
        games.put(GAME_ID, this.initiate());
        ReflectionTestUtils.setField(gameServiceImpl, "games", games);
        when(gameServiceImpl.resolveGameId(any())).thenCallRealMethod();
        when(gameServiceImpl.getGame(any())).thenCallRealMethod();
        when(gameServiceImpl.sow(any())).thenCallRealMethod();
    }

    @Test
    public void startGameReturnsNewGameId_WhenPassingAnInvalidGameId() throws Exception {
        // Arrange
        final String INVALID_GAME_ID = new UUID(0L, 0L).toString();
        var okStatus = status().isOk();

        // Act
        var resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/start")
                        .queryParam("gameId", INVALID_GAME_ID)
                        .accept(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(okStatus);
        resultActions.andExpect(result -> assertFalse("This game Id exists",
                MockMvcResultMatchers.jsonPath("$.gameId").value(GAME_ID).equals(GAME_ID)));
    }

    @Test
    public void startGameReturnsTheExistingGame_WhenPassingTheExistingGameId() throws Exception {
        // Arrange
        final String EXISTING_GAME_ID = GAME_ID.toString();
        final ResultMatcher OK_STATUS = status().isOk();

        // Act
        var resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/start")
                        .queryParam("gameId", EXISTING_GAME_ID)
                        .accept(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(OK_STATUS);
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.gameId").value(GAME_ID.toString()));
    }

    @Test
    public void sowShouldSwitchPlayers_WhenItIsInvokedAndThereIsNoPlayAgain() throws Exception {
        // Arrange
        final ResultMatcher OK_STATUS = status().isOk();
        final int THE_NEXT_PLAYER_INDEX = 1;
        var sow = Sow.builder().gameId(GAME_ID).player(WellKnown.FIRST_PLAYER_INDEX).pitNumber(4).build();

        // Act
        var resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/sow")
                        .content(asJsonString(sow))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(OK_STATUS);
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.gameId").value(GAME_ID.toString()));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.currentPlayer").value(THE_NEXT_PLAYER_INDEX));
    }

    //Mocking Data
    private Game initiate() {
        List<Player> players = new ArrayList<>();
        for (var i = WellKnown.FIRST_PLAYER_INDEX; i < WellKnown.PLAYERS_COUNT; i++) {
            var pits = new int[WellKnown.TOTAL_PITS];
            Arrays.fill(pits, WellKnown.STONES_IN_EACH_PIT);
            var player = Player.builder().smallPits(pits).name(new Name(Name.DEFAULT + i)).build();
            players.add(player);
        }

        return Game.builder()
                .gameId(GAME_ID)
                .currentPlayer(WellKnown.FIRST_PLAYER_INDEX)
                .players(players)
                .build();
    }

    private static String asJsonString(final Object obj) {
        try {
            var mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
