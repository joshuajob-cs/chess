package model;

import java.util.List;

public record GameList(List<GameData> games) {
    public List<GameData> get(){
        return games;
    }

    public GameData get(int i){
        return games.get(i);
    }

    public int size(){
        return games.size();
    }

    public GameData first(){
        return games.getFirst();
    }
}
