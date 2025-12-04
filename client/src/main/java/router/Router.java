package router;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import model.*;
import server.DataAccessException;
import server.ServerFacade;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.List;

public class Router {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private String authToken = "";
    private List<Integer> gameIDs = null;
    private int gameID = 0;

    public Router(int port, ServerMessageObserver observer){
        server = new ServerFacade(port);
        ws = new WebSocketFacade(port, observer);
    }

    public void register(String username, String password, String email)  throws DataAccessException{
        var ret = server.register(username, password, email);
        assert ret != null;
        authToken = ret;
    }

    public void login(String username, String password) throws DataAccessException{
        var ret = server.login(username, password);
        assert ret != null;
        authToken = ret;
    }

    public void logout() throws DataAccessException{
        server.logout();
        authToken = "";
    }

    public GameList listGames() throws DataAccessException{
        return server.listGames();
    }

    public int createGame(String name) throws DataAccessException{
        var gameID = server.createGame(name);
        var games = listGames();
        gameIDs = orderIDs(games);
        return gameIDs.indexOf(gameID) + 1;
    }

    public void joinGame(ChessGame.TeamColor color, int gameNum) throws DataAccessException{
        if (gameIDs == null){
            gameIDs = orderIDs(listGames());
        }
        if (gameNum <= 0 || gameNum > gameIDs.size()){
            throw new DataAccessException("There is not a game with that number.");
        }
        gameID = gameIDs.get(gameNum - 1);
        ws.join(authToken, gameID, color);
    }

    public ChessBoard getGame(int gameNum) throws DataAccessException{
        var data = listGames();
        if (gameNum <= 0 || gameNum > data.size()){
            throw new DataAccessException("There is not a game with that number.");
        }
        gameID = gameIDs.get(gameNum - 1);
        return data.get(gameID).game().getBoard();
    }

    public ChessBoard getGame() throws DataAccessException{
        if (gameID == 0){
            throw new DataAccessException("You have not joined the game");
        }
        var data = listGames();
        return data.get(gameID).game().getBoard();
    }

    public void leave(){
        ws.leave(authToken, gameID);
        gameID = 0;
    }

    public void move(ChessMove move){
        ws.move(authToken, gameID, move);
    }

    public void resign(){
        ws.resign(authToken, gameID);
    }

    private List<Integer> orderIDs(GameList list){
        var ret = new ArrayList<Integer>();
        for (GameData data: list.games()){
            ret.add(data.gameID());
        }
        return ret;
    }
}