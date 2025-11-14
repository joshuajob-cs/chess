package model;

public record ErrorMessage(String message) {
    public String str(){
        return message;
    }
}
