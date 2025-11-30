package service;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.*;
import model.*;
import server.DataAccessException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameService {
    private final AuthDAO authMemory = new SQLAuthDAO();
    private final GameDAO gameMemory = new SQLGameDAO();
    public static int id = 1;

    public GameList listGames(String authToken) throws DataAccessException {
        validateRequest(authToken);
        return gameMemory.listGames();
    }

    public GameID createGame(CreateGameRequest request) throws DataAccessException{
        if(request.authToken() == null || request.gameName() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Missing authentication token or game name"));
        }
        var games = listGames(request.authToken());
        id = games.nextID(id);
        gameMemory.createGame(new GameData(id, null, null, request.gameName(), new ChessGame()));
        return new GameID(id ++);
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

    public GameData getGame(GetGameRequest request) throws DataAccessException{
        if(request.authToken() == null || request.gameID() < 1){
            throw new DataAccessException("400", new DataAccessException("Error: Missing authentication token or GameID"));
        }
        validateRequest(request.authToken());
        GameData gameData = gameMemory.getGame(request.gameID());
        if(gameData == null){
            throw new DataAccessException("400", new DataAccessException("Error: There is no game with that game number."));
        }
        return gameData;
    }

    public void leave(){
        //Calls UpdateGame
    }

    public ChessBoard move(MoveRequest request) throws DataAccessException{
        GameData data = getGame(new GetGameRequest(request.authToken(), request.gameID()));
        try {
            data.game().makeMove(request.move());
        } catch (InvalidMoveException ex) {
            throw new DataAccessException("400", new DataAccessException(ex.getMessage()));
        }
        gameMemory.updateGame(data);
        return data.game().getBoard();
    }

    public void resign(){
        //Calls UpdateGame, player turn is null
    }

    private AuthData validateRequest(String authToken) throws DataAccessException{
        AuthData data = authMemory.getAuth(authToken);
        if (data == null){
            throw new DataAccessException("401", new DataAccessException("Error: You are not authorized. Please log in."));
        }
        return data;
    }
}
