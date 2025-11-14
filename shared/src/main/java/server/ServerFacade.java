package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken = "";
    private List<Integer> gameIDs = null;

    public ServerFacade(int port){
        serverUrl = "http://localhost:" + port + "/";
    }

    public void clear() throws DataAccessException{
        var request = buildRequest("DELETE", "db", new HTTPData("", "",""));
        sendRequest(request);
        authToken = "";
    }

    public void register(String username, String password, String email)  throws DataAccessException{
        var body = new UserData(username, password, email);
        var request = buildRequest("POST", "user", new HTTPData(body, "",""));
        var response = sendRequest(request);
        var ret = handleResponse(response, LoginResponse.class);
        assert ret != null;
        authToken = ret.authToken();
    }

    public void login(String username, String password) throws DataAccessException{
        var body = new LoginData(username, password);
        var request = buildRequest("POST", "session", new HTTPData(body, "",""));
        var response = sendRequest(request);
        var ret = handleResponse(response, LoginResponse.class);
        assert ret != null;
        authToken = ret.authToken();
    }

    public void logout() throws DataAccessException{
        var request = buildRequest("DELETE", "session", new HTTPData("", "authorization", authToken));
        var response = sendRequest(request);
        handleResponse(response, null);
        authToken = "";
    }

    public GameList listGames() throws DataAccessException{
        var request = buildRequest("GET", "game", new HTTPData("", "authorization", authToken));
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public int createGame(String name) throws DataAccessException{
        if (gameIDs == null){
            gameIDs = orderIDs(listGames());
        }
        var body = new GameName(name);
        var request = buildRequest("POST", "game", new HTTPData(body, "authorization", authToken));
        var response = sendRequest(request);
        var ret = handleResponse(response, GameID.class);
        assert ret != null;
        gameIDs.add(ret.num());
        return ret.num();
    }

    public void joinGame(ChessGame.TeamColor color, int gameID) throws DataAccessException{
        if (gameIDs == null){
            gameIDs = orderIDs(listGames());
        }
        var body = new ColorAndId(color, gameID);
        var request = buildRequest("PUT", "game", new HTTPData(body, "authorization", authToken));
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private List<Integer> orderIDs(GameList list){
        var ret = new ArrayList<Integer>();
        for (GameData data: list.games()){
            ret.add(data.gameID());
        }
        return ret;
    }

    private HttpRequest buildRequest(String method, String path, HTTPData data) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(data.body()));
        if (!data.header().isEmpty()) {
            request.setHeader(data.header(), data.headerValue());
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException{
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.ConnectException e){
            throw new DataAccessException("You forgot to start the server.");
        } catch (java.io.IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException{
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            try {
                var error = new Gson().fromJson(response.body(), ErrorMessage.class);
                throw new DataAccessException(error.str());
            } catch(com.google.gson.JsonSyntaxException e){
                throw new RuntimeException(response.body());
            }
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
