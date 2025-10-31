package model;

import java.util.Objects;

public record UserData(String username, String password, String email) {
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
}
