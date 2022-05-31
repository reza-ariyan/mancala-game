$(document).ready(function () {
    const firstPlayer = "player0", secondPlayer = "player1", winner = "winner", enabled = "enabled",
        disabled = "disabled";

    $("#new-game").click(function () {
        localStorage.removeItem('gameId')
        $(`.large-pit`).removeClass(winner);
        initialize();
    });

    let initialize;
    (initialize = function () {
        $.get("/start", {gameId: localStorage.getItem('gameId')}).done(sync);
    })()

    function fillPits(player) {
        $(`#${player.name.value}`).empty();
        let mdSize = Math.floor(12 / player.smallPits.length);
        $.each(player.smallPits, function (index, data) {
            $(`#${player.name.value}`).append(`<div class=\"col-md-${mdSize} pit\">${data}</div>`)
        });
        $(`.${player.name.value}.large-pit > span`).html(player.largePit);
    }

    function sync(game) {
        localStorage.setItem('gameId', game.gameId);
        $.each(game.players, function (index, player) {
            fillPits(player);
        });
        togglePlayers(firstPlayer, secondPlayer, `player${game.currentPlayer}` !== firstPlayer);
        if (game.finished) {
            $(".pit").removeClass(enabled).addClass(disabled);
            $(`.player${game.winner}.large-pit`).addClass(winner);
        }
    }

    function togglePlayers(player, opponent, revert) {
        if (revert) return togglePlayers(opponent, player);
        $(`#${player} .pit`).addClass(enabled).removeClass(disabled);
        $(`#${opponent} .pit`).addClass(disabled).removeClass(enabled);
    }

    $(document).on('click', '.pit', function () {
        if ($(this).hasClass(disabled)) return;
        let data = {
            gameId: localStorage.getItem('gameId'),
            player: $(this).parent().attr('value'),
            pitNumber: $(this).index()
        };
        $.post({
            url: '/sow',
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8'
        }).done(sync);
    });
});
