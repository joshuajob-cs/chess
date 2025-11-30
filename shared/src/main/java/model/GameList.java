package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public int nextID(int i){
        Set<Integer> ids = new HashSet<>();
        for (GameData game:games){
            ids.add(game.gameID());
        }
        while (ids.contains(i)){
            i++;
        }
        return i;
    }
}
