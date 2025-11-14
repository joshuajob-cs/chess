package model;

//Must be named gameName instead of str, fromJson(json, GameName.class) looks for gameName
public record GameName(String gameName) {
    public String str(){
        return gameName;
    }
}
