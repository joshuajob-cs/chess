import chess.*;
import server.Server;
import ui.Client;

import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.WHITE_KING;

public class Main {
    public static void main(String[] args) {
        var server = new Server();
        var port = server.run(0);
        var user = new Client(port);
        System.out.println(WHITE_KING + "Let's play chess. Yippee!" + BLACK_KING);
        user.run();
    }
}