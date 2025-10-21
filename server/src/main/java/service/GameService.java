package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import model.JoinGameRequest;

import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameService {
    private final AuthDAO authMemoryAccess = new MemoryAuthDAO();
    private final GameDAO gameMemoryAccess = new MemoryGameDAO();
    public static int nextID = 0;

    public List<GameData> listGames(String authToken) throws DataAccessException{
        validateRequest(authToken);
        return gameMemoryAccess.listGames();
    }

    public int createGame(CreateGameRequest request) throws DataAccessException{
        validateRequest(request.authToken());
        gameMemoryAccess.createGame(new GameData(nextID, null, null, request.gameName(), new ChessGame()));
        return nextID ++;
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException{
        AuthData auth = validateRequest(request.authToken());
        GameData gameData = gameMemoryAccess.getGame(request.gameID());
        if (request.color() == WHITE && gameData.whiteUsername() == null){
            gameMemoryAccess.updateGame(new GameData(gameData.gameID(), auth.username(), gameData.blackUsername(), gameData.gameName(), gameData.game()));
        } else if (request.color() == BLACK && gameData.blackUsername() == null) {
            gameMemoryAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), auth.username(), gameData.gameName(), gameData.game()));
        }
        else{
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
    }

    private AuthData validateRequest(String authToken) throws DataAccessException{
        AuthData data = authMemoryAccess.getAuth(authToken);
        if (data == null){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
        }
        return data;
    }
}
