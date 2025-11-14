package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken = "";

    public ServerFacade() {
        Server server = new Server();
        server.run(8080);
        serverUrl = "http://localhost:8080/";
    }

    public ServerFacade(int port){
        serverUrl = "http://localhost:" + port + "/";
    }

    public void clear(){
        var request = buildRequest("DELETE", "db", new HTTPData("", "",""));
        sendRequest(request);
        authToken = "";
    }

    public void register(UserData body)  throws DataAccessException{
        var request = buildRequest("POST", "user", new HTTPData(body, "",""));
        var response = sendRequest(request);
        var ret = handleResponse(response, LoginResponse.class);
        assert ret != null;
        authToken = ret.authToken();
    }

    public void login(LoginData body) throws DataAccessException{
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

    public void createGame(GameName body) throws DataAccessException{
        var request = buildRequest("POST", "game", new HTTPData(body, "authorization", authToken));
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void joinGame(ColorAndId body) throws DataAccessException{
        var request = buildRequest("PUT", "game", new HTTPData(body, "authorization", authToken));
        var response = sendRequest(request);
        handleResponse(response, null);
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

    private HttpResponse<String> sendRequest(HttpRequest request){
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch(java.io.IOException | java.lang.InterruptedException e){
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
