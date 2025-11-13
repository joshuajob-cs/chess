import chess.*;
import ui.Client;

import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.WHITE_KING;

public class Main {
    public static void main(String[] args) {
        var user = new Client();
        System.out.println(WHITE_KING + "Let's play chess. Yippee!" + BLACK_KING);
        user.run();
    }
}