package model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record UserData(String username, String password, String email) implements SQLData{
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserData userData)) {
            return false;
        }
        return Objects.equals(email, userData.email) && Objects.equals(username, userData.username) && Objects.equals(password, userData.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }

    @Override
    public List<String> toSQL() {
        return Arrays.asList(username, password, email);
    }
}
