package server;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        Server server = new Server();
        server.run(8080);
        serverUrl = url;
    }

    public void clear(){
        var request = buildRequest("DELETE", "db", new HTTPData("", "",""));
        sendRequest(request);
    }

    public UserData register(UserData body){
        var request = buildRequest("POST", "user", new HTTPData("", "",""));
        var response = sendRequest(request);
        return handleResponse(response, UserData.class);
    }

    public LoginResponse login(LoginData data){
        var request = buildRequest("POST", "session", new HTTPData("", "",""));
        var response = sendRequest(request);
        return handleResponse(response, LoginResponse.class);
    }

    public void logout(){
        var request = buildRequest("DELETE", "session", new HTTPData("", "",""));
        // How do I set header?
        sendRequest(request);
    }

    public GameList listGames(){
        var request = buildRequest("GET", "GAME", new HTTPData("", "",""));
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public Map<String, Integer> createGame(GameName data){
        var request = buildRequest("POST", "game", new HTTPData("", "",""));
        var response = sendRequest(request);
        // How do I grab a map instead of a class that I made?
        return handleResponse(response, Map.class);
    }

    public void joinGame(ColorAndId data){
        // How do I set header?
        var request = buildRequest("PUT", "game", new HTTPData("", "",""));
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, HTTPData data) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(data.body()));
        if (data.header() != null) {
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
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass){
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
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
