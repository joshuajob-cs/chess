package model;

public record GameID(int gameID) {
    public int num(){
        return gameID;
    }
}
