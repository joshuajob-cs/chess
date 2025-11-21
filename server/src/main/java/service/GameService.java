package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import server.DataAccessException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameService {
    private final AuthDAO authMemory = new SQLAuthDAO();
    private final GameDAO gameMemory = new SQLGameDAO();
    public static int nextID = 1;

    public GameList listGames(String authToken) throws DataAccessException {
        validateRequest(authToken);
        return gameMemory.listGames();
    }

    public GameID createGame(CreateGameRequest request) throws DataAccessException{
        if(request.authToken() == null || request.gameName() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Missing authentication token or game name"));
        }
        validateRequest(request.authToken());
        gameMemory.createGame(new GameData(nextID, null, null, request.gameName(), new ChessGame()));
        return new GameID(nextID ++);
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException{
        if(request.authToken() == null || request.playerColor() == null || request.gameID() < 1){
            throw new DataAccessException("400", new DataAccessException("Error: Missing authentication token, player color, or gameID"));
        }
        AuthData auth = validateRequest(request.authToken());
        GameData gameData = gameMemory.getGame(request.gameID());
        if(gameData == null){
            throw new DataAccessException("400", new DataAccessException("Error: There is no game with that game number."));
        }
        if (request.playerColor() == WHITE && gameData.whiteUsername() == null){
            gameMemory.updateGame(new GameData(gameData.gameID(), auth.username(), gameData.blackUsername(), gameData.gameName(), gameData.game()));
        } else if (request.playerColor() == BLACK && gameData.blackUsername() == null) {
            gameMemory.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), auth.username(), gameData.gameName(), gameData.game()));
        }
        else{
            throw new DataAccessException("403", new DataAccessException("Error: Another player has already taken that spot. Choose an empty spot."));
        }
    }

    public void leave(){
        //Calls UpdateGame
    }

    public void move(){
        //Calls UpdateGame
        // If player turn is null, it means the game is over
    }

    public void resign(){
        //Calls UpdateGame, player turn is null
    }

    public AuthData validateRequest(String authToken) throws DataAccessException{
        AuthData data = authMemory.getAuth(authToken);
        if (data == null){
            throw new DataAccessException("401", new DataAccessException("Error: You are not authorized. Please log in."));
        }
        return data;
    }
}
