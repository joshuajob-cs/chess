import server.Quitter;

public class Main {
    public static void main(String[] args) {
        var server = new Quitter();
        server.start();
    }
}