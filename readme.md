# Mancala Game

The mancala games are a family of two-player turn-based strategy board games played with small stones, beans, or seeds and rows of holes or pits in the earth, a board or other playing surface. The objective is usually to capture all or some set of the opponent's pieces.

### Technologies used

* Spring Boot
* Swagger
* Thymeleaf
* Bootstrap
* JQuery

## Usage

### Start Game

To view projects UI you can open link below in browser

```  
http://localhost:8080  
```

## Configurations

You can configure game players count, pits count, and stones quantity in each pit from WellKnown located in domain
package.

Example WellKnown data:

```java
public interface WellKnown {
    int TOTAL_PITS = 6;
    int STONES_IN_EACH_PIT = 6;
    int FIRST_PLAYER_INDEX = 0;
    String PLAYER_NAME_SUFFIX = "player";
    int Empty = 0;
    int PLAYERS_COUNT = 2;
}
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.
