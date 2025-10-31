package model;

import java.util.Arrays;
import java.util.List;

public record AuthData(String authToken, String username) implements SQLData {

    @Override
    public List<String> toSQL() {
        return Arrays.asList(authToken, username);
    }
}
