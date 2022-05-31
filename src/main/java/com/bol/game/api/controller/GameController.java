package com.bol.game.api.controller;

import com.bol.game.domain.contract.GameService;
import com.bol.game.domain.model.Game;
import com.bol.game.domain.model.Sow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public Game getStart(@RequestParam String gameId) {
        var validatedGameId = gameService.resolveGameId(gameId);
        return gameService.getGame(validatedGameId);
    }

    @PostMapping("/sow")
    public Game getSow(@RequestBody @Valid Sow sow) {
        return gameService.sow(sow);
    }
}
